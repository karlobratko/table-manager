package hr.kbratko.tablemanager.ui.viewmodel;

import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.repository.model.UserType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;

public class UserViewModel extends ViewModel<User> {
  public UserViewModel(final @NotNull User model) {
    super(model);
  }

  public @NotNull ReadOnlyIntegerProperty idProperty() {return new SimpleIntegerProperty(model.getId());}

  public @NotNull StringProperty firstNameProperty() {return new SimpleStringProperty(model.getFirstName());}

  public @NotNull StringProperty lastNameProperty() {return new SimpleStringProperty(model.getLastName());}

  public @NotNull StringProperty fullNameProperty() {return new SimpleStringProperty(model.getFullName());}

  public @NotNull StringProperty emailProperty() {return new SimpleStringProperty(model.getEmail());}

  public @NotNull ObjectProperty<UserType> typeProperty() {return new SimpleObjectProperty<UserType>(model.getType());}
}
