package hr.kbratko.tablemanager.ui.dialogs;

import hr.kbratko.tablemanager.repository.ReservationRepository;
import hr.kbratko.tablemanager.repository.TableReservationRepository;
import hr.kbratko.tablemanager.repository.factory.ReservationRepositoryFactory;
import hr.kbratko.tablemanager.repository.factory.TableReservationRepositoryFactory;
import hr.kbratko.tablemanager.repository.model.Reservation;
import hr.kbratko.tablemanager.repository.model.Table;
import hr.kbratko.tablemanager.repository.model.TableReservation;
import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.ui.viewmodel.ReservationViewModel;
import hr.kbratko.tablemanager.ui.viewmodel.TableViewModel;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Requests;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class TableReservationsDialog extends Dialog<ButtonType> {
  private static final Logger logger = Logger.getLogger(TableReservationsDialog.class.getName());
  private final Table                      table;

  private ObservableList<ReservationViewModel> reservations;

  @FXML
  private ListView<LocalDate> lvDates;

  @FXML
  private TableView<ReservationViewModel> tvReservations;

  @FXML
  private TableColumn<ReservationViewModel, Integer> tcId;

  @FXML
  private TableColumn<ReservationViewModel, String> tcOwner;

  @FXML
  private TableColumn<ReservationViewModel, LocalDate> tcDate;

  @FXML
  private TableColumn<ReservationViewModel, LocalTime> tcTime;

  @FXML
  private TableColumn<ReservationViewModel, Integer> tcNrSeats;


  public TableReservationsDialog(Window owner, final @NotNull Table table) {
    try {
      this.table = table;

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/hr/kbratko/tablemanager/ui/dialogs/table-reservations-dialog.fxml"));
      loader.setController(this);

      DialogPane dialogPane = loader.load();

      initOwner(owner);
      initModality(Modality.APPLICATION_MODAL);

      setResizable(false);
      setTitle("Table Reservations");
      setDialogPane(dialogPane);

      initializeData();
      initializeListView();
      initializeTableCells();
      initializeTableValues();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private void initializeData() {
    try {
      final var response = Requests.send(
        Request.of(
          RequestOperation.GET_ALL_RESERVATIONS_BY_TABLE_ID,
          table.getId()
        )
      );

      if (response.getStatus() != ResponseStatus.OK_200) {
        Alerts.showError(
          "Error",
          "Error while trying to fetch all reservations of table",
          response.getMessage()
        );

        return;
      }

      this.reservations =
        FXCollections.observableArrayList(
          ((Collection<Reservation>) response.getData())
            .stream()
            .map(ReservationViewModel::new)
            .toList()
        );
    } catch (SocketTimeoutException e) {
      logger.log(
        Level.WARNING,
        "Socket timed out after %d ms".formatted(Requests.DEFAULT_TIMEOUT),
        e
      );
      this.close();
    } catch (IOException e) {
      logger.log(
        Level.WARNING,
        "Could not connect to %s:%d".formatted(Requests.DEFAULT_HOST, Requests.DEFAULT_PORT),
        e
      );
      this.close();
    } catch (ClassNotFoundException e) {
      logger.log(
        Level.SEVERE,
        "Could not cast response object",
        e
      );
      this.close();
    }
  }

  private void initializeListView() {
    lvDates.setItems(
      FXCollections.observableList(
        reservations.stream()
                    .map(viewModel -> viewModel.getModel().getDate())
                    .distinct()
                    .sorted((o1, o2) -> -o1.compareTo(o2))
                    .toList()
      )
    );
    lvDates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    lvDates.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);

    if (!lvDates.getItems().isEmpty())
      lvDates.getSelectionModel().selectFirst();

    lvDates.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(final LocalDate date, final boolean empty) {
        super.updateItem(date, empty);

        final var now = LocalDate.now();
        if (!empty) {
          setText(date.format(DateTimeFormatter.ofPattern("MMM, dd yyyy")));

          if (date.isBefore(now))
            setTextFill(Color.LIGHTGRAY);
          else if (date.isEqual(now)) {
            setTextFill(Color.RED);
            setText("Today");
          } else if (date.isBefore(now.plusDays(3)))
            setTextFill(Color.ORANGE);
          else
            setTextFill(Color.GREEN);
        } else {
          setText(null);
        }
      }
    });
  }

  private void onSelectedItemChanged(final ObservableValue<? extends LocalDate> observableValue, final LocalDate oldValue, final LocalDate newValue) {
    if (Objects.nonNull(newValue))
      initializeTableValues();
  }

  private void initializeTableCells() {
    tcId.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
    tcOwner.setCellValueFactory(cell -> cell.getValue().ownerProperty());
    tcDate.setCellValueFactory(cell -> cell.getValue().dateProperty());
    tcTime.setCellValueFactory(cell -> cell.getValue().timeProperty());
    tcNrSeats.setCellValueFactory(cell -> cell.getValue().nrSeatsProperty().asObject());
  }

  private void initializeTableValues() {
    FilteredList<ReservationViewModel> filteredReservations =
      reservations.filtered(viewModel -> viewModel.getModel().getDate().isEqual(lvDates.getSelectionModel().getSelectedItem()));
    SortedList<ReservationViewModel> sortedReservations =
      new SortedList<>(filteredReservations,
                       Comparator.comparing(o -> o.getModel().getDate()));
    tvReservations.setItems(sortedReservations);
    sortedReservations.comparatorProperty().bind(tvReservations.comparatorProperty());
  }
}
