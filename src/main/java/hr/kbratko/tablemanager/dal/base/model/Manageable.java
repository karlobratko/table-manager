package hr.kbratko.tablemanager.dal.base.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public interface Manageable<K> {
  @Nullable
  LocalDateTime getCreateDate();

  @Nullable
  K getCreatedBy();

  @Nullable
  LocalDateTime getUpdateDate();

  @Nullable
  K getUpdatedBy();

  @Nullable
  LocalDateTime getDeleteDate();

  @Nullable
  K getDeletedBy();

  void setCreateDate(final @NotNull LocalDateTime createDate);

  void setCreatedBy(final @NotNull K createdBy);

  void setUpdateDate(final @NotNull LocalDateTime updateDate);

  void setUpdatedBy(final @NotNull K updatedBy);

  void setDeleteDate(final @Nullable LocalDateTime deleteDate);

  void setDeletedBy(final @Nullable K deletedBy);
}
