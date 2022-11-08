package hr.kbratko.tablemanager.ui.models;

import hr.kbratko.tablemanager.utils.Serializations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FsReservationRepository {
  private static final String                  FILE_NAME      = "reservations_cache.txt";
  private static final String                  FILE_NAME_SER  = "reservations.ser";
  private static final String                  DELIMITER      = "###";
  private static final FsReservationRepository INSTANCE       = new FsReservationRepository();
  private static final DateTimeFormatter       DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
  private static final DateTimeFormatter       TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

  private static final ObservableList<Reservation> RESERVATIONS = FXCollections.observableArrayList();

  @Contract(pure = true)
  private FsReservationRepository() {}

  @Contract(pure = true)
  public static FsReservationRepository getInstance() {return INSTANCE;}

  @Contract(pure = true)
  public ObservableList<Reservation> getReservations() {return RESERVATIONS;}

  public void addReservation(final @NotNull Reservation item) {
    RESERVATIONS.add(item);
  }

  public void removeReservation(final @NotNull Reservation item) {RESERVATIONS.remove(item);}

  public void loadReservations() throws Exception {
    Serializations.<List<ReservationProjection>>read(FILE_NAME_SER)
                  .forEach(item ->
                             RESERVATIONS.add(
                               new Reservation
                                 .Builder(item.getId(),
                                          item.getReservationOn(),
                                          item.getDate(),
                                          item.getTime(),
                                          item.getNrSeats())
                                 .description(item.getDescription())
                                 .build()));
  }

  public void saveReservations() throws IOException {
    Serializations.write(
      RESERVATIONS.stream()
                  .map(item -> new ReservationProjection(item.getId(),
                                                         item.getReservationOn(),
                                                         item.getDate(),
                                                         item.getTime(),
                                                         item.getNrSeats(),
                                                         item.getDescription()))
                  .toList(),
      FILE_NAME_SER);
  }
}
