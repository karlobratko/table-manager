package hr.kbratko.tablemanager.repository.fs;

import hr.kbratko.tablemanager.repository.ReservationRepository;
import hr.kbratko.tablemanager.repository.model.Reservation;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;

public class FsReservationRepository
  implements FsRepository<Reservation>,
  ReservationRepository {
  private static final String FILE_NAME = "reservations.ser";

  private final @NotNull TreeSet<Reservation> cache = new TreeSet<>();

  private void load() throws Exception {
    cache.clear();

    if (!Files.exists(Paths.get(FILE_NAME)))
      Files.createFile(Paths.get(FILE_NAME));

    try (final var ois =
           new ObjectInputStream(
             new BufferedInputStream(
               Files.newInputStream(Paths.get(FILE_NAME),
                 StandardOpenOption.READ)))) {
      boolean eof = false;
      while (!eof)
        try {
          Reservation reservation = (Reservation) ois.readObject();
          cache.add(reservation);
        } catch (EOFException ex) {
          eof = true;
        }
    } catch (EOFException ex) {
      // empty file
    }
  }

  private void save() throws Exception {
    try (final var oos =
           new ObjectOutputStream(
             new BufferedOutputStream(
               Files.newOutputStream(Paths.get(FILE_NAME),
                 StandardOpenOption.CREATE,
                 StandardOpenOption.WRITE,
                 StandardOpenOption.TRUNCATE_EXISTING)))) {
      for (Reservation reservation : cache)
        oos.writeObject(reservation);
    }
  }

  @Override
  public void commit() throws Exception {
    synchronized (this) {
      save();
    }
  }

  @Override
  public @NotNull Integer nextId() {
    synchronized (this) {
      return cache.isEmpty()
        ? 1
        : cache.last().getId() + 1;
    }
  }

  @Override
  public @NotNull Integer create(final @NotNull Reservation model) throws Exception {
    synchronized (this) {
      load();

      final Optional<Reservation> check = cache
        .stream()
        .filter(other -> other.getOwner().equals(model.getOwner()) &&
          other.getDate().equals(model.getDate()) &&
          other.getTime().equals(model.getTime()))
        .findFirst();
      if (check.isPresent())
        return 0;

      model.setId(nextId());
      cache.add(model);
      return 1;
    }
  }

  @Override
  public @NotNull Optional<Reservation> read(final @NotNull Integer id) throws Exception {
    synchronized (this) {
      load();

      return cache.contains(new Reservation(id))
        ? Optional.ofNullable(cache.ceiling(new Reservation(id)))
        : Optional.empty();
    }
  }

  @Override
  public Set<Reservation> read() throws Exception {
    synchronized (this) {
      load();

      return cache;
    }
  }

  @Override
  public @NotNull Integer update(final @NotNull Reservation model) throws Exception {
    synchronized (this) {
      load();

      if (cache.contains(model)) {
        final Reservation cachedModel = cache.ceiling(model);
        if (Objects.nonNull(cachedModel)) {
          cachedModel.copy(model);
          return 1;
        }
      }

      return 0;
    }
  }

  @Override
  public @NotNull Integer delete(final @NotNull Reservation model) throws Exception {
    synchronized (this) {
      load();

      return cache.remove(model) ? 1 : 0;
    }
  }
}
