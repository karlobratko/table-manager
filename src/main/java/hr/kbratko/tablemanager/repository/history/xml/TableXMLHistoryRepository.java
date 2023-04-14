package hr.kbratko.tablemanager.repository.history.xml;

import hr.kbratko.tablemanager.repository.model.TableHistoryModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TableXMLHistoryRepository implements XMLHistoryRepository<TableHistoryModel> {
  public static final Logger logger = Logger.getLogger(TableXMLHistoryRepository.class.getName());

  private static final String FILE_NAME = "table_history.xml";

  private static final String XML_ROOT = "History";

  @Override
  public void write(TableHistoryModel model) throws Exception {
    final var document = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder()
      .newDocument();

    final var root = document.createElement(XML_ROOT);
    document.appendChild(root);
    root.appendChild(model.toXML(document));

    final var source = new DOMSource(document);
    final var result = new StreamResult(new File(FILE_NAME));

    TransformerFactory.newInstance()
      .newTransformer()
      .transform(source, result);

    logger.info("XML file created and populated.");
  }

  @Override
  public void append(TableHistoryModel model) throws Exception {
    final var file = new File(FILE_NAME);
    if (!file.exists()) {
      write(model);
      return;
    }

    final var document = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder()
      .parse(file);

    final var root = document.getDocumentElement();

    root.appendChild(model.toXML(document));

    final var source = new DOMSource(document);
    final var result = new StreamResult(new File(FILE_NAME));

    TransformerFactory.newInstance()
      .newTransformer()
      .transform(source, result);

    logger.info("XML file created and populated.");
  }

  @Override
  public void writeAll(Collection<TableHistoryModel> models) throws Exception {
    final var document = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder()
      .newDocument();

    final var root = document.createElement(XML_ROOT);
    document.appendChild(root);

    models.stream()
      .map(table -> table.toXML(document))
      .forEach(root::appendChild);

    final var source = new DOMSource(document);
    final var result = new StreamResult(new File(FILE_NAME));

    TransformerFactory.newInstance()
      .newTransformer()
      .transform(source, result);

    logger.info("XML file created and populated.");
  }

  @Override
  public Optional<TableHistoryModel> popLast() throws Exception {
    final var file = new File(FILE_NAME);
    if (!file.exists())
      return Optional.empty();

    final var document = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder()
      .parse(file);

    final var root = document.getDocumentElement();

    final var nodes = root.getElementsByTagName(TableHistoryModel.class.getSimpleName());
    if (nodes.getLength() == 0)
      return Optional.empty();

    final var node = nodes.item(nodes.getLength() - 1);
    if (node.getNodeType() != Node.ELEMENT_NODE)
      return Optional.empty();

    final var element = (Element) node;
    final var model = TableHistoryModel.empty().fromXML(element);

    node.getParentNode().removeChild(node);

    final var source = new DOMSource(document);
    final var result = new StreamResult(new File(FILE_NAME));

    TransformerFactory.newInstance()
      .newTransformer()
      .transform(source, result);

    return Optional.of(model);
  }

  @Override
  public Optional<TableHistoryModel> readLast() throws Exception {
    final var file = new File(FILE_NAME);
    if (!file.exists())
      return Optional.empty();

    final var document = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder()
      .parse(file);

    final var root = document.getDocumentElement();

    final var nodes = root.getElementsByTagName(TableHistoryModel.class.getSimpleName());
    if (nodes.getLength() == 0)
      return Optional.empty();

    final var node = nodes.item(0);
    if (node.getNodeType() != Node.ELEMENT_NODE)
      return Optional.empty();

    final var element = (Element) node;
    return Optional.of(
      TableHistoryModel.empty().fromXML(element)
    );
  }

  @Override
  public Collection<TableHistoryModel> readAll() throws Exception {
    final var file = new File(FILE_NAME);
    if (!file.exists())
      return Collections.emptyList();

    final var document = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder()
      .parse(file);

    final var root = document.getDocumentElement();

    final var nodes = root.getElementsByTagName(TableHistoryModel.class.getSimpleName());

    final var list = new ArrayList<TableHistoryModel>();
    for (int i = 0; i < nodes.getLength(); ++i) {
      final var node = nodes.item(0);
      if (node.getNodeType() != Node.ELEMENT_NODE) continue;

      final var element = (Element) node;
      list.add(TableHistoryModel.empty().fromXML(element));
    }

    return list;
  }
}
