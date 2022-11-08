package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.ui.dialogs.CreateReservationDialog;
import hr.kbratko.tablemanager.ui.dialogs.CreateTableDialog;
import hr.kbratko.tablemanager.ui.dialogs.TableReservationsDialog;
import hr.kbratko.tablemanager.ui.models.*;
import hr.kbratko.tablemanager.utils.Alerts;
import hr.kbratko.tablemanager.utils.Documentations;
import hr.kbratko.tablemanager.utils.Strings;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class TablesController {
  @FXML
  private ListView<Table>  lvTables;
  @FXML
  private TextField        tfId;
  @FXML
  private TextField        tfName;
  @FXML
  private Spinner<Integer> spNrSeats;
  @FXML
  private TextArea         taDescription;
  @FXML
  private Button           btnSort;

  private final Validator         _validator = new Validator();
  private final SortedList<Table> _tables    = new SortedList<>(FsTableRepository.getInstance().getTables(),
                                                                Comparator.comparing(Table::getNrSeats).reversed());

  private Stage   _stage;
  private boolean _sortAsc = false;

  @FXML
  protected void initialize() {
    initializeSpinner();
    initializeListView();
    initializeValidation();
    initializeButton();
  }

  private void initializeValidation() {
    _validator.createCheck()
              .dependsOn("name", tfName.textProperty())
              .withMethod(c -> {
                if (Strings.isNullOrBlank(c.get("name")))
                  c.error("Please fill name field");
              }).decorates(tfName);
  }

  private void initializeSpinner() {
    spNrSeats.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
  }

  private void initializeListView() {
    lvTables.setItems(_tables);
    lvTables.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
    lvTables.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    if (!lvTables.getItems().isEmpty())
      lvTables.getSelectionModel().selectFirst();
  }

  private void initializeButton() {
    btnSort.setGraphic(FontIcon.of(FontAwesome.SORT_ASC));
    btnSort.setOnAction(actionEvent -> {
      _sortAsc = !_sortAsc;
      btnSort.setGraphic(_sortAsc ? FontIcon.of(FontAwesome.SORT_DESC) : FontIcon.of(FontAwesome.SORT_ASC));
      _tables.setComparator(_tables.getComparator().reversed());
    });
  }


  @FXML
  protected void onSelectedItemChanged(final ObservableValue<? extends Table> observableValue, final Table oldValue, final Table newValue) {
    if (Objects.nonNull(newValue)) {
      final var table = lvTables.getSelectionModel().getSelectedItem();
      if (Objects.nonNull(table)) {
        tfId.setText(Integer.toString(table.getId()));
        tfName.setText(table.getName());
        spNrSeats.getValueFactory().setValue(table.getNrSeats());
        taDescription.setText(table.getDescription());
      }
    }
  }

  @FXML
  protected void showRelatedReservations() {
    if (Objects.nonNull(lvTables.getSelectionModel().getSelectedItem()))
      new TableReservationsDialog(_stage, lvTables.getSelectionModel().getSelectedItem()).showAndWait();
  }

  @FXML
  protected void updateTableData() {
    final var table = lvTables.getSelectionModel().getSelectedItem();
    if (Objects.nonNull(table)) {
      if (!_validator.validate()) {
        Alerts.showError("Update Table", "Error while trying to update Table.", getValidationMessages());
        return;
      }

      table.setName(tfName.getText());
      table.setNrSeats(spNrSeats.getValue());
      table.setDescription(taDescription.getText());

      final var selectedIndex = lvTables.getSelectionModel().getSelectedIndex();
      final var items         = lvTables.getItems();
      lvTables.setItems(null);
      lvTables.setItems(items);
      lvTables.getSelectionModel().select(selectedIndex);
    }
  }

  private String getValidationMessages() {
    return _validator.validationResultProperty().get().getMessages().stream().map(msg -> msg.getSeverity().toString() + ": " + msg.getText()).collect(Collectors.joining("\n"));
  }

  @FXML
  protected void deleteTable() {
    final var table = lvTables.getSelectionModel().getSelectedItem();
    if (Objects.nonNull(table)) showAlertAndDelete(table);
  }

  private void showAlertAndDelete(final @NotNull Table table) {
    Alerts.showConfirmation("Delete Table",
                            String.format("Delete item: %s?", table.getName()),
                            "Are you sure? Press OK to confirm, or Cancel to Back out.")
          .ifPresent(type -> {
            if (type == ButtonType.OK) FsTableRepository.getInstance().removeTable(table);
          });
  }

  @FXML
  protected void createNewTable() {
    new CreateTableDialog(_stage).showAndWait()
                                 .ifPresent(result -> result.ifPresent(table -> {
                                   FsTableRepository.getInstance().addTable(table);
                                   lvTables.getSelectionModel().select(table);
                                 }));
  }

  @FXML
  protected void createNewReservation() {
    new CreateReservationDialog(_stage).showAndWait()
                                       .ifPresent(result -> result.ifPresent(model -> {
                                         for (Table table : model.getRelatedTables())
                                           FsTableReservationRepository.getInstance()
                                                                       .addTableReservation(TableReservation.of(table.getId(),
                                                                                                                model.getReservation().getId()));
                                         FsReservationRepository.getInstance().addReservation(model.getReservation());
                                         try {
                                           final var loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/hr/kbratko/tablemanager/ui/views/reservations-view.fxml")));
                                           _stage.setScene(new Scene(loader.load()));
                                           final ReservationsController controller = loader.getController();
                                           controller.setStage(_stage);
                                           controller.selectReservation(model.getReservation());
                                         } catch (IOException e) {
                                           e.printStackTrace();
                                         }
                                       }));
  }

  @FXML
  protected void openReservationsWindow() {
    try {
      final var loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/hr/kbratko/tablemanager/ui/views/reservations-view.fxml")));
      _stage.setScene(new Scene(loader.load())); final ReservationsController controller = loader.getController();
      controller.setStage(_stage);
    } catch (IOException e) {
      e.printStackTrace();
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
  protected void exitApplication() {Platform.exit();}

  public void setStage(final @NotNull Stage stage) {
    _stage = stage;
  }

  public void selectTable(Table table) {
    lvTables.getSelectionModel().select(table);
  }
}
