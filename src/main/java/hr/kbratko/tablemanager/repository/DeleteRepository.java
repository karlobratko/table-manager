package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface DeleteRepository<K extends Comparable<K>, T extends Identifiable<K>> extends IdentifiableRepository<K, T> {
  @NotNull Integer delete(final @NotNull T model) throws Exception;

  default @NotNull Integer delete(final @NotNull Collection<T> models) throws Exception {return 0;}
}
