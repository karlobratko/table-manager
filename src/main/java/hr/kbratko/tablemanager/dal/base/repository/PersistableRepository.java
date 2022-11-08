package hr.kbratko.tablemanager.dal.base.repository;

import hr.kbratko.tablemanager.dal.base.model.Persistable;
import hr.kbratko.tablemanager.dal.concrete.status.ResponseCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PersistableRepository<K, T extends Persistable<K>> extends IdentifiableRepository<K, T> {
  @NotNull
  ResponseCode create(final @NotNull T model, final @Nullable K createdBy) throws Exception;

  @NotNull
  Collection<T> readAllIfNotDeleted() throws Exception;

  @NotNull
  Optional<T> readByIdIfNotDeleted(final @NotNull K id) throws Exception;

  ResponseCode update(final @NotNull UUID uuid, final @NotNull T model, final @Nullable K updatedBy) throws Exception;

  ResponseCode delete(final @NotNull UUID uuid, final @Nullable K deletedBy) throws Exception;
}
