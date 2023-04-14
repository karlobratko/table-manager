package hr.kbratko.tablemanager.server;

import hr.kbratko.tablemanager.server.callables.RequestHandler;
import hr.kbratko.tablemanager.server.jndi.JndiHelper;
import hr.kbratko.tablemanager.server.jndi.JndiKeyEnum;
import hr.kbratko.tablemanager.utils.Sockets;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

public class TableManagerApplicationServer {
  private static final int THREAD_POOL_SIZE = 16;
  private static final int CONNECTIONS_QUEUE_LENGTH = 32;
  private static final Logger logger = Logger.getLogger(TableManagerApplicationServer.class.getName());

  public static void main(String[] args) {
    final var pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    try {
      final int appPort = Integer.parseInt(JndiHelper.getConfigurationParameter(JndiKeyEnum.APP_PORT));
      final String appHost = JndiHelper.getConfigurationParameter(JndiKeyEnum.APP_HOST);

      try (final var server = Sockets.server(appPort, CONNECTIONS_QUEUE_LENGTH, InetAddress.getByName(appHost))) {
        logger.info("Server listening on %s".formatted(server.getLocalSocketAddress()));

        while (true) {
          try {
            final var client = server.accept();
            pool.submit(RequestHandler.from(client));
          } catch (IOException e) {
            logger.log(Level.WARNING, "Error while accepting connection", e);
          } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Unexpected error", e);
          }
        }
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Could not start server", e);
      }
    } catch (NamingException e) {
      logger.log(Level.SEVERE, "Couldn't find configuration parameter", e);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error while accessing configuration file", e);
    }

    pool.shutdown();
    try {
      if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
        pool.shutdownNow();
        if (!pool.awaitTermination(60, TimeUnit.SECONDS))
          logger.warning("Pool did not terminate");
      }
    } catch (InterruptedException e) {
      pool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
