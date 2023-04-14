package hr.kbratko.tablemanager.repository.model;

import hr.kbratko.tablemanager.repository.Copyable;
import hr.kbratko.tablemanager.repository.IdentifiableModel;
import hr.kbratko.tablemanager.repository.history.xml.XMLConvertible;
import java.io.Serial;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Table
  extends IdentifiableModel<Integer>
  implements Copyable<Table>, Serializable, XMLConvertible<Table> {

  // XMLConvertible field names
  public static final String XML_ELEMENT_ID = "Id";
  public static final String XML_ELEMENT_NAME = "Name";
  public static final String XML_ELEMENT_NRSEATS = "NrSeats";
  public static final String XML_ELEMENT_DESCRIPTION = "Description";
  @Serial
  private static final long serialVersionUID = 1L;

  private String name;
  private Integer nrSeats;
  private String description;

  private Table(final @NotNull Builder builder) {
    super(builder.id);
    this.name = builder.name;
    this.nrSeats = builder.nrSeats;
    this.description = builder.description;
  }

  private Table() {}

  public Table(final @NotNull Integer id) {
    super(id);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Table empty() {
    return new Table();
  }

  public String getName() {
    return name;
  }

  public void setName(final @NotNull String name) {
    this.name = name;
  }

  public Integer getNrSeats() {
    return nrSeats;
  }

  public void setNrSeats(final @NotNull Integer nrSeats) {
    this.nrSeats = nrSeats;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public void copy(final @NotNull Table from) {
    this.name = from.name;
    this.nrSeats = from.nrSeats;
    this.description = from.description;
  }

  @Override
  public Element toXML(Document document) {
    final var root = document.createElement(Table.class.getSimpleName());

    final var id = document.createElement(XML_ELEMENT_ID);
    id.appendChild(document.createTextNode(this.id.toString()));
    root.appendChild(id);

    final var name = document.createElement(XML_ELEMENT_NAME);
    name.appendChild(document.createTextNode(this.name));
    root.appendChild(name);

    final var nrSeats = document.createElement(XML_ELEMENT_NRSEATS);
    nrSeats.appendChild(document.createTextNode(this.nrSeats.toString()));
    root.appendChild(nrSeats);

    final var description = document.createElement(XML_ELEMENT_DESCRIPTION);
    description.appendChild(document.createTextNode(this.description));
    root.appendChild(description);

    return root;
  }

  @Override
  public Table fromXML(Element element) {
    this.id = Integer.parseInt(
      element.getElementsByTagName(XML_ELEMENT_ID)
        .item(0)
        .getTextContent()
    );

    this.name = element.getElementsByTagName(XML_ELEMENT_NAME)
      .item(0)
      .getTextContent();

    this.nrSeats = Integer.parseInt(
      element.getElementsByTagName(XML_ELEMENT_NRSEATS)
        .item(0)
        .getTextContent()
    );

    this.description = element.getElementsByTagName(XML_ELEMENT_DESCRIPTION)
      .item(0)
      .getTextContent();

    return this;
  }

  public static final class Builder {
    private Integer id;
    private String name;
    private Integer nrSeats;
    private String description;

    private Builder() {
    }

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

    public @NotNull Table build() {
      return new Table(this);
    }
  }
}
