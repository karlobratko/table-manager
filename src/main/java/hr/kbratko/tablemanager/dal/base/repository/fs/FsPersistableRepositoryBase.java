package hr.kbratko.tablemanager.dal.base.repository.fs;

import hr.kbratko.tablemanager.dal.base.model.Persistable;
import hr.kbratko.tablemanager.dal.base.repository.PersistableRepository;

public abstract class FsPersistableRepositoryBase<K, T extends Persistable<K>> implements PersistableRepository<K, T> {
  private static final String DIRECTORY = "assets/fs-repo";
  private static final String DELIMITER = "###";

  protected abstract String getModelName();
}
