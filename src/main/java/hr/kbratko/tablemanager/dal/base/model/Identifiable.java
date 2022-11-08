package hr.kbratko.tablemanager.dal.base.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Identifiable<K> {
  @Nullable
  K getId();

  @Nullable
  UUID getUUID();

  void setId(final @NotNull K id);

  void setUUID(final @NotNull UUID uuid);
}
