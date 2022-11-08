package hr.kbratko.tablemanager.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {
  private Stage _stage;

  @FXML
  protected void onLoginBtnClick() {
    try {
      final var loader =
        new FXMLLoader(
          Objects.requireNonNull(
            getClass().getResource("/hr/kbratko/tablemanager/ui/views/login-view.fxml")));
      _stage.setScene(new Scene(loader.load()));
      final LoginController controller = loader.getController();
      controller.setStage(_stage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void onRegisterBtnClick() {}

  public void setStage(final @NotNull Stage stage) {
    _stage = stage;
  }
}
