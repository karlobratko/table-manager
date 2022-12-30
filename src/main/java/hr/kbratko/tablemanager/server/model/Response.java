package hr.kbratko.tablemanager.server.model;

import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import java.io.Serial;
import java.io.Serializable;

public class Response implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private final ResponseStatus status;
  private final String message;
  private final Object data;

  private Response(ResponseStatus status, String message, Object data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public static Response from(ResponseStatus status, String message, Object data) {
    return new Response(status, message, data);
  }

  public ResponseStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public Object getData() {
    return data;
  }
}
