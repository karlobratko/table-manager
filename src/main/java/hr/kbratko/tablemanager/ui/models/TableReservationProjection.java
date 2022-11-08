package hr.kbratko.tablemanager.ui.models;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class TableReservationProjection implements Serializable {
  static final long serialVersionUID = 1L;

  private final int _tableId;
  private final int _reservationId;

  @Contract(pure = true)
  public TableReservationProjection(final int tableId,
                                    final int reservationId) {
    _tableId       = tableId;
    _reservationId = reservationId;
  }

  @Contract(pure = true)
  public int getTableId() {return _tableId;}

  @Contract(pure = true)
  public int getReservationId() {return _reservationId;}
}