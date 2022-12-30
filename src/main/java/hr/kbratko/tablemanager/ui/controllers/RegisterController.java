package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.ui.infrastructure.Metadata;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Requests;
import hr.kbratko.tablemanager.utils.Strings;
import hr.kbratko.tablemanager.utils.Validations;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.jetbrains.annotations.NotNull;

public class RegisterController implements Controller {
  private final Logger logger = Logger.getLogger(RegisterController.class.getName());
  private final Validator validator = new Validator();

  private Stage stage;
  private Metadata metadata;

  @FXML
  private TextField tfFirstName;

  @FXML
  private TextField tfLastName;

  @FXML
  private TextField tfEmail;

  @FXML
  private PasswordField pfPassword;

  @FXML
  private PasswordField pfConfirmPassword;

  @FXML
  protected void onLoginBtnClick() {
    navigateToLoginController();
  }

  @FXML
  protected void onRegisterBtnClick() {
    if (!validator.validate()) {
      Alerts.showError("Register",
        "Error while trying to register.",
        Validations.getMessages(validator));
      return;
    }

    try {
      final var response = Requests.send(
        Request.of(
          RequestOperation.REGISTER,
          User.builder()
            .firstName(tfFirstName.getText().trim())
            .lastName(tfLastName.getText().trim())
            .email(tfEmail.getText().trim())
            .password(pfPassword.getText().trim())
            .build()
        )
      );

      if (response.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Register",
          "Error while trying to register",
          response.getMessage()
        );
        
        return;
      }
    } catch (SocketTimeoutException e) {
      logger.log(
        Level.WARNING,
        "Socket timed out after %d ms".formatted(Requests.DEFAULT_TIMEOUT),
        e
      );
    } catch (IOException e) {
      logger.log(
        Level.WARNING,
        "Could not connect to %s:%d".formatted(Requests.DEFAULT_HOST, Requests.DEFAULT_PORT),
        e
      );
    } catch (ClassNotFoundException e) {
      logger.log(
        Level.SEVERE,
        "Could not cast response object",
        e
      );
    }

    navigateToLoginController();
  }

  @Override
  public void initialize(final URL url, final ResourceBundle resourceBundle) {
    initializeValidation();
  }

  private void initializeValidation() {
    validator.createCheck()
      .dependsOn("firstname", tfFirstName.textProperty())
      .withMethod(c -> {
        if (Strings.isNullOrEmpty(c.get("firstname")))
          c.error("Please fill first name field");
      })
      .decorates(tfFirstName);

    validator.createCheck()
      .dependsOn("lastname", tfLastName.textProperty())
      .withMethod(c -> {
        if (Strings.isNullOrEmpty(c.get("lastname")))
          c.error("Please fill last name field");
      })
      .decorates(tfLastName);

    validator.createCheck()
      .dependsOn("email", tfEmail.textProperty())
      .withMethod(c -> {
        if (Strings.isNullOrEmpty(c.get("email")))
          c.error("Please fill email field");
      })
      .decorates(tfEmail);

    validator.createCheck()
      .dependsOn("password", pfPassword.textProperty())
      .withMethod(c -> {
        final String password = c.get("password");
        if (Strings.isNullOrEmpty(password) || password.length() < 8)
          c.error("Password should be at least 8 characters long");
      })
      .decorates(pfPassword);

    validator.createCheck()
      .dependsOn("password", pfPassword.textProperty())
      .dependsOn("confirmpassword", pfConfirmPassword.textProperty())
      .withMethod(c -> {
        final String password = c.get("password");
        final String confirmPassword = c.get("confirmpassword");
        if (Strings.isNullOrEmpty(password) ||
          Strings.isNullOrEmpty(confirmPassword) ||
          !password.equals(confirmPassword))
          c.error("Please confirm your password");
      })
      .decorates(pfConfirmPassword);
  }

  private void navigateToLoginController() {
    try {
      final var loader =
        new FXMLLoader(
          Objects.requireNonNull(
            getClass().getResource("/hr/kbratko/tablemanager/ui/views/login-view.fxml")
          )
        );
      stage.setScene(new Scene(loader.load()));

      final Controller controller = loader.<LoginController>getController();
      controller.setStage(stage);
      controller.setMetadata(metadata);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setStage(final @NotNull Stage stage) {
    this.stage = stage;
  }

  @Override
  public void setMetadata(final @NotNull Metadata metadata) {
    this.metadata = metadata;
  }
}
