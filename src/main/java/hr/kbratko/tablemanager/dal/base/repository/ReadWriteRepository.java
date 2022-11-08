package hr.kbratko.tablemanager.dal.base.repository;

public interface ReadWriteRepository<K> extends ReadOnlyRepository<K>, WriteOnlyRepository<K> {
  
}
