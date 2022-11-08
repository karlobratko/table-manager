package hr.kbratko.tablemanager.ui.models;

import javafx.beans.property.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Table {
  private final ReadOnlyIntegerProperty _id;
  private final StringProperty          _name;
  private final IntegerProperty         _nrSeats;
  private final StringProperty          _description;

  public static final class Builder {
    private final int    _id;
    private final String _name;
    private final int    _nrSeats;

    private String _description = "";

    @Contract(pure = true)
    public Builder(final int id, final @NotNull String name, final int nrSeats) {
      _id      = id;
      _name    = name;
      _nrSeats = nrSeats;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull Builder description(final @NotNull String description) {
      _description = description;
      return this;
    }

    @Contract(" -> new")
    public @NotNull Table build() {
      return new Table(this);
    }
  }

  private Table(@NotNull Builder builder) {
    _id          = new SimpleIntegerProperty(builder._id);
    _name        = new SimpleStringProperty(builder._name);
    _nrSeats     = new SimpleIntegerProperty(builder._nrSeats);
    _description = new SimpleStringProperty(builder._description);
  }

  @Contract(pure = true)
  private Table(final int id,
                final @NotNull String name,
                final int nrSeats) {
    _id          = new SimpleIntegerProperty(id);
    _name        = new SimpleStringProperty(name);
    _nrSeats     = new SimpleIntegerProperty(nrSeats);
    _description = new SimpleStringProperty("");
  }

  @Contract(pure = true)
  private Table(final int id,
                final @NotNull String name,
                final int nrSeats,
                final @NotNull String description) {
    this(id, name, nrSeats);
    _description.set(description);
  }

  @Contract(pure = true)
  public int getId() {return _id.get();}

  @Contract(pure = true)
  public @NotNull ReadOnlyIntegerProperty idProperty() {return _id;}

  @Contract(pure = true)
  public int getNrSeats() {return _nrSeats.get();}

  @Contract(pure = true)
  public @NotNull IntegerProperty nrSeatsProperty() {return _nrSeats;}

  @Contract(pure = true)
  public @NotNull String getName() {return _name.get();}

  @Contract(pure = true)
  public @NotNull StringProperty nameProperty() {return _name;}

  @Contract(pure = true)
  public @NotNull String getDescription() {return _description.get();}

  @Contract(pure = true)
  public @NotNull StringProperty descriptionProperty() {return _description;}

  //  public void setId(final int id) {_id.set(id);}

  public void setNrSeats(final int nrSeats) {_nrSeats.set(nrSeats);}

  public void setName(final @NotNull String name) {_name.set(name);}

  public void setDescription(final @NotNull String description) {_description.set(description);}

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (Objects.isNull(obj) || getClass() != obj.getClass()) return false;
    final var table = (Table)obj;
    return _name.get().equals(table._name.get());
  }

  @Override
  public int hashCode() {
    return Objects.hash(_name.get());
  }

  @Override
  public String toString() {
    return String.format("%s (%d seat/s)", _name.get(), _nrSeats.get());
  }
}
