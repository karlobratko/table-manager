package hr.kbratko.tablemanager.repository.history;

import java.util.Collection;
import java.util.Optional;

public interface HistoryReadRepository<T> {

  Optional<T> popLast() throws Exception;

  Optional<T> readLast() throws Exception;

  Collection<T> readAll() throws Exception;

}
