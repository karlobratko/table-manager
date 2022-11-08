package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.ui.dialogs.CreateReservationDialog;
import hr.kbratko.tablemanager.ui.dialogs.CreateTableDialog;
import hr.kbratko.tablemanager.ui.models.*;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Strings;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationsController {
  @FXML
  private ListView<Reservation> lvReservations;
  @FXML
  private TextField             tfId;
  @FXML
  private TextField             tfReservationOn;
  @FXML
  private DatePicker            dpDate;
  @FXML
  private Spinner<Integer>      spHours;
  @FXML
  private Spinner<Integer>      spMinutes;
  @FXML
  private Spinner<Integer>      spNrSeats;
  @FXML
  private TextArea              taDescription;
  @FXML
  private ListView<Table>       lvRelatedTables;
  @FXML
  private Button                btnUpdate;
  @FXML
  private Button                btnDelete;
  @FXML
  private Button                btnSort;

  private final Validator               _validator    = new Validator();
  private final SortedList<Reservation> _reservations = new SortedList<>(FsReservationRepository.getInstance().getReservations(),
                                                                         Comparator.comparing(Reservation::getDate).reversed());

  private Stage       _stage;
  private boolean     _sortAsc = false;
  private Reservation _selectedReservation;


  @FXML
  protected void initialize() {
    initializeSpinner();
    initializeListView();
    initializeValidation();
    initializeButton();
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
              .dependsOn("reservationOn", tfReservationOn.textProperty())
              .dependsOn("date", dpDate.valueProperty())
              .dependsOn("hours", spHours.valueProperty())
              .dependsOn("minutes", spMinutes.valueProperty())
              .withMethod(c -> {
                final String    reservationOn = c.get("reservationOn");
                final LocalDate date          = c.get("date");
                final var       time          = LocalTime.of(c.get("hours"), c.get("minutes"));

                if (!Objects.isNull(_selectedReservation) &&
                    FsReservationRepository.getInstance()
                                           .getReservations()
                                           .stream()
                                           .filter(reservation -> !reservation.equals(_selectedReservation))
                                           .anyMatch(table -> table.getReservationOn().equals(reservationOn) &&
                                                              table.getDate().equals(date) &&
                                                              table.getTime().equals(time)))
                  c.error("Reservation name, date, and time must be unique");
              });
  }

  private void initializeSpinner() {
    spNrSeats.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    spHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 24, 1));
    spMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 5));
  }

  private void initializeListView() {
    lvReservations.setItems(_reservations);
    lvReservations.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
    lvReservations.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    lvReservations.getSelectionModel().selectFirst();
    lvReservations.setCellFactory(lv -> {
      final var cell = new ListCell<Reservation>() {
        @Override
        protected void updateItem(final Reservation reservation, final boolean empty) {
          super.updateItem(reservation, empty);

          if (!empty && Objects.nonNull(reservation)) {
            setText(reservation.toString());

            final var date = LocalDate.now();
            if (reservation.getDate().isBefore(date)) {
              setTextFill(Color.BLACK);
            }
            else if (reservation.getDate().isEqual(date)) {
              setTextFill(Color.RED);
              setText(String.format("%s (Today)", reservation.getReservationOn()));
            }
            else if (reservation.getDate().isBefore(date.plusDays(3)))
              setTextFill(Color.ORANGE);
            else
              setTextFill(Color.GREEN);
          }
          else {
            setText(null);
          }
        }
      };
      cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        lv.requestFocus(); if (!cell.isEmpty()) {
          int idx = cell.getIndex(); if (lv.getSelectionModel().getSelectedIndices().contains(idx)) {
            lv.getSelectionModel().clearSelection(idx);
          }
          else {
            lv.getSelectionModel().select(idx);
          } event.consume();
        }
      }); return cell;
    });

    lvRelatedTables.setItems(FsTableRepository.getInstance().getTables());
    lvRelatedTables.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    lvRelatedTables.setCellFactory(lv -> {
      final var cell = new ListCell<Table>() {
        @Override
        protected void updateItem(final Table table, final boolean empty) {
          super.updateItem(table, empty);

          setText(!empty && Objects.nonNull(table) ? String.format("%s (%d)", table.getName(), table.getNrSeats()) : null);
        }
      };
      cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        lv.requestFocus(); if (!cell.isEmpty()) {
          int idx = cell.getIndex(); if (lv.getSelectionModel().getSelectedIndices().contains(idx)) {
            lv.getSelectionModel().clearSelection(idx);
          }
          else {
            lv.getSelectionModel().select(idx);
          } event.consume();
        }
      }); return cell;
    });
  }

  private void initializeButton() {
    btnSort.setGraphic(FontIcon.of(FontAwesome.SORT_ASC));
    btnSort.setOnAction(actionEvent -> {
      _sortAsc = !_sortAsc;
      btnSort.setGraphic(_sortAsc ? FontIcon.of(FontAwesome.SORT_DESC) : FontIcon.of(FontAwesome.SORT_ASC));
      _reservations.setComparator(_reservations.getComparator().reversed());
    });
  }

  @FXML
  protected void onSelectedItemChanged(final ObservableValue<? extends Reservation> observableValue, final Reservation oldValue, final Reservation newValue) {
    if (Objects.nonNull(newValue)) {
      final var reservation = lvReservations.getSelectionModel().getSelectedItem();
      if (Objects.nonNull(reservation)) {
        tfId.setText(Integer.toString(reservation.getId())); tfReservationOn.setText(reservation.getReservationOn());
        dpDate.setValue(reservation.getDate()); spHours.getValueFactory().setValue(reservation.getTime().getHour());
        spMinutes.getValueFactory().setValue(reservation.getTime().getMinute());
        spNrSeats.getValueFactory().setValue(reservation.getNrSeats());
        taDescription.setText(reservation.getDescription());

        _selectedReservation = reservation;

        if (reservation.getDate().isBefore(LocalDate.now()))
          btnUpdate.setDisable(true);
        else
          btnUpdate.setDisable(false);
        selectListViewItems(reservation);
      }
    }
  }

  private void selectListViewItems(final Reservation reservation) {
    lvRelatedTables.getSelectionModel().clearSelection();

    final int[] ids    = FsTableReservationRepository.getInstance().getTableReservations().stream().filter(tr -> tr.getReservationId() == reservation.getId()).mapToInt(TableReservation::getTableId).toArray();
    final var   tables = FsTableRepository.getInstance().getTables().stream().filter(table -> Arrays.stream(ids).anyMatch(id -> id == table.getId())).toList();
    for (Table tbl : tables)
      lvRelatedTables.getSelectionModel().select(tbl);
  }

  @FXML
  protected void updateReservation() {
    final var reservation = lvReservations.getSelectionModel().getSelectedItem();
    if (Objects.nonNull(reservation)) {
      if (!_validator.validate()) {
        Alerts.showError("Update Reservation", "Error while trying to update Reservation.", getValidationMessages());
        return;
      }

      reservation.setReservationOn(tfReservationOn.getText()); reservation.setDate(dpDate.getValue());
      reservation.setTime(LocalTime.of(spHours.getValue(), spMinutes.getValue()));
      reservation.setNrSeats(spNrSeats.getValue()); reservation.setDescription(taDescription.getText());

      FsTableReservationRepository.getInstance().removeTableReservationIf(tr -> tr.getReservationId() == reservation.getId());
      for (Table table : lvRelatedTables.getSelectionModel().getSelectedItems())
        FsTableReservationRepository.getInstance().addTableReservation(TableReservation.of(table.getId(), reservation.getId()));

      final var selectedIndex = lvReservations.getSelectionModel().getSelectedIndex();
      final var items         = lvReservations.getItems();
      lvReservations.setItems(null);
      lvReservations.setItems(items);
      lvReservations.getSelectionModel().select(selectedIndex);
    }
  }


  private String getValidationMessages() {
    return _validator.validationResultProperty().get().getMessages().stream().map(msg -> msg.getSeverity().toString() + ": " + msg.getText()).collect(Collectors.joining("\n"));
  }

  @FXML
  protected void deleteReservation() {
    final var reservation = lvReservations.getSelectionModel().getSelectedItem();
    if (Objects.nonNull(reservation)) showAlertAndDelete(reservation);
  }

  private void showAlertAndDelete(final @NotNull Reservation reservation) {
    final Optional<ButtonType> btn = Alerts.showConfirmation("Delete Reservation", String.format("Delete item: %s?", reservation.getReservationOn()), "Are you sure? Press OK to confirm, or Cancel to Back out.");
    btn.ifPresent(type -> {
      if (type == ButtonType.OK) {
        FsTableReservationRepository.getInstance().removeTableReservationIf(tr -> tr.getReservationId() == reservation.getId());
        FsReservationRepository.getInstance().removeReservation(reservation);
      }
    });
  }

  @FXML
  protected void openTablesWindow() {
    try {
      final var loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/hr/kbratko/tablemanager/ui/views/tables-view.fxml")));
      _stage.setScene(new Scene(loader.load())); final TablesController controller = loader.getController();
      controller.setStage(_stage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void createNewReservation() {
    new CreateReservationDialog(_stage).showAndWait().ifPresent(result -> result.ifPresent(model -> {
      for (Table table : model.getRelatedTables())
        FsTableReservationRepository.getInstance().addTableReservation(TableReservation.of(table.getId(), model.getReservation().getId()));
      FsReservationRepository.getInstance().addReservation(model.getReservation());
    }));
  }

  @FXML
  protected void createNewTable() {
    new CreateTableDialog(_stage).showAndWait()
                                 .ifPresent(result -> result.ifPresent(table -> {
                                   FsTableRepository.getInstance().addTable(table);
                                   try {
                                     final var loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/hr/kbratko/tablemanager/ui/views/tables-view.fxml")));
                                     _stage.setScene(new Scene(loader.load()));
                                     final TablesController controller = loader.getController();
                                     controller.setStage(_stage);
                                     controller.selectTable(table);
                                   } catch (IOException e) {
                                     e.printStackTrace();
                                   }
                                 }));
  }
  
  @FXML
  protected void createDocumentation() {}

  @FXML
  protected void exitApplication() {Platform.exit();}

  public void setStage(final @NotNull Stage stage) {
    _stage = stage;
  }

  public void selectReservation(Reservation reservation) {
    lvReservations.getSelectionModel().select(reservation);
  }
}