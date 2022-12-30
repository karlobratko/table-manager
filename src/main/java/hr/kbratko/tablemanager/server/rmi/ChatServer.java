package hr.kbratko.tablemanager.server.rmi;

import hr.kbratko.tablemanager.repository.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {
  void send(User user, String message) throws RemoteException;
  void join(User user, String hostname, String service) throws RemoteException;
  void leave(User user) throws RemoteException;
}
