module hr.kbratko.tablemanager {
  requires javafx.controls;
  requires javafx.fxml;

  requires net.synedra.validatorfx;
  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires org.kordamp.bootstrapfx.core;
  requires org.jetbrains.annotations;
  requires org.kordamp.ikonli.fontawesome;
  requires org.kordamp.ikonli.core;
  requires org.kordamp.ikonli.javafx;

  opens hr.kbratko.tablemanager.ui to javafx.fxml;
  opens hr.kbratko.tablemanager.ui.controllers to javafx.fxml;
  opens hr.kbratko.tablemanager.ui.dialogs to javafx.fxml;
  
  exports hr.kbratko.tablemanager.ui;
  exports hr.kbratko.tablemanager.ui.controllers;
  exports hr.kbratko.tablemanager.ui.dialogs;
  exports hr.kbratko.tablemanager.ui.models;
  
  exports hr.kbratko.tablemanager.dal.base.model;
  exports hr.kbratko.tablemanager.dal.base.repository;
  exports hr.kbratko.tablemanager.dal.base.repository.fs;
  exports hr.kbratko.tablemanager.dal.base.status;
  exports hr.kbratko.tablemanager.dal.concrete.model;
  exports hr.kbratko.tablemanager.dal.concrete.status;
  
  exports hr.kbratko.tablemanager.utils;
}