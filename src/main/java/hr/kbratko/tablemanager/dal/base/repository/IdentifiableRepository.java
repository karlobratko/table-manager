package hr.kbratko.tablemanager.dal.base.repository;

import hr.kbratko.tablemanager.dal.base.model.Identifiable;
import hr.kbratko.tablemanager.dal.concrete.status.ResponseCode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface IdentifiableRepository<K, T extends Identifiable<K>> extends CRUDRepository<T> {
  @NotNull
  Optional<T> readById(final @NotNull T id) throws Exception;

  @NotNull
  ResponseCode update(final @NotNull UUID guid, final @NotNull T model) throws Exception;

  @NotNull
  ResponseCode delete(final @NotNull UUID guid) throws Exception;
}
