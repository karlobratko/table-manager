package hr.kbratko.tablemanager.repository.factory;

import hr.kbratko.tablemanager.repository.ReservationRepository;
import hr.kbratko.tablemanager.repository.fs.FsReservationRepository;

public class ReservationRepositoryFactory {
  private static final ReservationRepository repository = new FsReservationRepository();

  private ReservationRepositoryFactory() {throw new AssertionError("No hr.kbratko.tablemanager.repository.factory.ReservationRepositoryFactory instances for you!");}

  public static ReservationRepository getInstance() {return repository;}
}
