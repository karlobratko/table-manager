package hr.kbratko.tablemanager.ui.controllers;

import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Stageable {
  void setStage(final @NotNull Stage stage);
}
