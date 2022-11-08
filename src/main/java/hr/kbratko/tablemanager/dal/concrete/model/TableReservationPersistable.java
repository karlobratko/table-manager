package hr.kbratko.tablemanager.dal.concrete.model;

import hr.kbratko.tablemanager.dal.base.model.PersistableBase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class TableReservationPersistable extends PersistableBase<Integer> {
  private final int _tableFK;
  private final int _reservationFK;

  public TableReservationPersistable(final int tableFK,
                                     final int reservationFK) {
    super();
    _tableFK       = tableFK;
    _reservationFK = reservationFK;
  }

  public TableReservationPersistable(final int tableFK,
                                     final int reservationFK,
                                     final @NotNull Integer id,
                                     final @NotNull UUID guid,
                                     final @NotNull Integer createdBy,
                                     final @NotNull LocalDateTime createDate,
                                     final @NotNull Integer updatedBy,
                                     final @NotNull LocalDateTime updateDate,
                                     final @Nullable Integer deletedBy,
                                     final @Nullable LocalDateTime deleteDate) {
    super(id,
          guid,
          createdBy,
          createDate,
          updatedBy,
          updateDate,
          deletedBy,
          deleteDate);
    _tableFK       = tableFK;
    _reservationFK = reservationFK;
  }

  @Contract(pure = true)
  public int getTableId() {return _tableFK;}

  @Contract(pure = true)
  public int getReservationId() {return _reservationFK;}

  @Contract(value = "null -> false", pure = true)
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (Objects.isNull(obj) || getClass() != obj.getClass()) return false;
    final var tr = (TableReservationPersistable)obj;
    return _tableFK == tr._tableFK &&
           _reservationFK == tr._reservationFK;
  }

  @Override
  public int hashCode() {
    return Objects.hash(_tableFK, _reservationFK);
  }

  @Override
  public String toString() {return "%d, %d".formatted(_tableFK, _reservationFK);}
}
