package hr.kbratko.tablemanager.ui;

import hr.kbratko.tablemanager.ui.controllers.LoginController;
import hr.kbratko.tablemanager.ui.models.FsReservationRepository;
import hr.kbratko.tablemanager.ui.models.FsTableRepository;
import hr.kbratko.tablemanager.ui.models.FsTableReservationRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TableManagerApplication extends Application {
  public static final String NAME = "Table Manager";

  @Override
  public void start(@NotNull Stage stage) throws IOException {
    FXMLLoader            loader     = new FXMLLoader(getClass().getResource("/hr/kbratko/tablemanager/ui/views/login-view.fxml"));
    stage.setTitle(NAME);
    stage.setScene(new Scene(loader.load(), 600, 400));
    stage.show();
    final LoginController controller = loader.getController();
    controller.setStage(stage);
  }

  @Override
  public void init() {
    try {
      FsTableRepository.getInstance().loadTables();
      FsReservationRepository.getInstance().loadReservations();
      FsTableReservationRepository.getInstance().loadTableReservations();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }
  }

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void stop() {
    try {
      FsTableRepository.getInstance().saveTables();
      FsReservationRepository.getInstance().saveReservations();
      FsTableReservationRepository.getInstance().saveTableReservations();
    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }
  }

}