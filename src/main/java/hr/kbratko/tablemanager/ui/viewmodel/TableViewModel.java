package hr.kbratko.tablemanager.ui.viewmodel;

import hr.kbratko.tablemanager.repository.model.Table;
import javafx.beans.property.*;
import org.jetbrains.annotations.NotNull;

public class TableViewModel extends ViewModel<Table> {
  public TableViewModel(final @NotNull Table model) {
    super(model);
  }
  
  public @NotNull ReadOnlyIntegerProperty idProperty() {return new SimpleIntegerProperty(model.getId());}
  
  public @NotNull StringProperty nameProperty() {return new SimpleStringProperty(model.getName());}
  
  public @NotNull IntegerProperty nrSeatsProperty() {return new SimpleIntegerProperty(model.getNrSeats());}
  
  public @NotNull StringProperty descriptionProperty() {return new SimpleStringProperty(model.getDescription());}
}
