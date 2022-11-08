package hr.kbratko.tablemanager.ui.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateReservationModel {
  private final Reservation _reservation;
  private final List<Table> _relatedTables;

  @Contract(pure = true)
  private CreateReservationModel(final @NotNull Reservation reservation,
                                 final @NotNull List<Table> relatedTables) {
    _reservation   = reservation;
    _relatedTables = relatedTables;
  }

  @Contract(value = "_, _ -> new", pure = true)
  public static @NotNull CreateReservationModel of(final @NotNull Reservation reservation,
                                                   final @NotNull List<Table> relatedTables) {
    return new CreateReservationModel(reservation, relatedTables);
  }

  @Contract(pure = true)
  public Reservation getReservation() {
    return _reservation;
  }

  @Contract(pure = true)
  public List<Table> getRelatedTables() {
    return _relatedTables;
  }
}
