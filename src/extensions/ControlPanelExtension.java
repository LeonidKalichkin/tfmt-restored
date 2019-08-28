package extensions;

import java.io.IOException;

import transformice.connection.MasterConnectionProcessor;
import transformice.protocol.Packet;
import transformice.protocol.PacketDef;
import transformice.protocol.PacketProcessor;
import transformice.protocol.Request;
import transformice.protocol.packets.BindKeyResponse;
import transformice.protocol.packets.KeyPressedRequest;
import tweaker.Configuration;
import ui.UserInterface;
import ui.events.ControlPanelExtensionStatusEvent;
import ui.events.ControlPanelExtensionStatusEvent.Status;

public class ControlPanelExtension
{
  private final MasterConnectionProcessor connection;
  private final UserInterface ui;
  private final Configuration config;

  private final BindKeyResponse bindResponse;

  private int hotkey;
  private PacketProcessor hotkeyProcessor;
  private PacketProcessor enteredRoomProcessor;

  public ControlPanelExtension(MasterConnectionProcessor connection,
      UserInterface ui, Configuration config)
  {
    this.connection = connection;
    this.ui = ui;
    this.config = config;
    hotkey = config.getControlPanelHotkey();
    connection.getPacketDefResolver().setControlPanelHotkey(hotkey);
    bindResponse = new BindKeyResponse(hotkey, true, true,
        connection.getSession());

    initProcessors();
    registerProcessors();
  }

  private void registerProcessors()
  {
    connection.registerProcessor(hotkeyProcessor, PacketDef.KeyPressedRequest);
    connection.registerProcessor(enteredRoomProcessor,
        PacketDef.EnteredRoomOldResponse);
  }

  private void initProcessors()
  {
    hotkeyProcessor = new PacketProcessor()
    {
      @Override
      public Packet process(Packet packet) throws IOException
      {
        final KeyPressedRequest request = new KeyPressedRequest(
            (Request) packet);
        if (request.getKeyCode() == hotkey)
        {
          ui.process(new ControlPanelExtensionStatusEvent(
              ControlPanelExtension.this, Status.SHOW));
        }
        return packet;
      }
    };

    enteredRoomProcessor = new PacketProcessor()
    {
      @Override
      public Packet process(Packet packet) throws IOException
      {
        connection.getSatelliteConnection().sendClient(packet);
        connection.getSatelliteConnection().sendClient(bindResponse);
        return null;
      }
    };
  }

  public void setHotkey(int hotkey)
  {
    this.hotkey = hotkey;
    connection.getPacketDefResolver().setControlPanelHotkey(hotkey);

    bindResponse.setBind(false);
    connection.getSatelliteConnection().sendClient(bindResponse);

    bindResponse.setKeyCode(hotkey);
    bindResponse.setBind(true);
    connection.getSatelliteConnection().sendClient(bindResponse);
  }

  public MasterConnectionProcessor getConnection()
  {
    return connection;
  }
}
