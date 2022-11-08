package hr.kbratko.tablemanager.ui.models;

import hr.kbratko.tablemanager.utils.Serializations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class FsTableReservationRepository {
  private static final String                       FILE_NAME     = "table_reservations_cache.txt";
  private static final String                       FILE_NAME_SER = "table_reservations.ser";
  private static final String                       DELIMITER     = "###";
  private static final FsTableReservationRepository INSTANCE      = new FsTableReservationRepository();

  private static final ObservableSet<TableReservation> TABLE_RESERVATIONS = FXCollections.observableSet();

  @Contract(pure = true)
  private FsTableReservationRepository() {}

  @Contract(pure = true)
  public static FsTableReservationRepository getInstance() {return INSTANCE;}

  @Contract(pure = true)
  public ObservableList<TableReservation> getTableReservations() {return FXCollections.observableArrayList(TABLE_RESERVATIONS);}

  public void addTableReservation(final @NotNull TableReservation item) {
    TABLE_RESERVATIONS.add(item);
  }

  public void removeTableReservation(final @NotNull TableReservation item) {TABLE_RESERVATIONS.remove(item);}

  public void removeTableReservationIf(final @NotNull Predicate<TableReservation> predicate) {TABLE_RESERVATIONS.removeIf(predicate);}

  public void loadTableReservations() throws Exception {
    Serializations.<List<TableReservationProjection>>read(FILE_NAME_SER)
                  .forEach(item ->
                             TABLE_RESERVATIONS.add(
                               TableReservation.of(item.getTableId(),
                                                   item.getReservationId())));
  }

  public void saveTableReservations() throws IOException {
    Serializations.write(
      TABLE_RESERVATIONS.stream()
                        .map(item -> new TableReservationProjection(item.getTableId(),
                                                                    item.getReservationId()))
                        .toList(),
      FILE_NAME_SER);
  }
}
