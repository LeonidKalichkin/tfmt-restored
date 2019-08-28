package extensions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

import localization.Phrase;
import transformice.connection.MasterConnectionProcessor;
import transformice.protocol.Packet;
import transformice.protocol.PacketDef;
import transformice.protocol.PacketProcessor;
import transformice.protocol.Response;
import transformice.protocol.packets.PublicMessageResponse;
import tweaker.Configuration;
import ui.UserInterface;
import ui.events.AntiSpamExtensionStatusEvent;
import ui.events.AntiSpamExtensionStatusEvent.Status;
import ui.events.ErrorEvent;

public class AntiSpamExtension
{
  private final MasterConnectionProcessor connection;
  private final UserInterface ui;
  private final Configuration config;
  private PacketProcessor publicChatMessageProcessor;

  private final ArrayList<Pattern> patterns = new ArrayList<>();
  private boolean enabled;
  private String lastPlayerName;
  private String lastMessage;

  public AntiSpamExtension(MasterConnectionProcessor connection,
      UserInterface ui, Configuration config)
  {
    this.connection = connection;
    this.ui = ui;
    this.config = config;
    enabled = config.isAntiSpamEnabled();

    loadFilters();

    initProcessors();
    if (enabled)
    {
      registerProcessors();
    }
  }

  public void loadFilters()
  {
    patterns.clear();

    try (Scanner in = new Scanner(
        new URL(config.getFilterListUrl()).openStream()))
    {
      while (in.hasNextLine())
      {
        patterns.add(Pattern.compile(in.nextLine()));
      }
    } catch (final IOException e)
    {
      ui.process(new ErrorEvent(Phrase.ErrorLoadingFilterList.toString()
          + System.lineSeparator() + e.toString(), false));
    }

    for (final String filter : config.getUserFilterList().split("[\r\n]+"))
    {
      if (!filter.isEmpty())
      {
        patterns.add(Pattern.compile(filter));
      }
    }
  }

  public boolean filter(String playerName, String message)
  {
    boolean isSpam = false;

    lastPlayerName = playerName;
    lastMessage = message;

    if (message.length() > 255 || message.contains("\n")
        || message.contains("\r") || message.contains("&#10")
        || message.contains("&#13"))
    {
      isSpam = true;
    }

    if (!isSpam)
    {
      final Iterator<Pattern> it = patterns.iterator();
      while (!isSpam && it.hasNext())
      {
        if (it.next().matcher(message.toLowerCase()).find())
        {
          isSpam = true;
          ui.process(new AntiSpamExtensionStatusEvent(this,
              Status.MESSAGE_FILTERED));
        }
      }
    }

    return isSpam;
  }

  private void initProcessors()
  {
    publicChatMessageProcessor = new PacketProcessor()
    {
      @Override
      public Packet process(Packet packet) throws IOException
      {
        final PublicMessageResponse response = new PublicMessageResponse(
            (Response) packet);
        if (filter(response.getPlayerName(), response.getMessage()))
        {
          return null;
        }
        else
        {
          return response;
        }
      }
    };
  }

  private void registerProcessors()
  {
    connection.registerProcessor(publicChatMessageProcessor,
        PacketDef.PublicChatMessageResponse);
  }

  private void unregisterProcessors()
  {
    connection.unregisterProcessor(publicChatMessageProcessor,
        PacketDef.PublicChatMessageResponse);
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
    if (enabled)
    {
      registerProcessors();
    }
    else
    {
      unregisterProcessors();
    }
  }

  public String getLastPlayerName()
  {
    return lastPlayerName;
  }

  public String getLastMessage()
  {
    return lastMessage;
  }

  public MasterConnectionProcessor getConnection()
  {
    return connection;
  }

}
