package hr.kbratko.tablemanager.ui.dialogs;

import hr.kbratko.tablemanager.ui.models.FsTableRepository;
import hr.kbratko.tablemanager.ui.models.Table;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Strings;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreateTableDialog extends Dialog<Optional<Table>> {
  @FXML
  private TextField        tfId;
  @FXML
  private TextField        tfName;
  @FXML
  private Spinner<Integer> spNrSeats;
  @FXML
  private TextArea         taDescription;
  @FXML
  private ButtonType       btnCreate;

  private final ObjectProperty<Table> _table     = new SimpleObjectProperty<>(null);
  private final Validator             _validator = new Validator();

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
                           ? Optional.of(_table.get())
                           : Optional.empty());

      setOnShowing(dialogEvent -> Platform.runLater(() -> tfName.requestFocus()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  private void initialize() {
    initializeTextField();
    initializeSpinner();
    initializeValidation();
  }

  private void initializeTextField() {
    tfId.setText(
      Integer.toString(
        FsTableRepository.getInstance()
                         .getTables()
                         .stream()
                         .mapToInt(Table::getId)
                         .max()
                         .orElse(0) + 1));
  }

  private void initializeSpinner() {
    spNrSeats.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
  }

  private void initializeValidation() {
    _validator.createCheck()
              .dependsOn("name", tfName.textProperty())
              .withMethod(c -> {
                if (Strings.isNullOrBlank(c.get("name")))
                  c.error("Please fill name field");
              })
              .withMethod(c -> {
                if (FsTableRepository.getInstance().getTables().stream().anyMatch(table -> table.getName().equals(c.get("name"))))
                  c.error("Table name must be unique");
              })
              .decorates(tfName);
  }

  @FXML
  public void createNewTable(ActionEvent event) {
    if (!_validator.validate()) {
      Alerts.showError("Update Table",
                       "Error while trying to create table.",
                       getValidationMessages());
      event.consume();
    }

    _table.set(
      new Table
        .Builder(Integer.parseInt(tfId.getText()),
                 tfName.getText().trim(),
                 spNrSeats.getValue())
        .description(taDescription.getText())
        .build());
  }

  private String getValidationMessages() {
    return _validator.validationResultProperty()
                     .get()
                     .getMessages()
                     .stream()
                     .map(msg -> msg.getSeverity().toString() + ": " + msg.getText())
                     .collect(Collectors.joining("\n"));
  }

  public ObjectProperty<Table> tableProperty() {return _table;}
}
