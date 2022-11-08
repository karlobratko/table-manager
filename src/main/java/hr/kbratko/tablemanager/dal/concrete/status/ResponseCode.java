package hr.kbratko.tablemanager.dal.concrete.status;

import hr.kbratko.tablemanager.dal.base.status.StatusCode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCode implements StatusCode {

  INTERNAL_ERROR(-1),
  UNSUPPORTED(0),
  SUCCESS(1),
  NOT_FOUND(2),
  DENIED(3),
  COLLISION(4),
  UNKNOWN(5),
  RECREATED(6);

  private static final Map<Integer, StatusCode> _mappings = new HashMap<>();

  static {
    for (ResponseCode code : ResponseCode.values())
      _mappings.put(code.toInteger(), code);
  }

  private final int _code;

  @Contract(pure = true)
  ResponseCode(final int code) {_code = code;}

  @Contract(pure = true)
  @NotNull
  public static ResponseCode fromInteger(int code) {
    return switch (code) {
      case -1 -> INTERNAL_ERROR;
      case 0 -> UNSUPPORTED;
      case 1 -> SUCCESS;
      case 2 -> NOT_FOUND;
      case 3 -> DENIED;
      case 4 -> COLLISION;
      case 5 -> UNKNOWN;
      case 6 -> RECREATED;
      default -> throw new UnsupportedOperationException();
    };
  }

  @Contract(pure = true)
  @Override
  public int toInteger() {return _code;}

  @Contract(pure = true)
  @Override
  @NotNull
  public Map<Integer, StatusCode> getStatusCodeMappings() {return _mappings;}
}
