package tweaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import localization.Phrase;
import proxy.Socks4ProxyServer;
import ui.UserInterface;
import ui.events.ErrorEvent;
import ui.events.UpdateProgressEvent;
import ui.events.UpdateProgressEvent.Status;
import ui.events.UpdateProgressEvent.UpdateProgress;

public class Updater
{
  public enum UpdateCheckStrategy
  {
    DO_NOT_CHECK("DoNotCheck", -1), CHECK_AT_STARTUP("CheckAtStartup", -1),
    CHECK_EVERY_5_MINUTES("CheckEvery5Minutes", 5 * 60 * 1000),
    CHECK_EVERY_30_MINUTES("CheckEvery30Minutes", 30 * 60 * 1000),
    CHECK_EVERY_2_HOURS("CheckEvery2Hours", 2 * 60 * 60 * 1000);

    private final String name;
    private final long period;

    private UpdateCheckStrategy(String name, long period)
    {
      this.name = name;
      this.period = period;
    }

    public String getName()
    {
      return name;
    }

    public long getPeriod()
    {
      return period;
    }
  }

  private static final String FILES_URL =
      "https://raw.github.com/agath/tfmt/master/dist/files.properties";
  private static final String REPOSITORY_URL =
      "https://raw.github.com/agath/tfmt/master";
  
  private static final Timer UPDATE_TIMER = new Timer();

  public static void init(UserInterface ui, Configuration config)
  {
    setUpdateCheckStrategy(ui, config);

    if (config.isJustUpdated())
    {
      ui.process(new UpdateProgressEvent(
          new UpdateProgress(Status.JUST_UPDATED)));

      config.setJustUpdated(false);
    }
    else if (config.getUpdateCheckStrategy() == UpdateCheckStrategy.CHECK_AT_STARTUP)
    {
      run(ui, config);
    }
  }

  public static void run(UserInterface ui, Configuration config)
  {
    UpdateProgress checkProgress =
        new UpdateProgress(Status.CHECKING_FOR_UPDATES);
    ui.process(new UpdateProgressEvent(checkProgress));

    UpdateProgress checkResult = checkForUpdates(checkProgress);
    ui.process(new UpdateProgressEvent(checkResult));

    if (checkResult.getStatus() == Status.UPDATE_AVAILABLE)
    {
      UPDATE_TIMER.cancel();

      if (config.isUpdateAutomatically())
      {
        UpdateProgress updateProgress = new UpdateProgress(Status.PERFORMING);
        ui.process(new UpdateProgressEvent(updateProgress));

        UpdateProgress updateResult = update(updateProgress, checkResult);
        ui.process(new UpdateProgressEvent(updateResult));

        if (updateResult.getStatus() == Status.FINISHED)
        {
          reload(ui, config);
        }
      }
    }
  }

  public static void reload(UserInterface ui, Configuration config)
  {
    try
    {
      config.save();
    } catch (final IOException e)
    {
      ui.process(new ErrorEvent(Phrase.ErrorSavingConfiguration.toString()
          + System.lineSeparator() + e.toString(), false));
    }

    final Socks4ProxyServer proxyServer = Main.getProxyServer();
    if (proxyServer != null)
    {
      proxyServer.stop();
    }

    int exitCode = -1;
    try
    {
      final ArrayList<String> commands = new ArrayList<>();
      commands.add(System.getProperty("java.home") + File.separator + "bin"
          + File.separator + "java");
      commands.add("-cp");
      commands.add(Main.CLASSPATH.getAbsolutePath());
      commands.add(Main.class.getName());

      final ProcessBuilder builder = new ProcessBuilder(commands);
      builder.inheritIO();

      final Process process = builder.start();

      Runtime.getRuntime().addShutdownHook(new Thread()
      {
        @Override
        public void run()
        {
          process.destroy();
        }
      });

      exitCode = process.waitFor();
    } catch (final Exception e)
    {
      ui.process(new ErrorEvent(Phrase.ErrorReloading.toString()
          + System.lineSeparator() + e.toString(), true));
    }

    System.exit(exitCode);
  }

  public static UpdateProgress checkForUpdates(UpdateProgress progress)
  {
    progress.setWork(true);
    Properties sourceFiles = null, destFiles = null;
    final ArrayList<String> filesToCopy = new ArrayList<>();
    final ArrayList<String> filesToRemove = new ArrayList<>();
    long length = 0;

    try
    {
      final HttpsURLConnection connection =
          (HttpsURLConnection) new URL(FILES_URL).openConnection();

      File temp = File.createTempFile("tfmt", null);

      try (InputStream in = connection.getInputStream();
          FileOutputStream tempOut = new FileOutputStream(temp))
      {
        if (connection.getResponseCode() / 100 == 2)
        {
          final byte[] buf = new byte[4096];
          int bytesRead = 0;
          while (progress.isWork() && (bytesRead = in.read(buf)) != -1)
          {
            tempOut.write(buf, 0, bytesRead);
          }
        }
        else
        {
          return new UpdateProgress(Status.UPDATE_CHECK_FAILED);
        }
      }

      if (progress.isWork())
      {
        sourceFiles = new Properties();
        try (FileInputStream is = new FileInputStream(temp);
            InputStreamReader reader = new InputStreamReader(is, "UTF-8"))
        {
          sourceFiles.load(reader);
        }

        destFiles = new Properties();
        try (
            FileInputStream is =
                new FileInputStream(new File(Main.APPLICATION_DATA,
                    "files.properties"));
            InputStreamReader reader = new InputStreamReader(is, "UTF-8"))
        {
          destFiles.load(reader);
        }

        for (Object key : sourceFiles.keySet())
        {
          if (!destFiles.containsKey(key))
          {
            filesToCopy.add((String) key);
            length +=
                Long.parseLong(((String) sourceFiles.get(key)).split(" ")[1]);
          }
        }

        for (Object key : destFiles.keySet())
        {
          if (!sourceFiles.containsKey(key))
          {
            filesToRemove.add((String) key);
          }
        }
      }

      temp.delete();
    } catch (IOException e)
    {
      return new UpdateProgress(Status.UPDATE_CHECK_FAILED);
    }

    if (progress.isWork())
    {
      if (!(filesToCopy.isEmpty() || filesToRemove.isEmpty()))
      {
        UpdateProgress result = new UpdateProgress(Status.UPDATE_AVAILABLE);
        result.setFilesToCopy(filesToCopy);
        result.setFilesToRemove(filesToRemove);
        result.setLength(length);
        result.setFiles(sourceFiles);
        return result;
      }
      else
      {
        return new UpdateProgress(Status.NO_UPDATES);
      }
    }
    else
    {
      return new UpdateProgress(Status.CHECK_STOPPED);
    }
  }

  public static UpdateProgress update(UpdateProgress progress,
      UpdateProgress checkResult)
  {
    ArrayList<String> filesToCopy = checkResult.getFilesToCopy();
    ArrayList<String> filesToRemove = checkResult.getFilesToRemove();
    progress.setLength(checkResult.getLength());

    try
    {
      for (String fileName : filesToCopy)
      {
        if (progress.isWork())
        {
          File file = new File(Main.APPLICATION_DATA, fileName);
          URL url = new URL(new URL(REPOSITORY_URL), fileName);
          final HttpsURLConnection connection =
              (HttpsURLConnection) url.openConnection();

          if (connection.getResponseCode() / 100 == 2)
          {
            try (InputStream in = connection.getInputStream();
                FileOutputStream out = new FileOutputStream(file))
            {
              final byte[] buf = new byte[4096];
              int bytesRead = 0;
              while (progress.isWork() && (bytesRead = in.read(buf)) != -1)
              {
                out.write(buf, 0, bytesRead);
                progress.setLength(progress.getLength() + bytesRead);
              }
            }
          }
          else
          {
            return new UpdateProgress(Status.FAILED);
          }
        }
      }
    } catch (IOException e)
    {
      return new UpdateProgress(Status.FAILED);
    }

    if (progress.isWork())
    {
      for (String fileName : filesToRemove)
      {
        File file = new File(Main.APPLICATION_DATA, fileName);
        file.delete();
      }

      try (
          FileOutputStream out =
              new FileOutputStream(new File(Main.APPLICATION_DATA,
                  "files.properties"));
          OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8"))
      {
        checkResult.getFiles().store(writer, null);
      } catch (IOException e)
      {
        return new UpdateProgress(Status.FAILED);
      }

      return new UpdateProgress(Status.FINISHED);
    }
    else
    {
      return new UpdateProgress(Status.STOPPED);
    }
  }

  public static void setUpdateCheckStrategy(final UserInterface ui, final Configuration config)
  {
    final UpdateCheckStrategy strategy = config.getUpdateCheckStrategy();

    final long period = strategy.getPeriod();

    if (period != -1)
    {
      UPDATE_TIMER.schedule(new TimerTask()
      {

        @Override
        public void run()
        {
          Updater.run(ui, config);
        }
      }, strategy.getPeriod(), strategy.getPeriod());
    }
    else
    {
      UPDATE_TIMER.cancel();
    }
  }
}
