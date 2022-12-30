package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Sequentiable<T> {
  @NotNull T next(final @NotNull T value);
}
