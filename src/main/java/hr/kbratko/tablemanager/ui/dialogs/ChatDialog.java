package hr.kbratko.tablemanager.ui.dialogs;

import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.server.jndi.JndiHelper;
import hr.kbratko.tablemanager.server.jndi.JndiKeyEnum;
import hr.kbratko.tablemanager.server.rmi.ChatClient;
import hr.kbratko.tablemanager.server.rmi.ChatServer;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Window;
import javax.naming.NamingException;
import org.jetbrains.annotations.NotNull;

public class ChatDialog extends Dialog<ButtonType> implements ChatClient {
  private static final Logger logger = Logger.getLogger(ChatDialog.class.getName());

  private final User user;
  @FXML
  ListView<String> lvUsers;
  @FXML
  TextArea taMessages;
  @FXML
  TextField tfMessage;
  private ChatServer chatServer;

  public ChatDialog(Window owner, final @NotNull User user) {
    this.user = user;

    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/hr/kbratko/tablemanager/ui/dialogs/chat-dialog.fxml"));
      loader.setController(this);

      DialogPane dialogPane = loader.load();

      initOwner(owner);
      initModality(Modality.APPLICATION_MODAL);

      setResizable(false);
      setTitle("Chat");
      setDialogPane(dialogPane);

      setResultConverter(buttonType -> {
        if (Objects.equals(ButtonBar.ButtonData.CANCEL_CLOSE, buttonType.getButtonData())) {
          try {
            chatServer.leave(user);
          } catch (RemoteException e) {
            logger.log(Level.WARNING, "Could not access remote service", e);
          }
        }

        return buttonType;
      });


      initializeClient();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void initializeClient() {
    try {
      final int rmiPort = Integer.parseInt(JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_PORT));
      final String rmiHost = JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_HOST);
      final String rmiService = JndiHelper.getConfigurationParameter(JndiKeyEnum.RMI_SERVICE);

      final String clientHost = InetAddress.getLocalHost().getHostAddress();
      final String clientService = "%s_%s".formatted(rmiService, user.getEmail());

      final Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
      final ChatClient chatClient = this;
      ChatClient skeleton = (ChatClient) UnicastRemoteObject.exportObject(chatClient, 0);
      registry.rebind("rmi://%s/%s".formatted(clientHost, clientService), chatClient);

      chatServer = (ChatServer) registry.lookup("rmi://%s/%s".formatted(rmiHost, rmiService));
      chatServer.join(user, clientHost, clientService);
    } catch (RemoteException e) {
      logger.log(Level.SEVERE, "Error while accessing registry", e);
    } catch (NotBoundException e) {
      logger.log(Level.SEVERE, "Error while bounding to chat server", e);
    } catch (NamingException e) {
      logger.log(Level.SEVERE, "Couldn't find configuration parameter", e);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error while accessing configuration file", e);
    }
  }

  @FXML
  protected void send() {
    try {
      chatServer.send(user, tfMessage.getText() + '\n');
      tfMessage.clear();
    } catch (RemoteException e) {
      logger.log(Level.SEVERE, "Error while bounding to chat server", e);
    }
  }

  @Override
  public void receive(String message) throws RemoteException {
    taMessages.appendText(message);
  }

  @Override
  public void updateUsers(String[] current) throws RemoteException {
    Platform.runLater(() -> {
      lvUsers.itemsProperty().setValue(FXCollections.observableArrayList());
      Arrays.stream(current).forEach(user -> {
        lvUsers.getItems().add(user);
      });
    });
  }
}
