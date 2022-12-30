package hr.kbratko.tablemanager.repository.model;

import hr.kbratko.tablemanager.repository.Copyable;
import hr.kbratko.tablemanager.repository.IdentifiableModel;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class Table
  extends IdentifiableModel<Integer>
  implements Copyable<Table>, Serializable {
  
  @Serial
  private static final long serialVersionUID = 1L;

  // properties

  private String  name;
  private Integer nrSeats;
  private String  description;

  // builder

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Integer id;
    private String  name;
    private Integer nrSeats;
    private String  description;

    private Builder() {}

    public @NotNull Builder id(final @NotNull Integer id) {
      this.id = id;
      return this;
    }

    public @NotNull Builder name(final @NotNull String name) {
      this.name = name;
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

    public @NotNull Table build() {return new Table(this);}
  }

  // constructors

  private Table(final @NotNull Builder builder) {
    super(builder.id);
    this.name        = builder.name;
    this.nrSeats     = builder.nrSeats;
    this.description = builder.description;
  }

  public Table(final @NotNull Integer id) {super(id);}

  // getters and setters

  public String getName() {return name;}

  public void setName(final @NotNull String name) {this.name = name;}

  public Integer getNrSeats() {return nrSeats;}

  public void setNrSeats(final @NotNull Integer nrSeats) {this.nrSeats = nrSeats;}

  public String getDescription() {return description;}

  public void setDescription(final String description) {this.description = description;}

  // overrides

  @Override
  public void copy(final @NotNull Table from) {
    this.name        = from.name;
    this.nrSeats     = from.nrSeats;
    this.description = from.description;
  }
}
