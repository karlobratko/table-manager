package hr.kbratko.tablemanager.ui.dialogs;

import hr.kbratko.tablemanager.ui.models.*;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Strings;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Window;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreateReservationDialog extends Dialog<Optional<CreateReservationModel>> {
  @FXML
  private TextField        tfId;
  @FXML
  private TextField        tfReservationOn;
  @FXML
  private DatePicker       dpDate;
  @FXML
  private Spinner<Integer> spHours;
  @FXML
  private Spinner<Integer> spMinutes;
  @FXML
  private Spinner<Integer> spNrSeats;
  @FXML
  private TextArea         taDescription;
  @FXML
  private ListView<Table>  lvRelatedTables;
  @FXML
  private ButtonType       btnCreate;

  private final ObjectProperty<CreateReservationModel> _createReservationModel = new SimpleObjectProperty<>(null);
  private final Validator                              _validator              = new Validator();

  public CreateReservationDialog(Window owner) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/hr/kbratko/tablemanager/ui/dialogs/create-reservation-dialog.fxml"));
      loader.setController(this);

      DialogPane dialogPane = loader.load();
      dialogPane.lookupButton(btnCreate).addEventFilter(ActionEvent.ANY, this::createNewReservation);

      initOwner(owner);
      initModality(Modality.APPLICATION_MODAL);

      setResizable(false);
      setTitle("Create New Reservation");
      setDialogPane(dialogPane);
      setResultConverter(buttonType ->
                           Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())
                           ? Optional.of(_createReservationModel.get())
                           : Optional.empty());

      setOnShowing(dialogEvent -> Platform.runLater(() -> tfReservationOn.requestFocus()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  private void initialize() {
    initializeTextField();
    initializeSpinner();
    initializeListView();
    initializeValidation();
  }

  private void initializeTextField() {
    tfId.setText(
      Integer.toString(
        FsReservationRepository.getInstance()
                               .getReservations()
                               .stream()
                               .mapToInt(Reservation::getId)
                               .max()
                               .orElse(0) + 1));
  }

  private void initializeSpinner() {
    spNrSeats.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    spHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 24, 1));
    spMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 5));
  }

  private void initializeListView() {
    lvRelatedTables.setItems(FsTableRepository.getInstance().getTables());
    lvRelatedTables.getSelectionModel()
                   .setSelectionMode(SelectionMode.MULTIPLE);
    lvRelatedTables.setCellFactory(lv -> {
      final var cell = new ListCell<Table>() {
        @Override
        protected void updateItem(final Table table, final boolean empty) {
          super.updateItem(table, empty);
          setText(!empty && Objects.nonNull(table) ? String.format("%s (%d)", table.getName(), table.getNrSeats()) : null);
        }
      };
      cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        lv.requestFocus();
        if (!cell.isEmpty()) {
          int idx = cell.getIndex();
          if (lv.getSelectionModel().getSelectedIndices().contains(idx)) {
            lv.getSelectionModel().clearSelection(idx);
          }
          else {
            lv.getSelectionModel().select(idx);
          }
          event.consume();
        }
      });
      return cell;
    });
  }


  private void initializeValidation() {
    _validator.createCheck()
              .dependsOn("reservationOn", tfReservationOn.textProperty())
              .withMethod(c -> {
                if (Strings.isNullOrBlank(c.get("reservationOn")))
                  c.error("Please fill reservation on field");
              })
              .decorates(tfReservationOn);
    _validator.createCheck()
              .dependsOn("date", dpDate.valueProperty())
              .withMethod(c -> {
                final LocalDate date = c.get("date");
                if (Objects.isNull(date))
                  c.error("Please fill date field");
                else {
                  if (LocalDate.now().isAfter(date))
                    c.error("Date must be after todays date");
                }
              })
              .decorates(dpDate);
    _validator.createCheck()
              .dependsOn("date", dpDate.valueProperty())
              .dependsOn("hours", spHours.valueProperty())
              .dependsOn("minutes", spMinutes.valueProperty())
              .withMethod(c -> {
                final LocalDate date = c.get("date");
                final var       time = LocalTime.of(c.get("hours"), c.get("minutes"));
                if (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now()))
                  c.error("Cannot create reservation in past");
              })
              .decorates(dpDate);
    _validator.createCheck()
              .dependsOn("reservationOn", tfReservationOn.textProperty())
              .dependsOn("date", dpDate.valueProperty())
              .dependsOn("hours", spHours.valueProperty())
              .dependsOn("minutes", spMinutes.valueProperty())
              .withMethod(c -> {
                final String    reservationOn = c.get("reservationOn");
                final LocalDate date          = c.get("date");
                final var       time          = LocalTime.of(c.get("hours"), c.get("minutes"));
                if (FsReservationRepository.getInstance()
                                           .getReservations()
                                           .stream()
                                           .anyMatch(table -> table.getReservationOn().equals(reservationOn) &&
                                                              table.getDate().equals(date) &&
                                                              table.getTime().equals(time)))
                  c.error("Reservation name, date, and time must be unique");
              });
  }

  @FXML
  public void createNewReservation(ActionEvent event) {
    if (!_validator.validate()) {
      Alerts.showError("Create Reservation",
                       "Error while trying to create reservation.",
                       getValidationMessages());
      event.consume();
    }
    else {

      _createReservationModel.set(
        CreateReservationModel.of(
          new Reservation.Builder(Integer.parseInt(tfId.getText()),
                                  tfReservationOn.getText(),
                                  dpDate.getValue(),
                                  LocalTime.of(spHours.getValue(), spMinutes.getValue()),
                                  spNrSeats.getValue())
            .description(taDescription.getText())
            .build(),
          lvRelatedTables.getSelectionModel().getSelectedItems()));
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

  public ObjectProperty<CreateReservationModel> modelProperty() {return _createReservationModel;}
}
