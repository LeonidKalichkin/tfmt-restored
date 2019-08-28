package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import tweaker.Configuration;
import ui.UserInterface;
import ui.events.ErrorEvent;
import ui.events.ProxyServerStatusEvent;
import ui.events.ProxyServerStatusEvent.Status;

public class Socks4ProxyServer implements Runnable
{
  private static final byte[] SUCCESS_REPLY = {0, 90, 0, 0, 0, 0, 0, 0};
  private static final byte[] FAILURE_REPLY = {0, 91, 0, 0, 0, 0, 0, 0};

  private final UserInterface ui;
  private final Configuration config;
  private final ConnectionProcessor connectionProcessor;
  private ServerSocket proxyServerSocket;

  private volatile boolean work;
  private int port;

  public Socks4ProxyServer(UserInterface ui, Configuration config,
      ConnectionProcessor connectionProcessor)
  {
    this.ui = ui;
    this.config = config;
    this.connectionProcessor = connectionProcessor;
    ui.process(new ProxyServerStatusEvent(this, Status.CREATED));
  }

  @Override
  public void run()
  {
    work = true;
    port = config.getProxyServerPort();
    try (ServerSocket proxyServer = new ServerSocket(port))
    {
      proxyServerSocket = proxyServer;
      ui.process(new ProxyServerStatusEvent(this, Status.STARTED));

      while (work)
      {
        final Socket clientSocket = proxyServer.accept();
        new Thread()
        {
          @Override
          public void run()
          {
            try (InputStream cin = clientSocket.getInputStream();
                OutputStream cout = clientSocket.getOutputStream())
            {
              final int version = cin.read(), action = cin.read();
              if (version == 4 && action == 1)
              {
                listenClientSocket(clientSocket, cin, cout);
              }
              else
              {
                cout.write(FAILURE_REPLY);
              }
            } catch (final IOException e)
            {
              ui.process(new ErrorEvent(ErrorEvent.Error.ReadingClientSocket, e));
            }
          }
        }.start();
      }
    } catch (final IOException e2)
    {
      if (work)
      {
        ui.process(new ProxyServerStatusEvent(Socks4ProxyServer.this, Status.FAILED));
        ui.process(new ErrorEvent(ErrorEvent.Error.Listening, e2));
      }
      else
      {
        ui.process(new ProxyServerStatusEvent(this, Status.STOPPED));
      }
    }
  }

  private void listenClientSocket(Socket clientSocket, InputStream cin,
      OutputStream cout) throws IOException
  {
    final int port = (cin.read() << 8) | cin.read();
    final byte[] addr = new byte[4];
    cin.read(addr);

    while (cin.read() != 0)
    {}

    try (Socket serverSocket = new Socket(InetAddress.getByAddress(addr), port);
        InputStream sin = serverSocket.getInputStream();
        OutputStream sout = serverSocket.getOutputStream())
    {
      if (serverSocket.isConnected())
      {
        cout.write(SUCCESS_REPLY);
        connectionProcessor.process(ui, config, clientSocket, serverSocket);
      }
      else
      {
        cout.write(FAILURE_REPLY);
      }
    } catch (final IOException e)
    {
      ui.process(new ErrorEvent(ErrorEvent.Error.ProcessingConnection, e));
    }
  }

  public int getPort()
  {
    return port;
  }

  public void stop()
  {
    if (work)
    {
      work = false;
      try
      {
        proxyServerSocket.close();
      } catch (final IOException e)
      {}
    }
  }
}
