package hr.kbratko.tablemanager.server.jndi;

public enum JndiKeyEnum {
  RMI_PORT("rmi.port"),
  RMI_HOST("rmi.host"),
  RMI_SERVICE("rmi.service"),
  APP_PORT("app.port"),
  APP_HOST("app.host");

  private String key;

  JndiKeyEnum(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
