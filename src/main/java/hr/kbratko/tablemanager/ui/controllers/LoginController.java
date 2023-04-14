package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.repository.UserRepository;
import hr.kbratko.tablemanager.repository.factory.UserRepositoryFactory;
import hr.kbratko.tablemanager.repository.model.Table;
import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.ui.infrastructure.Metadata;
import hr.kbratko.tablemanager.ui.viewmodel.TableViewModel;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Requests;
import hr.kbratko.tablemanager.utils.Strings;
import hr.kbratko.tablemanager.utils.Validations;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
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

public class LoginController implements Controller {
  private static final Logger logger = Logger.getLogger(LoginController.class.getName());
  private final Validator validator = new Validator();

  private Stage stage;
  private Metadata metadata;
  private User loggedInUser;

  @FXML
  private TextField tfEmail;

  @FXML
  private PasswordField pfPassword;

  @FXML
  protected void onLoginBtnClick() {
    if (!validator.validate()) {
      Alerts.showError("Login",
        "Error while trying to login.",
        Validations.getMessages(validator));
      return;
    }

    logger.info("Logging in as %s, %s (%s)".formatted(loggedInUser.getType(), loggedInUser.getFullName(), loggedInUser.getEmail()));

    try {
      final var loader =
        new FXMLLoader(
          Objects.requireNonNull(
            getClass().getResource("/hr/kbratko/tablemanager/ui/views/tables-view.fxml")
          )
        );
      stage.setScene(new Scene(loader.load()));

      final BusinessController<Table, TableViewModel> controller = loader.<TablesController>getController();
      controller.setStage(stage);
      controller.setMetadata(metadata);
      controller.setUser(loggedInUser);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void onRegisterBtnClick() {
    try {
      final var loader =
        new FXMLLoader(
          Objects.requireNonNull(
            getClass().getResource("/hr/kbratko/tablemanager/ui/views/register-view.fxml")
          )
        );
      stage.setScene(new Scene(loader.load()));

      final RegisterController controller = loader.getController();
      controller.setStage(stage);
      controller.setMetadata(metadata);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initialize(final URL url, final ResourceBundle resourceBundle) {
    initializeValidation();
  }

  private void initializeValidation() {
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
        if (password.length() < 8)
          c.error("Password should be at least 8 characters long");
      })
      .decorates(pfPassword);

    validator.createCheck()
      .dependsOn("email", tfEmail.textProperty())
      .dependsOn("password", pfPassword.textProperty())
      .withMethod(c -> {
        final String email = c.get("email");
        final String password = c.get("password");

        try {
          final var response = Requests.send(
            Request.of(
              RequestOperation.LOGIN,
              User.builder()
                .email(email)
                .password(password)
                .build()
            )
          );

          Optional<User> user =
            response.getStatus() == ResponseStatus.OK_200
              ? Optional.of((User) response.getData())
              : Optional.empty();

          if (user.isEmpty())
            c.error(response.getMessage());
          else {
            loggedInUser = user.get();
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
      })
      .decorates(tfEmail)
      .decorates(pfPassword);
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