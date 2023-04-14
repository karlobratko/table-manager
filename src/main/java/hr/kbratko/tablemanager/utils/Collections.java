package hr.kbratko.tablemanager.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Collections {
  @Contract(value = " -> fail", pure = true)
  private Collections() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Collections instances for you!");}

  @SafeVarargs
  @Contract(pure = true)
  public static <T> @NotNull List<T> mutableListOf(final @NotNull T... elements) {
    final List<T> list = new ArrayList<>();
    java.util.Collections.addAll(list, elements);
    return list;
  }
}
