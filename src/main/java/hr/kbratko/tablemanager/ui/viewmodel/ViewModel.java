package hr.kbratko.tablemanager.ui.viewmodel;

import org.jetbrains.annotations.NotNull;

public abstract class ViewModel<T> {
  protected final @NotNull T model;
  
  public ViewModel(final @NotNull T model) {
    this.model = model;
  }
  
  public @NotNull T getModel() {
    return model;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ViewModel<?> viewModel = (ViewModel<?>) o;

    return model.equals(viewModel.model);
  }

  @Override
  public int hashCode() {
    return model.hashCode();
  }
}
