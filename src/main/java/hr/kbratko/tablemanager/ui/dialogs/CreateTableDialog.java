package hr.kbratko.tablemanager.ui.dialogs;

import hr.kbratko.tablemanager.repository.model.Table;
import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Requests;
import hr.kbratko.tablemanager.utils.SpinnerValueFactories;
import hr.kbratko.tablemanager.utils.Strings;
import hr.kbratko.tablemanager.utils.Validations;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Window;
import net.synedra.validatorfx.Validator;

public class CreateTableDialog extends Dialog<Optional<Table>> {
  private final Logger logger = Logger.getLogger(CreateTableDialog.class.getName());
  private final Validator validator = new Validator();

  private Table table;

  @FXML
  private TextField tfName;

  @FXML
  private Spinner<Integer> spNrSeats;

  @FXML
  private TextArea taDescription;

  @FXML
  private ButtonType btnCreate;

  public CreateTableDialog(Window owner) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/hr/kbratko/tablemanager/ui/dialogs/create-table-dialog.fxml"));
      loader.setController(this);

      DialogPane dialogPane = loader.load();
      dialogPane.lookupButton(btnCreate).addEventFilter(ActionEvent.ANY, this::createNewTable);

      initOwner(owner);
      initModality(Modality.APPLICATION_MODAL);

      setResizable(false);
      setTitle("Create New Table");
      setDialogPane(dialogPane);
      setResultConverter(buttonType ->
        Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())
          ? Optional.ofNullable(table)
          : Optional.empty());

      setOnShowing(dialogEvent -> Platform.runLater(() -> tfName.requestFocus()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  public void createNewTable(ActionEvent event) {
    if (!validator.validate()) {
      Alerts.showError(
        "Create Table",
        "Error while trying to create table.",
        Validations.getMessages(validator)
      );

      event.consume();
      return;
    }

    try {
      final var response = Requests.send(
        Request.of(
          RequestOperation.CREATE_TABLE,
          Table.builder()
            .name(tfName.getText().trim())
            .nrSeats(spNrSeats.getValue())
            .description(taDescription.getText())
            .build()
        )
      );

      if (response.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Tables",
          "Error while trying to create table",
          response.getMessage()
        );

        event.consume();
        return;
      }

      table = (Table) response.getData();
    } catch (SocketTimeoutException e) {
      logger.log(
        Level.WARNING,
        "Socket timed out after %d ms".formatted(Requests.DEFAULT_TIMEOUT),
        e
      );
      event.consume();
    } catch (IOException e) {
      logger.log(
        Level.WARNING,
        "Could not connect to %s:%d".formatted(Requests.DEFAULT_HOST, Requests.DEFAULT_PORT),
        e
      );
      event.consume();
    } catch (ClassNotFoundException e) {
      logger.log(
        Level.SEVERE,
        "Could not cast response object",
        e
      );
      event.consume();
    }
  }

  @FXML
  private void initialize() {
    initializeSpinner();
    initializeValidation();
  }

  private void initializeSpinner() {
    spNrSeats.setValueFactory(SpinnerValueFactories.integer(1, 100, 1));
    spNrSeats.setEditable(true);
  }

  private void initializeValidation() {
    validator.createCheck()
      .dependsOn("name", tfName.textProperty())
      .withMethod(c -> {
        if (Strings.isNullOrBlank(c.get("name")))
          c.error("Please fill name field");
      })
      .decorates(tfName);

    validator.createCheck()
      .dependsOn("nrSeats", spNrSeats.valueProperty())
      .withMethod(c -> {
        if (Objects.isNull(c.get("nrSeats")))
          c.error("Please fill number of seats field");
      })
      .decorates(spNrSeats);
  }

  public ObjectProperty<Table> modelProperty() {
    return new SimpleObjectProperty<>(table);
  }
}
