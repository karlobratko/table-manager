package hr.kbratko.tablemanager.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Threads {
  @Contract(value = " -> fail", pure = true)
  private Threads() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Threads instances for you!");}
  
  public static void run(final @NotNull Runnable task) {new Thread(task).start();}
}
