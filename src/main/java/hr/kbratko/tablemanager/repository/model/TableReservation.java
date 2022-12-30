package hr.kbratko.tablemanager.repository.model;

import hr.kbratko.tablemanager.repository.Copyable;
import hr.kbratko.tablemanager.repository.IdentifiableModel;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class TableReservation
  extends IdentifiableModel<Integer>
  implements Copyable<TableReservation>, Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  // properties

  private Integer tableId;
  private Integer reservationId;

  // builder

  public static Builder builder() {
    return new Builder();
  }
  public static final class Builder {
    private Integer id;
    private Integer tableId;
    private Integer reservationId;

    private Builder() {}

    public @NotNull Builder id(final @NotNull Integer id) {
      this.id = id;
      return this;
    }

    public @NotNull Builder tableId(final @NotNull Integer tableId) {
      this.tableId = tableId;
      return this;
    }

    public @NotNull Builder reservationId(final @NotNull Integer reservationId) {
      this.reservationId = reservationId;
      return this;
    }

    public @NotNull TableReservation build() {return new TableReservation(this);}
  }

  // constructors

  private TableReservation(final @NotNull Builder builder) {
    super(builder.id);
    this.tableId       = builder.tableId;
    this.reservationId = builder.reservationId;
  }

  public TableReservation(final @NotNull Integer id) {super(id);}

  // getters and setters

  public Integer getTableId() {return tableId;}

  public void setTableId(final Integer tableId) {this.tableId = tableId;}

  public Integer getReservationId() {return reservationId;}

  public void setReservationId(final Integer reservationId) {this.reservationId = reservationId;}

  // overrides

  @Override
  public void copy(final @NotNull TableReservation from) {
    this.tableId       = from.tableId;
    this.reservationId = from.reservationId;
  }
}
