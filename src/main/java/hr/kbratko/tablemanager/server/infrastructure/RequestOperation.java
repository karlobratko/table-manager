package hr.kbratko.tablemanager.server.infrastructure;

public enum RequestOperation {
  LOGIN,
  REGISTER,
  GET_ALL_TABLES,
  UPDATE_TABLE,
  DELETE_TABLE,
  CREATE_TABLE,
  GET_ALL_RESERVATIONS,
  GET_ALL_TABLES_BY_RESERVATION_ID,
  GET_ALL_RESERVATIONS_BY_TABLE_ID,
  UPDATE_TABLE_RESERVATIONS,
  CREATE_TABLE_RESERVATIONS,
  UPDATE_RESERVATION,
  DELETE_RESERVATION,
  CREATE_RESERVATION
}