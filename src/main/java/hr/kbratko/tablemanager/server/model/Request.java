package hr.kbratko.tablemanager.server.model;

import hr.kbratko.tablemanager.server.infrastructure.RequestOperation;
import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;


  private final RequestOperation requestOperation;
  private final Object data;

  private Request(RequestOperation requestOperation, Object data) {
    this.requestOperation = requestOperation;
    this.data = data;
  }

  public static Request of(RequestOperation requestOperation, Object data) {
    return new Request(requestOperation, data);
  }

  public RequestOperation getOperation() {
    return requestOperation;
  }

  public Object getData() {
    return data;
  }
}
