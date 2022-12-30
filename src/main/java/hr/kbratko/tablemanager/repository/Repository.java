package hr.kbratko.tablemanager.repository;

@FunctionalInterface
public interface Repository {
  void commit() throws Exception;
}
