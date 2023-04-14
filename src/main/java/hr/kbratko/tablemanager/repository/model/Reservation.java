package hr.kbratko.tablemanager.repository.model;

import hr.kbratko.tablemanager.repository.Copyable;
import hr.kbratko.tablemanager.repository.IdentifiableModel;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation
  extends IdentifiableModel<Integer>
  implements Copyable<Reservation>, Serializable {
  
  @Serial
  private static final long serialVersionUID = 1L;

  // properties

  private String    owner;
  private LocalDate date;
  private LocalTime time;
  private Integer   nrSeats;
  private String    description;

  // builder

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Integer   id;
    private String    owner;
    private LocalDate date;
    private LocalTime time;
    private Integer   nrSeats;
    private String    description;

    private Builder() {}

    public @NotNull Builder id(final @NotNull Integer id) {
      this.id = id;
      return this;
    }

    public @NotNull Builder owner(final @NotNull String owner) {
      this.owner = owner;
      return this;
    }

    public @NotNull Builder date(final @NotNull LocalDate date) {
      this.date = date;
      return this;
    }

    public @NotNull Builder time(final @NotNull LocalTime time) {
      this.time = time;
      return this;
    }

    public @NotNull Builder nrSeats(final @NotNull Integer nrSeats) {
      this.nrSeats = nrSeats;
      return this;
    }

    public @NotNull Builder description(final String description) {
      this.description = description;
      return this;
    }

    public @NotNull Reservation build() {return new Reservation(this);}
  }

  // constructors

  private Reservation(final @NotNull Builder builder) {
    super(builder.id);
    this.owner       = builder.owner;
    this.date        = builder.date;
    this.time        = builder.time;
    this.nrSeats     = builder.nrSeats;
    this.description = builder.description;
  }

  public Reservation(final @NotNull Integer id) {super(id);}

  // getters and setters

  public String getOwner() {return owner;}

  public void setOwner(final @NotNull String owner) {this.owner = owner;}

  public LocalDate getDate() {return date;}

  public void setDate(final @NotNull LocalDate date) {this.date = date;}

  public LocalTime getTime() {return time;}

  public void setTime(final @NotNull LocalTime time) {this.time = time;}

  public Integer getNrSeats() {return nrSeats;}

  public void setNrSeats(final @NotNull Integer nrSeats) {this.nrSeats = nrSeats;}

  public String getDescription() {return description;}

  public void setDescription(final String description) {this.description = description;}

  // overrides

  @Override
  public void copy(final @NotNull Reservation from) {
    this.owner       = from.owner;
    this.date        = from.date;
    this.time        = from.time;
    this.nrSeats     = from.nrSeats;
    this.description = from.description;
  }
}
