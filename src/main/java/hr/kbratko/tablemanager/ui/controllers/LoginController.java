package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Strings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoginController {
  @FXML
  private TextField     tfUsername;
  @FXML
  private PasswordField pfPassword;

  private       Stage     _stage;
  private final Validator _validator = new Validator();

  @FXML
  protected void initialize() {
    initializeValidation();
  }

  private void initializeValidation() {
    _validator.createCheck()
              .dependsOn("username", tfUsername.textProperty())
              .withMethod(c -> {
                if (Strings.isNullOrEmpty(c.get("username")))
                  c.error("Please fill username field");
              })
              .decorates(tfUsername);

    _validator.createCheck()
              .dependsOn("password", pfPassword.textProperty())
              .withMethod(c -> {
                final String password = c.get("password");
                if (password.length() < 8)
                  c.error("Password should be at least 8 characters long");
              })
              .decorates(pfPassword);

    // TODO: remove _validator (TESTING only)
    _validator.createCheck()
              .dependsOn("username", tfUsername.textProperty())
              .dependsOn("password", pfPassword.textProperty())
              .withMethod(c -> {
                final String username = c.get("username");
                final String password = c.get("password");
                if (!username.equals("kbratko") || !password.equals("Pa$$w0rd"))
                  c.error("Invalid username or password");
              })
              .decorates(tfUsername)
              .decorates(pfPassword);
  }

  @FXML
  protected void onLoginBtnClick() {
    if (!_validator.validate()) {
      Alerts.showError("Login",
                       "Error while trying to login.",
                       getValidationMessages());
      return;
    }

    try {
      final var loader =
        new FXMLLoader(
          Objects.requireNonNull(
            getClass().getResource("/hr/kbratko/tablemanager/ui/views/tables-view.fxml")));
      _stage.setScene(new Scene(loader.load()));
      final TablesController controller = loader.getController();
      controller.setStage(_stage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getValidationMessages() {
    return _validator.validationResultProperty()
                     .get()
                     .getMessages()
                     .stream()
                     .map(msg -> msg.getSeverity().toString() + ": " + msg.getText())
                     .collect(Collectors.joining("\n"));
  }

  @FXML
  protected void onRegisterBtnClick() {
    try {
      final var loader =
        new FXMLLoader(
          Objects.requireNonNull(
            getClass().getResource("/hr/kbratko/tablemanager/ui/views/register-view.fxml")));
      _stage.setScene(new Scene(loader.load()));
      final RegisterController controller = loader.getController();
      controller.setStage(_stage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setStage(final @NotNull Stage stage) {
    _stage = stage;
  }
}