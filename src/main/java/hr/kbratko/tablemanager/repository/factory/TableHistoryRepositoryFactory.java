package hr.kbratko.tablemanager.repository.factory;

import hr.kbratko.tablemanager.repository.history.RwHistoryRepository;
import hr.kbratko.tablemanager.repository.history.xml.TableXMLHistoryRepository;
import hr.kbratko.tablemanager.repository.model.TableHistoryModel;

public class TableHistoryRepositoryFactory {
  private static final RwHistoryRepository<TableHistoryModel> repository = new TableXMLHistoryRepository();

  private TableHistoryRepositoryFactory() {
    throw new AssertionError("No hr.kbratko.tablemanager.repository.factory.TableHistoryRepositoryFactory instances for you!");
  }

  public static RwHistoryRepository<TableHistoryModel> getInstance() {
    return repository;
  }
}
