package hr.kbratko.tablemanager.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class Alerts {
  @Contract(value = " -> fail", pure = true)
  private Alerts() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Alerts instances for you!");}

  @Contract(pure = true)
  public static Optional<ButtonType> show(final @NotNull Alert.AlertType type,
                                          final @NotNull String title,
                                          final @NotNull String headerText,
                                          final @NotNull String contentText) {
    final var alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    alert.setResizable(false);
    alert.initModality(Modality.APPLICATION_MODAL);
    return alert.showAndWait();
  }

  @Contract(pure = true)
  public static Optional<ButtonType> showWarning(final @NotNull String title,
                                                 final @NotNull String headerText,
                                                 final @NotNull String contentText) {
    return show(Alert.AlertType.WARNING, title, headerText, contentText);
  }

  @Contract(pure = true)
  public static Optional<ButtonType> showInformation(final @NotNull String title,
                                                     final @NotNull String headerText,
                                                     final @NotNull String contentText) {
    return show(Alert.AlertType.INFORMATION, title, headerText, contentText);
  }

  @Contract(pure = true)
  public static Optional<ButtonType> showConfirmation(final @NotNull String title,
                                                      final @NotNull String headerText,
                                                      final @NotNull String contentText) {
    return show(Alert.AlertType.CONFIRMATION, title, headerText, contentText);
  }

  @Contract(pure = true)
  public static Optional<ButtonType> showError(final @NotNull String title,
                                               final @NotNull String headerText,
                                               final @NotNull String contentText) {
    return show(Alert.AlertType.ERROR, title, headerText, contentText);
  }
}
