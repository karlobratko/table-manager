package hr.kbratko.tablemanager.server.callables;

import hr.kbratko.tablemanager.repository.ReservationRepository;
import hr.kbratko.tablemanager.repository.TableRepository;
import hr.kbratko.tablemanager.repository.TableReservationRepository;
import hr.kbratko.tablemanager.repository.UserRepository;
import hr.kbratko.tablemanager.repository.factory.ReservationRepositoryFactory;
import hr.kbratko.tablemanager.repository.factory.TableHistoryRepositoryFactory;
import hr.kbratko.tablemanager.repository.factory.TableRepositoryFactory;
import hr.kbratko.tablemanager.repository.factory.TableReservationRepositoryFactory;
import hr.kbratko.tablemanager.repository.factory.UserRepositoryFactory;
import hr.kbratko.tablemanager.repository.history.HistoryAction;
import hr.kbratko.tablemanager.repository.history.RwHistoryRepository;
import hr.kbratko.tablemanager.repository.model.Reservation;
import hr.kbratko.tablemanager.repository.model.Table;
import hr.kbratko.tablemanager.repository.model.TableHistoryModel;
import hr.kbratko.tablemanager.repository.model.TableReservation;
import hr.kbratko.tablemanager.repository.model.User;
import hr.kbratko.tablemanager.repository.model.UserType;
import hr.kbratko.tablemanager.server.infrastructure.ResponseStatus;
import hr.kbratko.tablemanager.server.model.Request;
import hr.kbratko.tablemanager.server.model.Response;
import hr.kbratko.tablemanager.utils.Streams;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RequestHandler implements Callable<Void> {
  private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
  private static final UserRepository userRepo = UserRepositoryFactory.getInstance();
  private static final TableRepository tableRepo = TableRepositoryFactory.getInstance();
  private static final ReservationRepository reservationRepo = ReservationRepositoryFactory.getInstance();
  private static final TableReservationRepository tableReservationRepo = TableReservationRepositoryFactory.getInstance();
  private static final RwHistoryRepository<TableHistoryModel> tableHistoryRepository = TableHistoryRepositoryFactory.getInstance();

  private final Socket socket;

  private RequestHandler(Socket socket) {
    this.socket = socket;
  }

  public static RequestHandler from(Socket socket) {
    return new RequestHandler(socket);
  }

  @Override
  public Void call() throws Exception {

    try {
      final var in = Streams.objectInput(Streams.bufferedInput(socket.getInputStream()));
      final var request = (Request) in.readObject();
      socket.shutdownInput();

      logger.info(
        "%s %s/%s".formatted(
          LocalDateTime.now(),
          socket.getRemoteSocketAddress(),
          request.getOperation().toString().toLowerCase()
        )
      );

      Response response = switch (request.getOperation()) {
        case LOGIN -> loginUser(request.getData());
        case REGISTER -> registerUser(request.getData());
        case GET_ALL_TABLES -> getAllTables();
        case UPDATE_TABLE -> updateTable(request.getData());
        case DELETE_TABLE -> deleteTable(request.getData());
        case CREATE_TABLE -> createTable(request.getData());
        case GET_ALL_RESERVATIONS -> getAllReservations();
        case GET_ALL_TABLES_BY_RESERVATION_ID -> getAllTablesByReservationId(request.getData());
        case GET_ALL_RESERVATIONS_BY_TABLE_ID -> getAllReservationsByTableId(request.getData());
        case UPDATE_TABLE_RESERVATIONS -> updateTableReservations(request.getData());
        case CREATE_TABLE_RESERVATIONS -> createTableReservations(request.getData());
        case UPDATE_RESERVATION -> updateReservation(request.getData());
        case DELETE_RESERVATION -> deleteReservation(request.getData());
        case CREATE_RESERVATION -> createReservation(request.getData());
        case UNDO_TABLE_ACTION -> undoTableAction(request.getData());
      };

      final var out = Streams.objectOutput(Streams.bufferedOutput(socket.getOutputStream()));
      out.writeObject(response);
      out.flush();
      socket.shutdownOutput();
    } catch (IOException e) {
      logger.log(Level.INFO, "Client disconnected", e);
      // client disconnected;
      // ignore;
    } catch (Exception e) {
      logger.log(Level.INFO, "Internal server error", e);
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        // ignore;
      }
    }

    return null;
  }

  private Response loginUser(Object data) throws Exception {
    if (Objects.isNull(data) || !(data instanceof final User user))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for LOGIN endpoint",
        null
      );

    Optional<User> optional = userRepo.login(user.getEmail(), user.getPassword());

    return optional
      .map(value -> Response.from(
        ResponseStatus.OK_200,
        "Logged in successfully",
        value
      ))
      .orElseGet(() -> Response.from(
        ResponseStatus.FORBIDDEN_403,
        "Invalid username or password",
        null
      ));
  }

  private Response registerUser(Object data) {
    if (Objects.isNull(data) || !(data instanceof final User user))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for REGISTER endpoint",
        null
      );

    try {
      user.setType(UserType.GUEST);
      final Integer createdCount = userRepo.create(user);
      if (createdCount == 0)
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "User already registered",
          null
        );

      userRepo.commit();

      return Response.from(
        ResponseStatus.OK_200,
        "Registered successfully",
        user
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response getAllTables() {
    try {
      return Response.from(
        ResponseStatus.OK_200,
        "Fetched all tables successfully",
        tableRepo.read()
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response updateTable(Object data) {
    if (Objects.isNull(data) || !(data instanceof final Table table))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for UPDATE_TABLE endpoint",
        null
      );

    try {
      final var oldTable = tableRepo.read(table.getId());

      final Integer updatedCount = tableRepo.update(table);
      if (updatedCount == 0)
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Uniqueness violated",
          null
        );

      tableRepo.commit();

      tableHistoryRepository.append(TableHistoryModel.of(HistoryAction.UPDATE, oldTable.orElse(null)));
      tableHistoryRepository.append(TableHistoryModel.of(HistoryAction.UPDATE, table));

      return Response.from(
        ResponseStatus.OK_200,
        "Table updated successfully",
        table
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response deleteTable(Object data) {
    if (Objects.isNull(data) || !(data instanceof final Table table))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for DELETE_TABLE endpoint",
        null
      );

    try {
      final Integer deletedCount = tableRepo.delete(table);
      if (deletedCount == 0)
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "No tables were deleted",
          null
        );

      tableRepo.commit();

      tableHistoryRepository.append(TableHistoryModel.of(HistoryAction.DELETE, table));

      return Response.from(
        ResponseStatus.OK_200,
        "Table deleted successfully",
        null
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response createTable(Object data) {
    if (Objects.isNull(data) || !(data instanceof final Table table))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for CREATE_TABLE endpoint",
        null
      );

    try {
      final Integer createCount = tableRepo.create(table);
      if (createCount == 0)
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Uniqueness violated, please insert table with unique name",
          null
        );

      tableRepo.commit();

      tableHistoryRepository.append(TableHistoryModel.of(HistoryAction.CREATE, table));

      return Response.from(
        ResponseStatus.OK_200,
        "Table created successfully",
        table
      );
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Internal server error", e);
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response getAllReservations() {
    try {
      return Response.from(
        ResponseStatus.OK_200,
        "Fetched all reservations successfully",
        reservationRepo.read()
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response getAllTablesByReservationId(Object data) {
    try {
      if (Objects.isNull(data) || !(data instanceof final Integer id))
        return Response.from(
          ResponseStatus.BAD_REQUEST_400,
          "Invalid data for GET_ALL_TABLES_BY_RESERVATION_ID endpoint",
          null
        );

      final int[] tableIds = tableReservationRepo
        .read()
        .stream()
        .filter(tableReservation -> id.equals(tableReservation.getReservationId()))
        .mapToInt(TableReservation::getTableId)
        .toArray();

      return Response.from(
        ResponseStatus.OK_200,
        "Fetched all tables of reservation successfully",
        tableRepo
          .read()
          .stream()
          .filter(table -> Arrays
            .stream(tableIds)
            .anyMatch(i -> i == table.getId())
          )
          .collect(Collectors.toSet())
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response deleteReservation(Object data) {
    if (Objects.isNull(data) || !(data instanceof final Reservation reservation))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for DELETE_RESERVATION endpoint",
        null
      );

    try {
      final List<TableReservation> tableReservations =
        tableReservationRepo
          .read()
          .stream()
          .filter(tableReservation -> reservation.getId().equals(tableReservation.getReservationId()))
          .toList();
      final Integer deletedTableReservationsCount = tableReservationRepo.delete(tableReservations);
      if (deletedTableReservationsCount != tableReservations.size())
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Could not unbind tables from reservation",
          null
        );

      final Integer deletedCount = reservationRepo.delete(reservation);
      if (deletedCount == 0)
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "No reservations were deleted",
          null
        );

      tableReservationRepo.commit();
      reservationRepo.commit();

      return Response.from(
        ResponseStatus.OK_200,
        "Reservation deleted successfully",
        null
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  @SuppressWarnings("unchecked")
  private Response updateTableReservations(Object data) {
    if (Objects.isNull(data) || !(data instanceof Collection))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for UPDATE_TABLE_RESERVATIONS endpoint",
        null
      );

    final var collection = (Collection<TableReservation>) data;
    final var optionalFirst = collection.stream().findFirst();

    if (optionalFirst.isEmpty())
      return Response.from(
        ResponseStatus.OK_200,
        "Tables of reservation updated successfully",
        null
      );

    try {
      final List<TableReservation> tableReservationsForDelete = tableReservationRepo
        .read()
        .stream()
        .filter(tableReservation ->
          optionalFirst.get().getReservationId().equals(tableReservation.getReservationId()))
        .toList();
      final int deletedCount = tableReservationRepo.delete(tableReservationsForDelete);
      if (deletedCount != tableReservationsForDelete.size())
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Could not unbind tables from reservation",
          null
        );

      tableReservationRepo.commit();

      final int createdCount = tableReservationRepo.create(collection);
      if (createdCount != collection.size())
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Could not bind tables to reservation",
          null
        );

      tableReservationRepo.commit();

      return Response.from(
        ResponseStatus.OK_200,
        "Tables of reservation updated successfully",
        collection
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }


  private Response updateReservation(Object data) {
    if (Objects.isNull(data) || !(data instanceof final Reservation reservation))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for UPDATE_RESERVATION endpoint",
        null
      );

    try {
      final Integer updatedCount = reservationRepo.update(reservation);
      if (updatedCount == 0)
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Uniqueness violated",
          null
        );

      reservationRepo.commit();

      return Response.from(
        ResponseStatus.OK_200,
        "Reservation updated successfully",
        reservation
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response createReservation(Object data) {
    if (Objects.isNull(data) || !(data instanceof final Reservation reservation))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for CREATE_RESERVATION endpoint",
        null
      );

    try {
      final Integer createCount = reservationRepo.create(reservation);
      if (createCount == 0)
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Uniqueness violated, please insert reservation with unique owner, date, and time",
          null
        );

      reservationRepo.commit();

      return Response.from(
        ResponseStatus.OK_200,
        "Reservation created successfully",
        reservation
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  @SuppressWarnings("unchecked")
  private Response createTableReservations(Object data) {
    if (Objects.isNull(data) || !(data instanceof Collection))
      return Response.from(
        ResponseStatus.BAD_REQUEST_400,
        "Invalid data for CREATE_TABLE_RESERVATIONS endpoint",
        null
      );

    final var collection = (Collection<TableReservation>) data;

    try {
      final int createdCount = tableReservationRepo.create(collection);
      if (createdCount != collection.size())
        return Response.from(
          ResponseStatus.CONFLICT_409,
          "Could not bind tables to reservation",
          null
        );

      tableReservationRepo.commit();

      return Response.from(
        ResponseStatus.OK_200,
        "Tables of reservation updated successfully",
        collection
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response getAllReservationsByTableId(Object data) {
    try {
      if (Objects.isNull(data) || !(data instanceof final Integer id))
        return Response.from(
          ResponseStatus.BAD_REQUEST_400,
          "Invalid data for GET_ALL_RESERVATIONS_BY_TABLE_ID endpoint",
          null
        );

      final int[] reservationIds =
        tableReservationRepo.read()
          .stream()
          .filter(tr -> Objects.equals(tr.getTableId(), id))
          .mapToInt(TableReservation::getReservationId)
          .toArray();

      return Response.from(
        ResponseStatus.OK_200,
        "Fetched all reservations of table successfully",
        reservationRepo
          .read()
          .stream()
          .filter(reservation -> Arrays.stream(reservationIds).anyMatch(i -> reservation.getId() == i))
          .collect(Collectors.toSet())
      );
    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }

  private Response undoTableAction(Object data) {
    try {
      final var optional = tableHistoryRepository.popLast();

      if (optional.isEmpty())
        return Response.from(
          ResponseStatus.NO_CONTENT_204,
          "No history",
          null
        );

      final var history = optional.get();
      return switch (history.getAction()) {
        case CREATE -> {
          tableRepo.delete(history.getTable());
          tableRepo.commit();

          yield Response.from(
            ResponseStatus.OK_200,
            "History returned",
            TableHistoryModel.of(HistoryAction.DELETE, history.getTable())
          );
        }
        case DELETE -> {
          tableRepo.create(history.getTable());
          tableRepo.commit();

          yield Response.from(
            ResponseStatus.OK_200,
            "History returned",
            TableHistoryModel.of(HistoryAction.CREATE, history.getTable())
          );
        }
        case UPDATE -> {
          final var updatedOptional = tableHistoryRepository.popLast();
          if (updatedOptional.isEmpty())
            yield Response.from(
              ResponseStatus.NO_CONTENT_204,
              "No history",
              null
            );

          final var updated = updatedOptional.get();
          tableRepo.update(updated.getTable());
          tableRepo.commit();

          yield Response.from(
            ResponseStatus.OK_200,
            "History returned",
            TableHistoryModel.of(HistoryAction.UPDATE, updated.getTable())
          );
        }
      };

    } catch (Exception e) {
      return Response.from(
        ResponseStatus.INTERNAL_SERVER_ERROR_500,
        "Internal server error",
        null
      );
    }
  }
}
