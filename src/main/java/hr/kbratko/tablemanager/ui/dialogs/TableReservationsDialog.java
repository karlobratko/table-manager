package hr.kbratko.tablemanager.ui.dialogs;

import hr.kbratko.tablemanager.ui.models.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
  @FXML
  private ListView<LocalDate>                 lvDates;
  @FXML
  private TableView<Reservation>              tvReservations;
  @FXML
  private TableColumn<Reservation, Integer>   tcId;
  @FXML
  private TableColumn<Reservation, String>    tcReservationOn;
  @FXML
  private TableColumn<Reservation, LocalDate> tcDate;
  @FXML
  private TableColumn<Reservation, LocalTime> tcTime;
  @FXML
  private TableColumn<Reservation, Integer>   tcNrSeats;

  private final Table                       _table;
  private final ObservableList<Reservation> _reservations;


  public TableReservationsDialog(Window owner, final @NotNull Table table) {
    try {
      _table = table;
      final int[] reservationIds = FsTableReservationRepository.getInstance()
                                                               .getTableReservations()
                                                               .stream()
                                                               .filter(tr -> tr.getTableId() == _table.getId())
                                                               .mapToInt(TableReservation::getReservationId)
                                                               .toArray();
      _reservations = FsReservationRepository.getInstance()
                                             .getReservations()
                                             .stream()
                                             .filter(reservation -> Arrays.stream(reservationIds).anyMatch(id -> reservation.getId() == id))
                                             .collect(Collectors.toCollection(FXCollections::observableArrayList));
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/hr/kbratko/tablemanager/ui/dialogs/table-reservations-dialog.fxml"));
      loader.setController(this);

      DialogPane dialogPane = loader.load();

      initOwner(owner);
      initModality(Modality.APPLICATION_MODAL);

      setResizable(false);
      setTitle("Table Reservations");
      setDialogPane(dialogPane);

      initializeListView();
      initializeTableCells();
      initializeTableValues();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void initializeListView() {
    lvDates.setItems(
      FXCollections.observableList(
        _reservations.stream()
                     .map(Reservation::getDate)
                     .distinct()
                     .sorted((o1, o2) -> -o1.compareTo(o2))
                     .toList()));
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
          }
          else if (date.isBefore(now.plusDays(3)))
            setTextFill(Color.ORANGE);
          else
            setTextFill(Color.GREEN);
        }
        else {
          setText(null);
        }
      }
    });
  }

  private void onSelectedItemChanged(final ObservableValue<? extends LocalDate> observableValue, final LocalDate oldValue, final LocalDate newValue) {
    if (Objects.nonNull(newValue)) {
      initializeTableValues();
    }
  }

  private void initializeTableCells() {
    tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
    tcReservationOn.setCellValueFactory(new PropertyValueFactory<>("reservationOn"));
    tcDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    tcTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    tcTime.setCellFactory(column -> new TableCell<>() {
      @Override
      protected void updateItem(LocalTime time, boolean empty) {
        super.updateItem(time, empty);

        if (Objects.isNull(time) || empty) {
          setText(null);
          setTextFill(Color.BLACK);
        }
        else {
          setText(time.toString());

          final var reservation = getTableView().getItems().get(getIndex());
          if (reservation.getDate().isEqual(LocalDate.now())) {
            if (time.isBefore(LocalTime.now()))
              setTextFill(Color.BLACK);
            else if (time.isBefore(LocalTime.now().plusMinutes(30)))
              setTextFill(Color.RED);
            else if (time.equals(LocalTime.now().plusHours(1).plusMinutes(30)))
              setTextFill(Color.ORANGE);
            else
              setTextFill(Color.GREEN);
          }
          else
            setTextFill(Color.BLACK);
        }
      }
    });
    tcNrSeats.setCellValueFactory(new PropertyValueFactory<>("nrSeats"));
  }

  private void initializeTableValues() {
    FilteredList<Reservation> filteredReservations = _reservations
      .filtered(reservation -> reservation.getDate().isEqual(lvDates.getSelectionModel().getSelectedItem()));
    SortedList<Reservation> sortedReservations = new SortedList<>(filteredReservations,
                                                                  Comparator.comparing(Reservation::getTime));
    tvReservations.setItems(sortedReservations);
    sortedReservations.comparatorProperty().bind(tvReservations.comparatorProperty());
  }
}
