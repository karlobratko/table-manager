package hr.kbratko.tablemanager.ui;

import hr.kbratko.tablemanager.ui.controllers.Controller;
import hr.kbratko.tablemanager.ui.controllers.LoginController;
import hr.kbratko.tablemanager.ui.infrastructure.Metadata;
import java.io.IOException;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
//import java.util.Optional;

public class TableManagerApplication extends Application {
  public static final String NAME = "Table Manager";
  public static final Logger logger = Logger.getLogger(TableManagerApplication.class.getName());

  private Metadata metadata;

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(@NotNull Stage stage) throws IOException {
    logger.info("Starting client JavaFX application");

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/kbratko/tablemanager/ui/views/login-view.fxml"));
    stage.setTitle(NAME);
    stage.setScene(new Scene(loader.load(), 600, 400));
    stage.show();
    final Controller controller = loader.<LoginController>getController();
    controller.setStage(stage);
    controller.setMetadata(metadata);
  }

  @Override
  public void init() {
    logger.info("Initializing client JavaFX application");

    metadata = new Metadata(ProcessHandle.current().pid());
    logger.info("PID: %d".formatted(metadata.getPid()));
  }

  @Override
  public void stop() {
    logger.info("Stopping client JavaFX application");
  }

}