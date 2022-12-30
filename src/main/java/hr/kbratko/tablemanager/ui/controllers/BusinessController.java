package hr.kbratko.tablemanager.ui.controllers;

import hr.kbratko.tablemanager.ui.viewmodel.ViewModel;

public interface BusinessController<T, V extends ViewModel<T>> 
  extends Controller, 
          UserManager,
          ListSelector<T, V> {
}
