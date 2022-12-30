package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.ui.viewmodel.ViewModel;
import org.jetbrains.annotations.NotNull;

public interface ListSelector<T, V extends ViewModel<T>> {
  void selectListModel(final @NotNull V viewModel);
}
