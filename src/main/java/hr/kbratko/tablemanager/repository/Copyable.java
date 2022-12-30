package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Copyable<T> {
  void copy(final @NotNull T from);
}
