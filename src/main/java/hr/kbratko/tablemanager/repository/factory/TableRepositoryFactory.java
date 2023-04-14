package hr.kbratko.tablemanager.repository.factory;

import hr.kbratko.tablemanager.repository.TableRepository;
import hr.kbratko.tablemanager.repository.fs.FsTableRepository;

public class TableRepositoryFactory {
  private static final TableRepository repository = new FsTableRepository();

  private TableRepositoryFactory() {throw new AssertionError("No hr.kbratko.tablemanager.repository.factory.TableRepositoryFactory instances for you!");}

  public static TableRepository getInstance() {return repository;}
}
