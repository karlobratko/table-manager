package hr.kbratko.tablemanager.dal.base.status;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface StatusCode {
  int toInteger();

  @NotNull
  Map<Integer, StatusCode> getStatusCodeMappings();
}
