package tweaker;

import java.io.File;
import java.io.IOException;

import localization.Phrase;
import proxy.Socks4ProxyServer;
import transformice.connection.TransformiceConnectionProcessor;
import ui.UserInterface;

public class Main
{
  public static final String CONFIG_NAME = "tfmt.conf";
  public static final File CLASSPATH = new File(Main.class
      .getProtectionDomain().getCodeSource().getLocation().getPath());
  public static final File APPLICATION_DATA = CLASSPATH.getParentFile();

  private static Socks4ProxyServer proxyServer;

  public static void main(String[] args)
  {
    final Configuration config =
        new Configuration(new File(APPLICATION_DATA, CONFIG_NAME));
    try
    {
      config.load();
    } catch (final IOException e)
    {
      System.err.println(Phrase.ErrorLoadingConfiguration.toString()
          + System.lineSeparator() + e.toString());
      System.exit(-1);
    }

    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          config.save();
        } catch (final IOException e)
        {
          System.err.println(Phrase.ErrorSavingConfiguration.toString()
              + System.lineSeparator() + e.toString());
        }
      }
    });

    final UserInterface ui = new UserInterface(config);

    proxyServer =
        new Socks4ProxyServer(ui, config, TransformiceConnectionProcessor
            .getTransformiceConnectionProcessor());
    new Thread(proxyServer).start();

    Updater.init(ui, config);
  }

  public static Socks4ProxyServer getProxyServer()
  {
    return proxyServer;
  }
}
