package hr.kbratko.tablemanager.ui.models;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class TableProjection implements Serializable {
  static final long serialVersionUID = 1L;

  private final int    _id;
  private final String _name;
  private final int    _nrSeats;
  private final String _description;

  @Contract(pure = true)
  public TableProjection(final int id,
                         final String name,
                         final int nrSeats,
                         final String description) {
    _id          = id;
    _name        = name;
    _nrSeats     = nrSeats;
    _description = description;
  }

  @Contract(pure = true)
  public int getId() {
    return _id;
  }

  @Contract(pure = true)
  public String getName() {return _name;}

  @Contract(pure = true)
  public int getNrSeats() {
    return _nrSeats;
  }

  @Contract(pure = true)
  public String getDescription() {
    return _description;
  }
}
