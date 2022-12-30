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
  requires java.logging;
  requires java.rmi;
  requires java.naming;

  opens hr.kbratko.tablemanager.ui to javafx.fxml;
  opens hr.kbratko.tablemanager.ui.controllers to javafx.fxml;
  opens hr.kbratko.tablemanager.ui.dialogs to javafx.fxml;
  
  exports hr.kbratko.tablemanager.ui;
  exports hr.kbratko.tablemanager.ui.controllers;
  exports hr.kbratko.tablemanager.ui.dialogs;
  exports hr.kbratko.tablemanager.ui.infrastructure;
  exports hr.kbratko.tablemanager.ui.viewmodel;

  exports hr.kbratko.tablemanager.repository;
  exports hr.kbratko.tablemanager.repository.fs;
  exports hr.kbratko.tablemanager.repository.factory;
  exports hr.kbratko.tablemanager.repository.model;

  exports hr.kbratko.tablemanager.server;
  exports hr.kbratko.tablemanager.server.model;
  exports hr.kbratko.tablemanager.server.infrastructure;
  exports hr.kbratko.tablemanager.server.callables;
  
  exports hr.kbratko.tablemanager.utils;
  exports hr.kbratko.tablemanager.server.rmi;
}