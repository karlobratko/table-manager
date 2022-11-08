package hr.kbratko.tablemanager.ui.models;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TableReservation {
  private final ReadOnlyIntegerProperty _tableId;
  private final ReadOnlyIntegerProperty _reservationId;

  @Contract(pure = true)
  private TableReservation(final int tableId,
                           final int reservationId) {
    _tableId       = new SimpleIntegerProperty(tableId);
    _reservationId = new SimpleIntegerProperty(reservationId);
  }

  @Contract(value = "_, _ -> new", pure = true)
  public static @NotNull TableReservation of(final int tableId,
                                             final int reservationId) {
    return new TableReservation(tableId, reservationId);
  }

  @Contract(pure = true)
  public int getTableId() {return _tableId.get();}

  @Contract(pure = true)
  public @NotNull ReadOnlyIntegerProperty tableIdProperty() {return _tableId;}

  @Contract(pure = true)
  public int getReservationId() {return _reservationId.get();}

  @Contract(pure = true)
  public @NotNull ReadOnlyIntegerProperty reservationIdProperty() {return _reservationId;}


  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (Objects.isNull(obj) || getClass() != obj.getClass()) return false;
    final var tr = (TableReservation)obj;
    return _tableId.get() == tr._tableId.get() &&
           _reservationId.get() == tr._reservationId.get();
  }

  @Override
  public int hashCode() {
    return Objects.hash(_tableId.get(), _reservationId.get());
  }

  @Override
  public String toString() {return String.format("%d, %d", _tableId.get(), _reservationId.get());}
}
