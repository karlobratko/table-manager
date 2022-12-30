package hr.kbratko.tablemanager.utils;

import hr.kbratko.tablemanager.server.TableManagerApplicationServer;
import hr.kbratko.tablemanager.server.jndi.JndiHelper;
import hr.kbratko.tablemanager.server.jndi.JndiKeyEnum;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.server.model.Response;
import java.io.IOException;
import javax.naming.NamingException;

public class Requests {
  public static final int DEFAULT_TIMEOUT = 2000;
  public static final String DEFAULT_HOST;
  public static final int DEFAULT_PORT;

  static {
    try {
      DEFAULT_PORT = Integer.parseInt(JndiHelper.getConfigurationParameter(JndiKeyEnum.APP_PORT));
      DEFAULT_HOST = JndiHelper.getConfigurationParameter(JndiKeyEnum.APP_HOST);
    } catch (NamingException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Requests() {
    throw new RuntimeException("No Requests instance for you!!!");
  }

  public static Response send(Request request) throws IOException, ClassNotFoundException {
    return send(request, DEFAULT_TIMEOUT);
  }

  public static Response send(Request request, int timeout) throws IOException, ClassNotFoundException {
    return send(DEFAULT_HOST, DEFAULT_PORT, request, timeout);
  }

  public static Response send(String host, int port, Request request, int timeout) throws IOException, ClassNotFoundException {
    try (final var socket = Sockets.client(host, port)) {
      socket.setSoTimeout(timeout);

      final var out = Streams.objectOutput(Streams.bufferedOutput(socket.getOutputStream()));
      out.writeObject(request);
      out.flush();
      socket.shutdownOutput();

      final var in = Streams.objectInput(Streams.bufferedInput(socket.getInputStream()));
      final var response = (Response) in.readObject();
      socket.shutdownInput();

      return response;
    }
  }
}
