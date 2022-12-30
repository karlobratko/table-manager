package hr.kbratko.tablemanager.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Sockets {
  private Sockets() {
    throw new RuntimeException("No Sockets instance for you!!!");
  }

  public static Socket client(String host, int port) throws IOException {
    return new Socket(host, port);
  }

  public static Socket client(InetAddress address, int port) throws IOException {
    return new Socket(address, port);
  }

  public static ServerSocket server(int port) throws IOException {
    return new ServerSocket(port);
  }

  public static ServerSocket server(int port, int queueLength) throws IOException {
    return new ServerSocket(port, queueLength);
  }

  public static ServerSocket server(int port, int queueLength, InetAddress bindAddress) throws IOException {
    return new ServerSocket(port, queueLength, bindAddress);
  }

  public static boolean isConnected(Socket socket) {
    return socket.isConnected() && !socket.isClosed();
  }
}
