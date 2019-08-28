package ui;

import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;

import localization.Phrase;
import tweaker.Configuration;
import ui.events.AntiSpamExtensionStatusEvent;
import ui.events.ControlPanelExtensionStatusEvent;
import ui.events.ErrorEvent;
import ui.events.MasterConnectionStatusEvent;
import ui.events.ProxyServerStatusEvent;
import ui.events.UpdateProgressEvent;
import ui.events.UpdateProgressEvent.UpdateProgress;

public class ConsoleOutput implements Observer, EventProcessor
{
  private final Configuration config;
  private static final PrintStream out = System.out;
  private static final PrintStream err = System.err;

  private Thread progressBar;

  public ConsoleOutput(Configuration config)
  {
    this.config = config;
  }

  @Override
  public void update(Observable o, Object arg)
  {
    ((Event) arg).accept(this);
  }

  @Override
  public void process(ProxyServerStatusEvent event)
  {
    switch (event.getStatus())
    {
    case FAILED:
    break;
    case STARTED:
      out.println(String.format(Phrase.StartingProxyServer.toString(), event
          .getProxyServer().getPort()));
    break;
    case STOPPED:
      out.println(Phrase.StoppingProxyServer.toString());
    break;
    default:
    break;

    }
  }

  @Override
  public void process(final UpdateProgressEvent event)
  {
    final UpdateProgress progress = event.getProgress();
    switch (progress.getStatus())
    {
    case CHECKING_FOR_UPDATES:
      out.println(Phrase.CheckingForUpdates);
    break;
    case PERFORMING:
      out.println(Phrase.Downloading);

      progressBar = new Thread(new Runnable()
      {
        @Override
        public void run()
        {
          while (!Thread.currentThread().isInterrupted())
          {
            final StringBuilder bar = new StringBuilder("\033[A\r[");
            final int percents = (int) Math.floor(progress.getProgress() * 100
                / progress.getLength());
            for (int i = 0; i < percents / 2; ++i)
            {
              bar.append('#');
            }
            for (int i = percents / 2; i < 50; ++i)
            {
              bar.append(' ');
            }
            bar.append("] ").append(percents).append('%');
            out.println(bar.toString());

            try
            {
              Thread.sleep(50);
            } catch (final InterruptedException e)
            {
              break;
            }
          }
        }
      });
      progressBar.start();
    break;
    case FAILED:
      if (progressBar != null)
      {
        progressBar.interrupt();
      }
      progressBar = null;
      out.print("\033[2A\r\033[J");
      out.println(Phrase.UpdateFailed);
    break;
    case FINISHED:
      progressBar.interrupt();
      progressBar = null;
      out.print("\033[2A\r\033[J");
      out.println(Phrase.UpdateFinished);
    break;
    case JUST_UPDATED:
      out.println(Phrase.JustUpdated);
    break;
    case NO_UPDATES:
      out.println(Phrase.NoUpdates);
    break;
    case STOPPED:
      progressBar.interrupt();
      progressBar = null;
      out.println(Phrase.UpdateStopped);
    break;
    case UPDATE_AVAILABLE:
      out.println(Phrase.UpdateAvailable);
    break;
    default:
    break;

    }
  }

  @Override
  public void process(ErrorEvent event)
  {
    err.println(event.getMessage());
  }

  @Override
  public void process(AntiSpamExtensionStatusEvent event)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void process(MasterConnectionStatusEvent event)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void process(ControlPanelExtensionStatusEvent event)
  {
    // TODO Auto-generated method stub

  }
}
