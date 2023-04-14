package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

public interface IdentifiableRepository<K extends Comparable<K>, T extends Identifiable<K>>
  extends Repository {
  @NotNull K nextId();
}
