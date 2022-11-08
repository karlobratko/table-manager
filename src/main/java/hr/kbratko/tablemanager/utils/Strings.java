package hr.kbratko.tablemanager.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Strings {
  @Contract(value = " -> fail", pure = true)
  private Strings() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Strings instances for you!");}

  @Contract(pure = true)
  public static boolean isNullOrEmpty(final @Nullable String str) {
    return Objects.isNull(str) || Objects.equals(str, "");
  }

  @Contract(pure = true)
  public static boolean isNullOrBlank(final @Nullable String str) {
    return Objects.isNull(str) || Objects.equals(str.trim(), "");
  }
}
