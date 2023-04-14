package hr.kbratko.tablemanager.ui.dialogs;

import hr.kbratko.tablemanager.repository.model.Reservation;
import hr.kbratko.tablemanager.repository.model.Table;
import hr.kbratko.tablemanager.repository.model.TableReservation;
import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.ui.viewmodel.TableViewModel;
import hr.kbratko.tablemanager.ui.viewmodel.ViewModel;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Requests;
import hr.kbratko.tablemanager.utils.SpinnerValueFactories;
import hr.kbratko.tablemanager.utils.Strings;
import hr.kbratko.tablemanager.utils.Validations;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Window;
import net.synedra.validatorfx.Validator;

public class CreateReservationDialog extends Dialog<Optional<Reservation>> {
  private static final Logger logger = Logger.getLogger(CreateReservationDialog.class.getName());
  private final Validator validator = new Validator();

  private Reservation reservation;

  private ObservableList<TableViewModel> tables;

  @FXML
  private TextField tfOwner;

  @FXML
  private DatePicker dpDate;

  @FXML
  private Spinner<Integer> spHours;

  @FXML
  private Spinner<Integer> spMinutes;

  @FXML
  private Spinner<Integer> spNrSeats;

  @FXML
  private TextArea taDescription;

  @FXML
  private ListView<TableViewModel> lvRelatedTables;

  @FXML
  private ButtonType btnCreate;

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
          ? Optional.of(reservation)
          : Optional.empty());

      setOnShowing(dialogEvent -> Platform.runLater(() -> tfOwner.requestFocus()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  public void createNewReservation(ActionEvent event) {
    if (!validator.validate()) {
      Alerts.showError("Create Reservation",
        "Error while trying to create reservation",
        Validations.getMessages(validator));
      event.consume();
    }

    try {
      final var responseReservation = Requests.send(
        Request.of(
          RequestOperation.CREATE_RESERVATION,
          Reservation.builder()
            .owner(tfOwner.getText().trim())
            .date(dpDate.getValue())
            .time(LocalTime.of(spHours.getValue(), spMinutes.getValue()))
            .nrSeats(spNrSeats.getValue())
            .description(taDescription.getText())
            .build()
        )
      );

      if (responseReservation.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Tables",
          "Error while trying to create reservation",
          responseReservation.getMessage()
        );

        event.consume();
        return;
      }

      reservation = (Reservation) responseReservation.getData();

      final List<TableReservation> reservationTables = lvRelatedTables
        .getSelectionModel()
        .getSelectedItems()
        .stream()
        .map(ViewModel::getModel)
        .map(table -> TableReservation.builder()
          .reservationId(reservation.getId())
          .tableId(table.getId())
          .build())
        .toList();

      final var responseTableReservations =
        Requests.send(
          Request.of(
            RequestOperation.CREATE_TABLE_RESERVATIONS,
            reservationTables
          )
        );

      if (responseReservation.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Tables",
          "Error while trying to create reservation",
          responseReservation.getMessage()
        );

        event.consume();
      }
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
    initializeData();
    initializeListView();
    initializeValidation();
  }

  private void initializeSpinner() {
    spNrSeats.setValueFactory(SpinnerValueFactories.integer(1, 100, 1));
    spNrSeats.setEditable(true);

    spHours.setValueFactory(SpinnerValueFactories.integer(1, 24, 1));
    spHours.setEditable(true);

    spMinutes.setValueFactory(SpinnerValueFactories.integer(0, 59, 5));
    spMinutes.setEditable(true);
  }

  @SuppressWarnings("unchecked")
  private void initializeData() {
    try {
      final var response = Requests.send(
        Request.of(
          RequestOperation.GET_ALL_TABLES,
          null
        )
      );

      if (response.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Error",
          "Error while trying to fetch all tables",
          response.getMessage()
        );

        return;
      }

      tables =
        FXCollections.observableList(
          ((Collection<Table>) response.getData())
            .stream()
            .map(TableViewModel::new)
            .toList()
        );
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
  }

  private void initializeListView() {
    lvRelatedTables.setItems(tables);

    lvRelatedTables.getSelectionModel()
      .setSelectionMode(SelectionMode.MULTIPLE);
    lvRelatedTables.setCellFactory(lv -> {
      final var cell = new ListCell<TableViewModel>() {
        @Override
        protected void updateItem(final TableViewModel viewModel, final boolean empty) {
          super.updateItem(viewModel, empty);
          setText(
            !empty && Objects.nonNull(viewModel)
              ? String.format("%s (%d)",
              viewModel.getModel().getName(),
              viewModel.getModel().getNrSeats())
              : null
          );
        }
      };

      cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        lv.requestFocus();
        if (!cell.isEmpty()) {
          int idx = cell.getIndex();
          if (lv.getSelectionModel().getSelectedIndices().contains(idx)) {
            lv.getSelectionModel().clearSelection(idx);
          } else {
            lv.getSelectionModel().select(idx);
          }

          event.consume();
        }
      });

      return cell;
    });
  }


  private void initializeValidation() {
    validator.createCheck()
      .dependsOn("owner", tfOwner.textProperty())
      .withMethod(c -> {
        if (Strings.isNullOrBlank(c.get("owner")))
          c.error("Please fill reservation on field");
      })
      .decorates(tfOwner);

    validator.createCheck()
      .dependsOn("date", dpDate.valueProperty())
      .withMethod(c -> {
        final LocalDate date = c.get("date");
        if (Objects.isNull(date))
          c.error("Please fill date field");
        else {
          if (LocalDate.now().isAfter(date))
            c.error("Date must be after current date");
        }
      })
      .decorates(dpDate);

    validator.createCheck()
      .dependsOn("date", dpDate.valueProperty())
      .dependsOn("hours", spHours.valueProperty())
      .dependsOn("minutes", spMinutes.valueProperty())
      .withMethod(c -> {
        final LocalDate date = c.get("date");
        final Integer hours = c.get("hours");
        final Integer minutes = c.get("minutes");
        final var time = LocalTime.of(hours, minutes);
        if (LocalDate.now().isAfter(date) && time.isBefore(LocalTime.now()))
          c.error("Time must be after current time");
      })
      .decorates(spHours)
      .decorates(spMinutes);
  }

  public ObjectProperty<Reservation> modelProperty() {
    return new SimpleObjectProperty<>(reservation);
  }
}
