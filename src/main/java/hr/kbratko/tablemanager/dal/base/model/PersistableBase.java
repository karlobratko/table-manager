package hr.kbratko.tablemanager.dal.base.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class PersistableBase<K> implements Persistable<K> {
  protected @Nullable K    _id;
  protected @Nullable UUID _uuid;
  protected @Nullable K    _createdBy;
  protected @Nullable K    _updatedBy;
  protected @Nullable K    _deletedBy;

  protected @Nullable LocalDateTime _createDate;
  protected @Nullable LocalDateTime _updateDate;
  protected @Nullable LocalDateTime _deleteDate;

  @Contract(pure = true)
  public PersistableBase() {}

  @Contract(pure = true)
  public PersistableBase(@NotNull K id,
                         @NotNull UUID uuid,
                         @NotNull K createdBy,
                         @NotNull LocalDateTime createDate,
                         @NotNull K updatedBy,
                         @NotNull LocalDateTime updateDate,
                         @Nullable K deletedBy,
                         @Nullable LocalDateTime deleteDate) {
    this._id         = id;
    this._uuid       = uuid;
    this._createdBy  = createdBy;
    this._updatedBy  = updatedBy;
    this._deletedBy  = deletedBy;
    this._createDate = createDate;
    this._updateDate = updateDate;
    this._deleteDate = deleteDate;
  }

  @Override
  @Nullable
  public K getId() {
    return this._id;
  }

  @Override
  @Nullable
  public UUID getUUID() {
    return this._uuid;
  }

  @Override
  @Nullable
  public LocalDateTime getCreateDate() {
    return this._createDate;
  }

  @Override
  @Nullable
  public K getCreatedBy() {
    return this._createdBy;
  }

  @Override
  @Nullable
  public LocalDateTime getUpdateDate() {
    return this._updateDate;
  }

  @Override
  @Nullable
  public K getUpdatedBy() {
    return this._updatedBy;
  }

  @Override
  @Nullable
  public LocalDateTime getDeleteDate() {
    return this._deleteDate;
  }

  @Override
  @Nullable
  public K getDeletedBy() {
    return this._deletedBy;
  }

  @Override
  public void setId(final @NotNull K id) {
    this._id = id;
  }

  @Override
  public void setUUID(final @NotNull UUID uuid) {
    this._uuid = uuid;
  }

  @Override
  public void setCreateDate(final @NotNull LocalDateTime createDate) {
    this._createDate = createDate;
  }

  @Override
  public void setCreatedBy(final @NotNull K createdBy) {
    this._createdBy = createdBy;
  }

  @Override
  public void setUpdateDate(final @NotNull LocalDateTime updateDate) {
    this._updateDate = updateDate;
  }

  @Override
  public void setUpdatedBy(final @NotNull K updatedBy) {
    this._updatedBy = updatedBy;
  }

  @Override
  public void setDeleteDate(final @Nullable LocalDateTime deleteDate) {this._deleteDate = deleteDate;}

  @Override
  public void setDeletedBy(final @Nullable K deletedBy) {this._deletedBy = deletedBy;}
}
