package hr.kbratko.tablemanager.server.rmi;


import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.server.TableManagerApplicationServer;
import hr.kbratko.tablemanager.server.jndi.JndiHelper;
import hr.kbratko.tablemanager.server.jndi.JndiKeyEnum;
import hr.kbratko.tablemanager.server.model.Chatter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

public class TableManagerChatServer implements ChatServer {
  private static final Logger logger = Logger.getLogger(TableManagerApplicationServer.class.getName());

  private List<Chatter> chatters = new LinkedList<>();

  public static void main(String[] args) {
    try {
      final int rmiPort = Integer.parseInt(JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_PORT));
      final String rmiHost = JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_HOST);
      final String rmiService = JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_SERVICE);

      final Registry registry = LocateRegistry.createRegistry(rmiPort);
      final ChatServer chatServer = new TableManagerChatServer();
      final ChatServer skeleton = (ChatServer) UnicastRemoteObject.exportObject(chatServer, 0);
      registry.rebind("rmi://%s/%s".formatted(rmiHost, rmiService), chatServer);
      logger.info("Object registered in RMI registry");
    } catch (NamingException e) {
      logger.log(Level.SEVERE, "Couldn't find configuration parameter", e);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error while accessing configuration file", e);
    }
  }

  public void broadcast(String newMessage) {
    chatters.forEach(chatter -> {
      try {
        chatter.getClient().receive(newMessage);
      } catch (RemoteException e) {
        logger.log(Level.WARNING, "Could not access remote client", e);
      }
    });
  }

  public void unicast(ChatClient chatClient, String newMessage) throws RemoteException {
    chatClient.receive(newMessage);
  }

  private void updateUsers() {
    final var currentUsers =
      chatters
        .stream()
        .map(Chatter::getUser)
        .map(User::getFullName)
        .toArray(String[]::new);

    chatters.forEach(chatter -> {
      try {
        chatter.getClient().updateUsers(currentUsers);
      } catch (RemoteException e) {
        logger.log(Level.WARNING, "Could not access remote client", e);
      }
    });
  }

  @Override
  public void send(User user, String message) throws RemoteException {
    final var newMessage = "%s : %s".formatted(user.getFullName(), message);
    broadcast(newMessage);
  }

  @Override
  public void join(User user, String hostname, String service) throws RemoteException {
    try {
      final int rmiPort = Integer.parseInt(JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_PORT));
      final String rmiHost = JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_HOST);

      final Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
      final ChatClient client = (ChatClient) registry.lookup("rmi://%s/%s".formatted(hostname, service));
      chatters.add(Chatter.of(user, client));
      unicast(client, "[Server] : Hello %s, you are now free to chat.\n".formatted(user.getFullName()));
      broadcast("[Server] : %s has joined the group chat.\n".formatted(user.getFullName()));
      updateUsers();
    } catch (NotBoundException e) {
      logger.log(Level.SEVERE, "Registry host name not bound", e);
    } catch (NamingException e) {
      logger.log(Level.SEVERE, "Couldn't find configuration parameter", e);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error while accessing configuration file", e);
    }
  }

  @Override
  public void leave(User user) throws RemoteException {
    chatters.removeIf(chatter -> chatter.getUser().equals(user));
    broadcast("[Server] : %s has leaved the group chat.\n".formatted(user.getFullName()));
    updateUsers();
  }
}
