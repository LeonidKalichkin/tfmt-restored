package ui;

import java.util.Locale;
import java.util.Observable;

import javax.swing.UIManager;

import tweaker.Configuration;

public class UserInterface extends Observable
{
  private MainWindow mainWindow;
  private ConsoleOutput console;

  public UserInterface(Configuration config)
  {
    final String language = config.getLanguageCode();
    if (language != null)
    {
      Locale.setDefault(new Locale(language));
    }

    if (config.isCuiEnabled())
    {
      console = new ConsoleOutput(config);
      super.addObserver(console);
    }

    if (config.isGuiEnabled())
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (final Throwable e)
      {}

      mainWindow = new MainWindow(this, config);
      super.addObserver(mainWindow);
      mainWindow.setVisible(true);
    }
  }

  public void process(Event event)
  {
    super.setChanged();
    super.notifyObservers(event);
  }
}
