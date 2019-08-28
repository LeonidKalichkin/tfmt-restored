package extensions;

import java.io.IOException;

import transformice.connection.MasterConnectionProcessor;
import transformice.protocol.OldRequest;
import transformice.protocol.Packet;
import transformice.protocol.PacketDef;
import transformice.protocol.PacketProcessor;
import transformice.protocol.packets.LoginOldRequest;
import tweaker.Configuration;
import ui.UserInterface;

public class AutoLoginExtension
{
  private final MasterConnectionProcessor connection;
  private final UserInterface ui;
  private final Configuration config;
  private final boolean enabled;

  private PacketProcessor loginRequestProcessor;
  private PacketProcessor loggedInResponseProcessor;
  protected String lastNickname;
  protected String lastPasswordHash;
  protected String lastLoginUrl;
  protected String lastLoginBase64;

  public AutoLoginExtension(MasterConnectionProcessor connection,
      UserInterface ui, Configuration config)
  {
    this.connection = connection;
    this.ui = ui;
    this.config = config;
    enabled = config.isAutoLoginEnabled();

    initProcessors();

    if (enabled)
    {
      registerProcessors();
    }
  }

  private void registerProcessors()
  {
    connection.registerProcessor(loginRequestProcessor,
        PacketDef.LoginOldRequest);
    connection.registerProcessor(loggedInResponseProcessor,
        PacketDef.LoggedInOldResponse);
  }

  private void initProcessors()
  {
    loginRequestProcessor = new PacketProcessor()
    {
      @Override
      public Packet process(Packet packet) throws IOException
      {
        final LoginOldRequest request = new LoginOldRequest((OldRequest) packet);
        if (request.getPasswordHash().isEmpty())
        {
          if (config.getLastNickname() != null)
          {
            request.setNickname(config.getLastNickname());
            request.setPasswordHash(config.getLastPasswordHash());
            request.setUrl(config.getLastLoginUrl());
            request.setBase64(config.getLastLoginBase64());
          }
        }
        else
        {
          lastNickname = request.getNickname();
          lastPasswordHash = request.getPasswordHash();
          lastLoginUrl = request.getUrl();
          lastLoginBase64 = request.getBase64();
        }
        return request;
      }
    };

    loggedInResponseProcessor = new PacketProcessor()
    {

      @Override
      public Packet process(Packet packet) throws IOException
      {
        config.setLastNickname(lastNickname);
        config.setLastPasswordHash(lastPasswordHash);
        config.setLastLoginUrl(lastLoginUrl);
        config.setLastLoginBase64(lastLoginBase64);
        return packet;
      }
    };
  }

}
