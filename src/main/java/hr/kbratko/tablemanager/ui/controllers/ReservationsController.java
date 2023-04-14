package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.repository.ReservationRepository;
import hr.kbratko.tablemanager.repository.TableReservationRepository;
import hr.kbratko.tablemanager.repository.factory.ReservationRepositoryFactory;
import hr.kbratko.tablemanager.repository.factory.TableReservationRepositoryFactory;
import hr.kbratko.tablemanager.repository.model.Reservation;
import hr.kbratko.tablemanager.repository.model.Table;
import hr.kbratko.tablemanager.repository.model.TableReservation;
import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.ui.dialogs.CreateReservationDialog;
import hr.kbratko.tablemanager.ui.dialogs.CreateTableDialog;
import hr.kbratko.tablemanager.ui.infrastructure.Metadata;
import hr.kbratko.tablemanager.ui.viewmodel.ReservationViewModel;
import hr.kbratko.tablemanager.ui.viewmodel.TableViewModel;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Documentations;
import hr.kbratko.tablemanager.utils.Requests;
import hr.kbratko.tablemanager.utils.SpinnerValueFactories;
import hr.kbratko.tablemanager.utils.Strings;
import hr.kbratko.tablemanager.utils.Validations;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.jetbrains.annotations.NotNull;

public class ReservationsController implements BusinessController<Reservation, ReservationViewModel> {
  private static final Logger logger = Logger.getLogger(ReservationsController.class.getName());
  private final Validator validator = new Validator();

  private ObservableList<ReservationViewModel> reservations;
  private ObservableList<TableViewModel> tables;

  private Stage stage;
  private Metadata metadata;
  private User user;

  @FXML
  private ListView<ReservationViewModel> lvReservations;

  @FXML
  private TextField tfId;

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
  private Button btnUpdate;

  @FXML
  private Button btnDelete;

  @FXML
  protected void onSelectedItemChanged(final ObservableValue<? extends ReservationViewModel> observableValue,
                                       final ReservationViewModel oldValue,
                                       final ReservationViewModel newValue) {
    if (Objects.nonNull(newValue)) {
      final var reservation = lvReservations.getSelectionModel().getSelectedItem();

      if (Objects.nonNull(reservation)) {
        try {
          bindModel(reservation);
        } catch (Exception e) {
          logger.log(
            Level.SEVERE,
            "Unhandled exception",
            e
          );
        }

        btnUpdate.setDisable(
          reservation.getModel().getDate().isBefore(LocalDate.now())
        );
      }
    }
  }

  @FXML
  protected void updateReservation() {
    final var viewModel = lvReservations.getSelectionModel().getSelectedItem();
    if (Objects.nonNull(viewModel)) {
      if (!validator.validate()) {
        Alerts.showError("Update Reservation",
          "Error while trying to update Reservation.",
          Validations.getMessages(validator));
        return;
      }


      final var model = viewModel.getModel();
      model.setOwner(tfOwner.getText().trim());
      model.setDate(dpDate.getValue());
      model.setTime(LocalTime.of(spHours.getValue(), spMinutes.getValue()));
      model.setNrSeats(spNrSeats.getValue());

      if (!Strings.isNullOrEmpty((taDescription.getText())))
        model.setDescription(taDescription.getText().trim());

      try {
        final List<TableReservation> tableReservations = lvRelatedTables
          .getSelectionModel()
          .getSelectedItems()
          .stream()
          .map(TableViewModel::getModel)
          .map(table -> TableReservation.builder()
            .reservationId(model.getId())
            .tableId(table.getId())
            .build()
          )
          .toList();

        final var responseTableReservations = Requests.send(
          Request.of(
            RequestOperation.UPDATE_TABLE_RESERVATIONS,
            tableReservations
          )
        );

        if (responseTableReservations.getStatus() != ResponseStatus.OK_200) {
          Alerts.showError(
            "Error",
            "Error while trying to update reservation",
            responseTableReservations.getMessage()
          );

          return;
        }

        final var responseReservation = Requests.send(
          Request.of(
            RequestOperation.UPDATE_RESERVATION,
            model
          )
        );

        if (responseReservation.getStatus() != ResponseStatus.OK_200) {
          Alerts.showError(
            "Error",
            "Error while trying to update reservation",
            responseReservation.getMessage()
          );

          return;
        }

        lvReservations.refresh();
        selectListViewItems(viewModel);
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
      } catch (Exception e) {
        logger.log(
          Level.SEVERE,
          "Unhandled exception",
          e
        );
      }
    }
  }

  @FXML
  protected void deleteReservation() {
    final ReservationViewModel viewModel = lvReservations.getSelectionModel().getSelectedItem();
    if (Objects.nonNull(viewModel))
      showAlertAndDelete(viewModel);
  }

  @FXML
  protected void openTablesWindow() {
    try {
      final var loader = new FXMLLoader(
        Objects.requireNonNull(
          getClass().getResource("/hr/kbratko/tablemanager/ui/views/tables-view.fxml")
        )
      );
      stage.setScene(new Scene(loader.load()));

      final BusinessController<Table, TableViewModel> controller = loader.<TablesController>getController();
      controller.setStage(stage);
      controller.setMetadata(metadata);
      controller.setUser(user);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void createNewReservation() {
    new CreateReservationDialog(stage)
      .showAndWait()
      .ifPresent(result -> result
        .ifPresent(model -> {
          final var viewModel = new ReservationViewModel(model);

          reservations.add(viewModel);
          lvReservations.getSelectionModel().select(viewModel);
          lvReservations.getFocusModel().focus(lvReservations.getSelectionModel().getSelectedIndex());
          lvReservations.scrollTo(viewModel);

          //selectListViewItems(viewModel);
        }));
  }

  @FXML
  protected void createNewTable() {
    new CreateTableDialog(stage)
      .showAndWait()
      .ifPresent(result -> result
        .ifPresent(table -> {
          try {
            final var loader = new FXMLLoader(
              Objects.requireNonNull(
                getClass().getResource("/hr/kbratko/tablemanager/ui/views/tables-view.fxml")
              )
            );
            stage.setScene(new Scene(loader.load()));

            final BusinessController<Table, TableViewModel> controller = loader.<TablesController>getController();
            controller.setStage(stage);
            controller.setMetadata(metadata);
            controller.setUser(user);
            controller.selectListModel(new TableViewModel(table));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }));
  }

  @FXML
  protected void createDocumentation() {
    try {
      Documentations.generate(".", "documentation.html");
    } catch (IOException e) {
      Alerts.showError("Error",
        "Error while generating documentation",
        "Documentation could not be generated, could not access files");
    }
  }

  @FXML
  protected void exitApplication() {
    Platform.exit();
  }

  @Override
  public void initialize(final URL url, final ResourceBundle resourceBundle) {
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
      final var responseReservations = Requests.send(
        Request.of(
          RequestOperation.GET_ALL_RESERVATIONS,
          null
        )
      );

      if (responseReservations.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Error",
          "Error while trying to fetch all reservations",
          responseReservations.getMessage()
        );

        return;
      }

      reservations =
        FXCollections.observableArrayList(
          ((Collection<Reservation>) responseReservations.getData())
            .stream()
            .map(ReservationViewModel::new)
            .toList()
        );

      final var responseTables = Requests.send(
        Request.of(
          RequestOperation.GET_ALL_TABLES,
          null
        )
      );

      if (responseTables.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Error",
          "Error while trying to fetch all tables",
          responseTables.getMessage()
        );

        return;
      }

      tables =
        FXCollections.observableArrayList(
          ((Collection<Table>) responseTables.getData())
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
    lvReservations.setItems(reservations);
    lvReservations.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
    lvReservations.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    lvReservations.setCellFactory(lv -> {
      final var cell = new ListCell<ReservationViewModel>() {
        @Override
        protected void updateItem(final ReservationViewModel viewModel, final boolean empty) {
          super.updateItem(viewModel, empty);

          if (!empty && Objects.nonNull(viewModel)) {
            final var model = viewModel.getModel();

            setText(
              String.format(
                "%s (%s, %s)",
                model.getOwner(),
                model.getDate().format(DateTimeFormatter.ofPattern("MMM, dd yyyy")),
                model.getTime().format(DateTimeFormatter.ofPattern("HH:mm"))
              )
            );

            final var date = LocalDate.now();
            if (model.getDate().isBefore(date)) {
              setTextFill(Color.BLACK);
            } else if (model.getDate().isEqual(date)) {
              setTextFill(Color.RED);
              setText(String.format("%s (Today)", model.getOwner()));
            } else if (model.getDate().isBefore(date.plusDays(3)))
              setTextFill(Color.ORANGE);
            else
              setTextFill(Color.GREEN);
          } else {
            setText(null);
          }
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

    if (!lvReservations.getItems().isEmpty())
      lvReservations.getSelectionModel().selectFirst();

    lvRelatedTables.setItems(tables);
    lvRelatedTables.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

  private void showAlertAndDelete(final @NotNull ReservationViewModel viewModel) {
    final var model = viewModel.getModel();

    Alerts
      .showConfirmation("Delete Reservation",
        String.format("Delete item: %s?", model.getOwner()),
        "Are you sure? Press OK to confirm, or Cancel to back out.")
      .ifPresent(type -> {
        if (type == ButtonType.OK) {
          try {
            final var response = Requests.send(
              Request.of(
                RequestOperation.DELETE_RESERVATION,
                model
              )
            );

            if (response.getStatus() != ResponseStatus.OK_200) {
              Alerts.showError(
                "Error",
                "Error while trying to delete reservation",
                response.getMessage()
              );

              return;
            }

            reservations.remove(viewModel);
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
      });
  }

  private void bindModel(final @NotNull ReservationViewModel viewModel) throws Exception {
    final var model = viewModel.getModel();

    tfId.setText(Integer.toString(model.getId()));
    tfOwner.setText(model.getOwner());
    dpDate.setValue(model.getDate());
    spHours.getValueFactory().setValue(model.getTime().getHour());
    spMinutes.getValueFactory().setValue(model.getTime().getMinute());
    spNrSeats.getValueFactory().setValue(model.getNrSeats());
    taDescription.setText(model.getDescription());

    selectListViewItems(viewModel);
  }

  @SuppressWarnings("unchecked")
  private void selectListViewItems(final @NotNull ReservationViewModel viewModel) throws Exception {
    final var model = viewModel.getModel();

    lvRelatedTables.getSelectionModel().clearSelection();

    try {
      final var response = Requests.send(
        Request.of(
          RequestOperation.GET_ALL_TABLES_BY_RESERVATION_ID,
          model.getId()
        )
      );

      if (response.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Error",
          "Error while trying to fetch all tables of reservation",
          response.getMessage()
        );

        return;
      }

      for (
        final var table :
        ((Collection<Table>) response.getData())
          .stream()
          .map(TableViewModel::new)
          .collect(Collectors.toSet())
      )
        lvRelatedTables.getSelectionModel().select(table);
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

  public void setStage(final @NotNull Stage stage) {
    this.stage = stage;
  }

  @Override
  public void setMetadata(final @NotNull Metadata metadata) {
    this.metadata = metadata;
  }

  @Override
  public void setUser(final @NotNull User user) {
    this.user = user;
  }

  @Override
  public void selectListModel(@NotNull final ReservationViewModel viewModel) {
    lvReservations.getSelectionModel().select(viewModel);
    lvReservations.getFocusModel().focus(lvReservations.getSelectionModel().getSelectedIndex());
    lvReservations.scrollTo(viewModel);
  }
}