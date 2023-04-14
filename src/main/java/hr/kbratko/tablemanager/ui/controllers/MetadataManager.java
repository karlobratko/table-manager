package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.ui.infrastructure.Metadata;
import org.jetbrains.annotations.NotNull;

public interface MetadataManager {
  void setMetadata(final @NotNull Metadata metadata);
}
