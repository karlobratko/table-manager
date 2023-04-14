package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.repository.model.Reservation;
import hr.kbratko.tablemanager.repository.model.Table;
import hr.kbratko.tablemanager.repository.model.TableHistoryModel;
import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.ui.dialogs.ChatDialog;
import hr.kbratko.tablemanager.ui.dialogs.CreateReservationDialog;
import hr.kbratko.tablemanager.ui.dialogs.CreateTableDialog;
import hr.kbratko.tablemanager.ui.dialogs.TableReservationsDialog;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.jetbrains.annotations.NotNull;

public class TablesController implements BusinessController<Table, TableViewModel> {
  private static final Logger logger = Logger.getLogger(TablesController.class.getName());
  private final Validator validator = new Validator();
  private ObservableList<TableViewModel> tables;

  private Stage stage;
  private Metadata metadata;
  private User user;

  @FXML
  private ListView<TableViewModel> lvTables;

  @FXML
  private TextField tfId;

  @FXML
  private TextField tfName;

  @FXML
  private Spinner<Integer> spNrSeats;

  @FXML
  private TextArea taDescription;

  @FXML
  protected void onSelectedItemChanged(final ObservableValue<? extends TableViewModel> observableValue, final TableViewModel oldValue, final TableViewModel newValue) {
    if (Objects.nonNull(newValue)) {
      final TableViewModel viewModel = lvTables.getSelectionModel().getSelectedItem();

      if (Objects.nonNull(viewModel)) {
        bindModel(viewModel);
      }
    }
  }

  @FXML
  protected void showRelatedReservations() {
    if (Objects.nonNull(lvTables.getSelectionModel().getSelectedItem()))
      new TableReservationsDialog(stage, lvTables.getSelectionModel().getSelectedItem().getModel()).showAndWait();
  }

  @FXML
  protected void updateTableData() {
    final TableViewModel viewModel = lvTables.getSelectionModel().getSelectedItem();

    if (Objects.nonNull(viewModel)) {
      if (!validator.validate()) {
        Alerts.showError("Update Table",
          "Error while trying to update Table.",
          Validations.getMessages(validator));
        return;
      }

      final var model = viewModel.getModel();
      model.setName(tfName.getText().trim());
      model.setNrSeats(spNrSeats.getValue());

      if (!Strings.isNullOrEmpty(taDescription.getText()))
        model.setDescription(taDescription.getText().trim());

      try {
        final var response = Requests.send(
          Request.of(
            RequestOperation.UPDATE_TABLE,
            model
          )
        );

        if (response.getStatus() != ResponseStatus.OK_200) {
          Alerts.showError(
            "Error",
            "Error while trying to update table",
            response.getMessage()
          );

          return;
        }

        lvTables.refresh();
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
  }

  @FXML
  protected void deleteTable() {
    final TableViewModel viewModel = lvTables.getSelectionModel().getSelectedItem();
    if (Objects.nonNull(viewModel)) {
      showAlertAndDelete(viewModel);
    }
  }

  @FXML
  protected void createNewTable() {
    new CreateTableDialog(stage)
      .showAndWait()
      .ifPresent(result -> result
        .ifPresent(table -> {
          final TableViewModel viewModel = new TableViewModel(table);

          tables.add(viewModel);
          lvTables.getSelectionModel().select(viewModel);
          lvTables.getFocusModel().focus(lvTables.getSelectionModel().getSelectedIndex());
          lvTables.scrollTo(viewModel);
        }));
  }

  @FXML
  protected void createNewReservation() {
    new CreateReservationDialog(stage)
      .showAndWait()
      .ifPresent(result -> result
        .ifPresent(model -> {
          try {
            final var loader = new FXMLLoader(
              Objects.requireNonNull(
                getClass().getResource("/hr/kbratko/tablemanager/ui/views/reservations-view.fxml")
              )
            );
            stage.setScene(new Scene(loader.load()));

            final BusinessController<Reservation, ReservationViewModel> controller = loader.<ReservationsController>getController();
            controller.setStage(stage);
            controller.setMetadata(metadata);
            controller.setUser(user);
            controller.selectListModel(new ReservationViewModel(model));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }));
  }

  @FXML
  protected void openChat() {
    new ChatDialog(stage, user).showAndWait();
  }

  @FXML
  protected void openReservationsWindow() {
    try {
      final var loader = new FXMLLoader(
        Objects.requireNonNull(
          getClass().getResource("/hr/kbratko/tablemanager/ui/views/reservations-view.fxml")
        )
      );
      stage.setScene(new Scene(loader.load()));

      final BusinessController<Reservation, ReservationViewModel> controller = loader.<ReservationsController>getController();
      controller.setStage(stage);
      controller.setMetadata(metadata);
      controller.setUser(user);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void undoLastAction() {
    try {
      final var response = Requests.send(
        Request.of(
          RequestOperation.UNDO_TABLE_ACTION,
          null
        )
      );

      if (response.getStatus() == ResponseStatus.NO_CONTENT_204) {
        Alerts.showInformation(
          "Nothing to undo",
          "Could not undo, because there is no history",
          response.getMessage()
        );

        return;
      }

      final var history = (TableHistoryModel) response.getData();
      switch (history.getAction()) {
        case CREATE -> {
          final TableViewModel viewModel = new TableViewModel(history.getTable());

          tables.add(viewModel);
          lvTables.getSelectionModel().select(viewModel);
          lvTables.getFocusModel().focus(lvTables.getSelectionModel().getSelectedIndex());
          lvTables.scrollTo(viewModel);
        }
        case DELETE -> {
          tables.remove(new TableViewModel(history.getTable()));
        }
        case UPDATE -> {
          final var newViewModel = new TableViewModel(history.getTable());
          tables.removeIf(viewModel -> Objects.equals(viewModel.getModel(), history.getTable()));

          tables.add(newViewModel);
          lvTables.getSelectionModel().select(newViewModel);
          lvTables.getFocusModel().focus(lvTables.getSelectionModel().getSelectedIndex());
          lvTables.scrollTo(newViewModel);
        }
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
        FXCollections.observableArrayList(
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
    lvTables.setItems(
      new SortedList<>(
        tables,
        Comparator.comparing((TableViewModel o) -> o.getModel().getNrSeats()).reversed()
      )
    );
    lvTables.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
    lvTables.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    lvTables.setCellFactory(lv -> new ListCell<>() {
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
    });

    if (!lvTables.getItems().isEmpty())
      lvTables.getSelectionModel().selectFirst();
  }

  private void initializeValidation() {
    validator.createCheck()
      .dependsOn("name", tfName.textProperty())
      .withMethod(c -> {
        if (Strings.isNullOrBlank(c.get("name")))
          c.error("Please fill name field");
      }).decorates(tfName);
  }

  private void showAlertAndDelete(final @NotNull TableViewModel viewModel) {
    final var model = viewModel.getModel();

    Alerts.showConfirmation(
        "Delete Table",
        String.format("Delete item: %s?", model.getName()),
        "Are you sure? Press OK to confirm, or Cancel to back out."
      )
      .ifPresent(type -> {
        if (type == ButtonType.OK) {
          try {
            final var response = Requests.send(
              Request.of(
                RequestOperation.DELETE_TABLE,
                model
              )
            );

            if (response.getStatus() != ResponseStatus.OK_200) {
              Alerts.showError(
                "Error",
                "Error while trying to delete table",
                response.getMessage()
              );

              return;
            }

            tables.remove(viewModel);
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

  private void bindModel(final @NotNull TableViewModel viewModel) {
    final var model = viewModel.getModel();
    tfId.setText(Integer.toString(model.getId()));
    tfName.setText(model.getName());
    spNrSeats.getValueFactory().setValue(model.getNrSeats());
    taDescription.setText(model.getDescription());
  }

  @Override
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
  public void selectListModel(final @NotNull TableViewModel viewModel) {
    lvTables.getSelectionModel().select(viewModel);
    lvTables.getFocusModel().focus(lvTables.getSelectionModel().getSelectedIndex());
    lvTables.scrollTo(viewModel);
  }
}
