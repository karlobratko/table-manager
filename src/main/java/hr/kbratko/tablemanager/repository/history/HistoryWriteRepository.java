package hr.kbratko.tablemanager.repository.history;

import java.util.Collection;

public interface HistoryWriteRepository<T> {

  void write(T model) throws Exception;

  void append(T model) throws Exception;

  void writeAll(Collection<T> models) throws Exception;

}
