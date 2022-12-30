package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.repository.model.User;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface UserManager {
  void setUser(final @NotNull User user);
}
