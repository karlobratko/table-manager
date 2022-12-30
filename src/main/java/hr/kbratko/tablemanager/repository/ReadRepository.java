package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public interface ReadRepository<K extends Comparable<K>, T extends Identifiable<K>> extends IdentifiableRepository<K, T> {
  @NotNull Optional<T> read(final @NotNull K id) throws Exception;
  
  Set<T> read() throws Exception;
}
