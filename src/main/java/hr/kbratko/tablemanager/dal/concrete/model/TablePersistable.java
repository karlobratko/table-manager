package hr.kbratko.tablemanager.dal.concrete.model;

import hr.kbratko.tablemanager.dal.base.model.PersistableBase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class TablePersistable extends PersistableBase<Integer> {
  private final @NotNull String _name;
  private final          int    _nrSeats;
  private @Nullable      String _description;

  public TablePersistable(final @NotNull String name, final int nrSeats) {
    super();
    _name    = name;
    _nrSeats = nrSeats;
  }

  public TablePersistable(final @NotNull String name, final int nrSeats, final @Nullable String description) {
    this(name, nrSeats);
    _description = description;
  }

  public TablePersistable(final @NotNull String name,
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
    _name    = name;
    _nrSeats = nrSeats;
  }

  public TablePersistable(final @NotNull String name,
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
    this(name,
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
  private String getName() {return _name;}

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
    final var table = (TablePersistable)obj;
    return _name.equals(table._name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_name);
  }

  @Override
  public String toString() {return "%s (%d seat/s)".formatted(_name, _nrSeats);}
}
