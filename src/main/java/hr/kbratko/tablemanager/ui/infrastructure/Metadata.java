package hr.kbratko.tablemanager.ui.infrastructure;

import org.jetbrains.annotations.NotNull;

public class Metadata {
  private final @NotNull Long pid;
  
  public Metadata(final @NotNull Long pid) {
    this.pid = pid;
  }

  public Long getPid() {
    return pid;
  }
}
