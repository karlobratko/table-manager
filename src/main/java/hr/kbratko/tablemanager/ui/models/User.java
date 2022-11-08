package hr.kbratko.tablemanager.ui.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class User {
  private final String _username;
  private final String _password;

  @Contract(pure = true)
  private User(final @NotNull String username,
               final @NotNull String password) {
    _username = username;
    _password = password;
  }

  @Contract(value = "_, _ -> new", pure = true)
  public static @NotNull User of(final @NotNull String username,
                                 final @NotNull String password) {
    return new User(username, password);
  }

  @Contract(pure = true)
  public @NotNull String getUsername() {return _username;}

  @Contract(pure = true)
  public @NotNull String getPassword() {return _password;}
}
