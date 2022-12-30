package hr.kbratko.tablemanager.ui.viewmodel;

import hr.kbratko.tablemanager.repository.model.Reservation;
import javafx.beans.property.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationViewModel extends ViewModel<Reservation> {
  public ReservationViewModel(final @NotNull Reservation model) {
    super(model);
  }
  
  public @NotNull ReadOnlyIntegerProperty idProperty() {return new SimpleIntegerProperty(model.getId());}
  
  public @NotNull StringProperty ownerProperty() {return new SimpleStringProperty(model.getOwner());}
  
  public @NotNull ObjectProperty<LocalDate> dateProperty() {return new SimpleObjectProperty<>(model.getDate());}
  
  public @NotNull ObjectProperty<LocalTime> timeProperty() {return new SimpleObjectProperty<>(model.getTime());}
  
  public @NotNull IntegerProperty nrSeatsProperty() {return new SimpleIntegerProperty(model.getNrSeats());}
  
  public @NotNull StringProperty descriptionProperty() {return new SimpleStringProperty(model.getDescription());}
}
