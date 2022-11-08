package hr.kbratko.tablemanager.dal.base.repository;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ReadOnlyRepository<T> {
  @NotNull 
  Collection<T> readAll() throws Exception;
}
