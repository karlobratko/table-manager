package hr.kbratko.tablemanager.dal.base.repository;

import hr.kbratko.tablemanager.dal.concrete.status.ResponseCode;
import org.jetbrains.annotations.NotNull;

public interface CRUDRepository<T> extends ReadWriteRepository<T> {
  @NotNull
  ResponseCode update(final @NotNull T model) throws Exception;

  @NotNull
  ResponseCode delete(final @NotNull T model) throws Exception;
}
