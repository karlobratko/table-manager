package hr.kbratko.tablemanager.dal.base.repository;

import hr.kbratko.tablemanager.dal.concrete.status.ResponseCode;
import org.jetbrains.annotations.NotNull;

public interface WriteOnlyRepository<T> {
  @NotNull
  ResponseCode create(final @NotNull T model) throws Exception;
}
