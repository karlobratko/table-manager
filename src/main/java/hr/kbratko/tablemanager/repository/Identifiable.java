package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

public interface Identifiable<K extends Comparable<K>> {
  @NotNull K getId();

  void setId(final @NotNull K id);
}
