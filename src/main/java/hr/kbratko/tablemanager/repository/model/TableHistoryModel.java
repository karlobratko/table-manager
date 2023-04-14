package hr.kbratko.tablemanager.repository.model;

import hr.kbratko.tablemanager.repository.history.HistoryAction;
import hr.kbratko.tablemanager.repository.history.xml.XMLConvertible;
import java.io.Serial;
import java.io.Serializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TableHistoryModel implements Serializable, XMLConvertible<TableHistoryModel> {
  @Serial
  private static final long serialVersionUID = 1L;

  public static final String XML_ELEMENT_ACTION = "Action";

  private HistoryAction action;
  private Table table;

  private TableHistoryModel() {
  }

  private TableHistoryModel(HistoryAction action, Table table) {
    this.action = action;
    this.table = table;
  }

  public static TableHistoryModel empty() {
    return new TableHistoryModel();
  }

  public static TableHistoryModel of(HistoryAction action, Table table) {
    return new TableHistoryModel(action, table);
  }

  public HistoryAction getAction() {
    return action;
  }

  public Table getTable() {
    return table;
  }

  @Override
  public Element toXML(Document document) {
    final var root = document.createElement(TableHistoryModel.class.getSimpleName());

    final var action = document.createElement(XML_ELEMENT_ACTION);
    action.appendChild(document.createTextNode(this.action.name()));
    root.appendChild(action);

    root.appendChild(table.toXML(document));

    return root;
  }

  @Override
  public TableHistoryModel fromXML(Element element) {
    this.action = HistoryAction.valueOf(
      element.getElementsByTagName(XML_ELEMENT_ACTION)
        .item(0)
        .getTextContent()
    );

    this.table = Table.empty().fromXML(element);

    return this;
  }
}
