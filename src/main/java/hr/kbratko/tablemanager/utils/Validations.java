package hr.kbratko.tablemanager.utils;

import net.synedra.validatorfx.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class Validations {
  private Validations() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Validations instances for you!");}

  public static String getMessages(final @NotNull Validator validator) {
    return validator.validationResultProperty()
                    .get()
                    .getMessages()
                    .stream()
                    .map(msg -> msg.getSeverity().toString() + ": " + msg.getText())
                    .collect(Collectors.joining("\n"));
  }
}
