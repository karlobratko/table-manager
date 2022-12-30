package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

public interface UpdateRepository<K extends Comparable<K>, T extends Identifiable<K>> extends IdentifiableRepository<K, T> {
  @NotNull K update(final @NotNull T model) throws Exception;
}
