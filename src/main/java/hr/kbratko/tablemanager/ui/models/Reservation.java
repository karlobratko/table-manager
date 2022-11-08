package hr.kbratko.tablemanager.ui.models;

import javafx.beans.property.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Reservation {
  
  private final ReadOnlyIntegerProperty   _id;
  private final StringProperty            _reservationOn;
  private final ObjectProperty<LocalDate> _date;
  private final ObjectProperty<LocalTime> _time;
  private final IntegerProperty           _nrSeats;
  private final StringProperty            _description;

  public static final class Builder {
    private final int       _id;
    private final String    _reservationOn;
    private final LocalDate _date;
    private final LocalTime _time;
    private final int       _nrSeats;

    private String _description = "";

    @Contract(pure = true)
    public Builder(final int id,
                   final @NotNull String reservationOn,
                   final @NotNull LocalDate date,
                   final @NotNull LocalTime time,
                   final int nrSeats) {
      _id            = id;
      _reservationOn = reservationOn;
      _date          = date;
      _time          = time;
      _nrSeats       = nrSeats;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull Builder description(final @NotNull String description) {
      _description = description;
      return this;
    }

    @Contract(" -> new")
    public @NotNull Reservation build() {
      return new Reservation(this);
    }
  }

  @Contract(pure = true)
  private Reservation(@NotNull Builder builder) {
    _id            = new SimpleIntegerProperty(builder._id);
    _reservationOn = new SimpleStringProperty(builder._reservationOn);
    _date          = new SimpleObjectProperty<>(builder._date);
    _time          = new SimpleObjectProperty<>(builder._time);
    _nrSeats       = new SimpleIntegerProperty(builder._nrSeats);
    _description   = new SimpleStringProperty(builder._description);
  }

  @Contract(pure = true)
  private Reservation(final int id,
                      final @NotNull String reservationOn,
                      final @NotNull LocalDate date,
                      final @NotNull LocalTime time,
                      final int nrSeats) {
    _id            = new SimpleIntegerProperty(id);
    _reservationOn = new SimpleStringProperty(reservationOn);
    _date          = new SimpleObjectProperty<>(date);
    _time          = new SimpleObjectProperty<>(time);
    _nrSeats       = new SimpleIntegerProperty(nrSeats);
    _description   = new SimpleStringProperty("");
  }

  @Contract(pure = true)
  private Reservation(final int id,
                      final @NotNull String reservationOn,
                      final @NotNull LocalDate date,
                      final @NotNull LocalTime time,
                      final int nrSeats,
                      final @NotNull String description) {
    this(id, reservationOn, date, time, nrSeats);
    _description.set(description);
  }

  @Contract(pure = true)
  public int getId() {return _id.get();}

  @Contract(pure = true)
  public @NotNull ReadOnlyIntegerProperty idProperty() {return _id;}

  @Contract(pure = true)
  public @NotNull String getReservationOn() {return _reservationOn.get();}

  @Contract(pure = true)
  public @NotNull StringProperty reservationOnProperty() {return _reservationOn;}

  @Contract(pure = true)
  public @NotNull LocalDate getDate() {return _date.get();}

  @Contract(pure = true)
  public @NotNull ObjectProperty<LocalDate> dateProperty() {return _date;}

  @Contract(pure = true)
  public @NotNull LocalTime getTime() {return _time.get();}

  @Contract(pure = true)
  public @NotNull ObjectProperty<LocalTime> timeProperty() {return _time;}

  @Contract(pure = true)
  public int getNrSeats() {return _nrSeats.get();}

  @Contract(pure = true)
  public @NotNull IntegerProperty nrSeatsProperty() {return _nrSeats;}

  @Contract(pure = true)
  public @NotNull String getDescription() {return _description.get();}

  @Contract(pure = true)
  public @NotNull StringProperty descriptionProperty() {return _description;}

  //  public void setId(final int id) {_id.set(id);}

  public void setReservationOn(final @NotNull String reservationOn) {_reservationOn.set(reservationOn);}

  public void setDate(final @NotNull LocalDate date) {_date.set(date);}

  public void setTime(final @NotNull LocalTime time) {_time.set(time);}

  public void setNrSeats(final int nrSeats) {_nrSeats.set(nrSeats);}

  public void setDescription(final @NotNull String description) {_description.set(description);}

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (Objects.isNull(obj) || getClass() != obj.getClass()) return false;
    final var reservation = (Reservation)obj;
    return _reservationOn.get().equals(reservation._reservationOn.get()) &&
           _date.get().equals(reservation._date.get()) &&
           _time.get().equals(reservation._time.get());
  }

  @Override
  public int hashCode() {
    return Objects.hash(_reservationOn.get(), _date.get(), _time.get());
  }

  @Override
  public String toString() {return String.format("%s (%s, %s)", 
                                                 _reservationOn.get(),
                                                 _date.get().format(DateTimeFormatter.ofPattern("MMM, dd yyyy")),
                                                 _time.get().format(DateTimeFormatter.ofPattern("HH:mm")));}
}
