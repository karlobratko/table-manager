package hr.kbratko.tablemanager.server.infrastructure;

import java.util.StringTokenizer;

public enum ResponseStatus {
  OK_200,
  CREATED_201,
  NO_CONTENT_204,
  RESET_CONTENT_205,
  BAD_REQUEST_400,
  UNAUTHORIZED_401,
  FORBIDDEN_403,
  NOT_FOUND_404,
  CONFLICT_409,
  INTERNAL_SERVER_ERROR_500
}
