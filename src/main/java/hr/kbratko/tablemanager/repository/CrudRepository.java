package hr.kbratko.tablemanager.repository;

public interface CrudRepository<K extends Comparable<K>, T extends Identifiable<K>>
  extends CreateRepository<K, T>,
          ReadRepository<K, T>,
          UpdateRepository<K, T>,
          DeleteRepository<K, T> {
}
