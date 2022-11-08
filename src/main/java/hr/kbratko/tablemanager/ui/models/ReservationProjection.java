package hr.kbratko.tablemanager.ui.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationProjection implements Serializable {
  static final long serialVersionUID = 1L;

  private final int       _id;
  private final String    _reservationOn;
  private final LocalDate _date;
  private final LocalTime _time;
  private final int       _nrSeats;
  private final String    _description;

  @Contract(pure = true)
  public ReservationProjection(final int id,
                               final @NotNull String reservationOn,
                               final @NotNull LocalDate date,
                               final @NotNull LocalTime time,
                               final int nrSeats,
                               final String description) {
    _id            = id;
    _reservationOn = reservationOn;
    _date          = date;
    _time          = time;
    _nrSeats       = nrSeats;
    _description   = description;
  }

  @Contract(pure = true)
  public int getId() {
    return _id;
  }

  @Contract(pure = true)
  public String getReservationOn() {
    return _reservationOn;
  }

  @Contract(pure = true)
  public LocalDate getDate() {
    return _date;
  }

  @Contract(pure = true)
  public LocalTime getTime() {
    return _time;
  }

  @Contract(pure = true)
  public int getNrSeats() {
    return _nrSeats;
  }

  @Contract(pure = true)
  public String getDescription() {
    return _description;
  }
}
