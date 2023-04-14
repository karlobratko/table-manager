package hr.kbratko.tablemanager.repository.history.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLConvertible<T> {

  Element toXML(Document document);

  T fromXML(Element element);

}
