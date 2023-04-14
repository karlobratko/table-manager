package hr.kbratko.tablemanager.server.model;

import hr.kbratko.tablemanager.repository.model.User;

public class AuthData {
  private final User user;
  private final Object data;

  private AuthData(User user, Object data) {
    this.user = user;
    this.data = data;
  }

  public static AuthData of(User user, Object object) {
    return new AuthData(user, object);
  }

  public User getUser() {
    return user;
  }

  public Object getData() {
    return data;
  }
}
