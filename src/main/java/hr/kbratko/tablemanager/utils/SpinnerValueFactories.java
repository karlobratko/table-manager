package hr.kbratko.tablemanager.utils;

import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class SpinnerValueFactories {
  private SpinnerValueFactories() {throw new AssertionError("No hr.kbratko.tablemanager.utils.SpinnerValueFactories instances for you!");}

  public static SpinnerValueFactory.IntegerSpinnerValueFactory integer(int min, int max, int step) {
    final var valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, step);

    valueFactory.setConverter(new StringConverter<>() {
      @Override
      public String toString(Integer object) {
        return object.toString() ;
      }

      @Override
      public Integer fromString(String string) {
        if (string.matches("-?\\d+")) {
          return Integer.parseInt(string);
        }
        return 0 ;
      }
    });

    return valueFactory;
  }
}
