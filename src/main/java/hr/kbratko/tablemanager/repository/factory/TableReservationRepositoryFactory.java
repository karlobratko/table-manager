package hr.kbratko.tablemanager.repository.factory;

import hr.kbratko.tablemanager.repository.TableReservationRepository;
import hr.kbratko.tablemanager.repository.fs.FsTableReservationRepository;

public class TableReservationRepositoryFactory {
  private static final TableReservationRepository repository = new FsTableReservationRepository();

  private TableReservationRepositoryFactory() {throw new AssertionError("No hr.kbratko.tablemanager.repository.factory.TableReservationRepositoryFactory instances for you!");}

  public static TableReservationRepository getInstance() {return repository;}
}