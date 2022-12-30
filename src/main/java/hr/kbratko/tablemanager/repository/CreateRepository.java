package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface CreateRepository<K extends Comparable<K>, T extends Identifiable<K>>
  extends IdentifiableRepository<K, T> {
  @NotNull Integer create(final @NotNull T model) throws Exception;

  default @NotNull Integer create(final @NotNull Collection<T> models) throws Exception {return 0;}
}
