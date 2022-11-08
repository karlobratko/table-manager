package hr.kbratko.tablemanager.dal.concrete.model;

import hr.kbratko.tablemanager.dal.base.model.PersistableBase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public final class ReservationPersistable extends PersistableBase<Integer> {
  private final @NotNull String    _reservationOn;
  private final @NotNull LocalDate _date;
  private final @NotNull LocalTime _time;
  private final          int       _nrSeats;
  private @Nullable      String    _description;

  public ReservationPersistable(final @NotNull String reservationOn,
                                final @NotNull LocalDate date,
                                final @NotNull LocalTime time,
                                final int nrSeats) {
    super();
    _reservationOn = reservationOn;
    _date          = date;
    _time          = time;
    _nrSeats       = nrSeats;
  }

  public ReservationPersistable(final @NotNull String reservationOn,
                                final @NotNull LocalDate date,
                                final @NotNull LocalTime time,
                                final int nrSeats,
                                final @Nullable String description) {
    this(reservationOn, date, time, nrSeats);
    _description = description;
  }

  public ReservationPersistable(final @NotNull String reservationOn,
                                final @NotNull LocalDate date,
                                final @NotNull LocalTime time,
                                final int nrSeats,
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
    _reservationOn = reservationOn;
    _date          = date;
    _time          = time;
    _nrSeats       = nrSeats;
  }

  public ReservationPersistable(final @NotNull String reservationOn,
                                final @NotNull LocalDate date,
                                final @NotNull LocalTime time,
                                final int nrSeats,
                                final @Nullable String description,
                                final @NotNull Integer id,
                                final @NotNull UUID guid,
                                final @NotNull Integer createdBy,
                                final @NotNull LocalDateTime createDate,
                                final @NotNull Integer updatedBy,
                                final @NotNull LocalDateTime updateDate,
                                final @Nullable Integer deletedBy,
                                final @Nullable LocalDateTime deleteDate) {
    this(reservationOn,
         date,
         time,
         nrSeats,
         id,
         guid,
         createdBy,
         createDate,
         updatedBy,
         updateDate,
         deletedBy,
         deleteDate);
    _description = description;
  }

  @Contract(pure = true)
  @NotNull
  private String getReservationOn() {return _reservationOn;}

  @Contract(pure = true)
  @NotNull
  private LocalDate getDate() {return _date;}

  @Contract(pure = true)
  @NotNull
  private LocalTime getTime() {return _time;}

  @Contract(pure = true)
  private int getNrSeats() {return _nrSeats;}

  @Contract(pure = true)
  @Nullable
  private String getDescription() {return _description;}

  @Contract(mutates = "this")
  private void setDescription(final @NotNull String description) {_description = description;}

  @Contract(value = "null -> false", pure = true)
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (Objects.isNull(obj) || getClass() != obj.getClass()) return false;
    final var reservation = (ReservationPersistable)obj;
    return _reservationOn.equals(reservation._reservationOn) &&
           _date.equals(reservation._date) &&
           _time.equals(reservation._time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_reservationOn, _date, _time);
  }

  @Override
  public String toString() {
    return "%s (%s, %s)".formatted(_reservationOn,
                                   _date.format(DateTimeFormatter.ofPattern("MMM, dd yyyy")),
                                   _time.format(DateTimeFormatter.ofPattern("HH:mm")));
  }
}
