package hr.kbratko.tablemanager.server.model;

import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.server.rmi.ChatClient;

public class Chatter {
  private final User user;
  private final ChatClient client;

  private Chatter(User user, ChatClient client) {
    this.user = user;
    this.client = client;
  }

  public static Chatter of(User user, ChatClient client) {
    return new Chatter(user, client);
  }

  public User getUser() {
    return user;
  }

  public ChatClient getClient() {
    return client;
  }
}
