package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import tweaker.Configuration;
import ui.UserInterface;

public class ConnectionProcessor
{
  public void process(UserInterface ui, Configuration config,
      Socket clientSocket, Socket serverSocket) throws IOException
  {
    process(clientSocket.getInputStream(), clientSocket.getOutputStream(),
        serverSocket.getInputStream(), serverSocket.getOutputStream());
  }

  public void process(final InputStream cin, OutputStream cout,
      InputStream sin, final OutputStream sout)
  {
    new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          copy(cin, sout);
        } catch (final IOException e)
        {}
      }
    }.start();

    try
    {
      copy(sin, cout);
    } catch (final IOException e)
    {}
  }

  private static void copy(InputStream in, OutputStream out) throws IOException
  {
    int b;
    while ((b = in.read()) != -1)
    {
      out.write(b);
      if (in.available() == 0)
      {
        out.flush();
      }
    }
  }
}
