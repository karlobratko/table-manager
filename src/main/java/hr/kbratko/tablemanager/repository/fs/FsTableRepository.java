package hr.kbratko.tablemanager.repository.fs;

import hr.kbratko.tablemanager.repository.TableRepository;
import hr.kbratko.tablemanager.repository.model.Table;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;

public class FsTableRepository
  implements FsRepository<Table>,
  TableRepository {
  private static final String FILE_NAME = "tables.ser";
  private static boolean IS_USED = false;


  private final @NotNull TreeSet<Table> cache = new TreeSet<>();

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
          Table table = (Table) ois.readObject();
          cache.add(table);
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
      for (Table table : cache)
        oos.writeObject(table);
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
  public @NotNull Integer create(final @NotNull Table model) throws Exception {
    synchronized (this) {
      load();

      final Optional<Table> check = cache
        .stream()
        .filter(other -> other.getName().equals(model.getName()))
        .findFirst();
      if (check.isPresent())
        return 0;

      model.setId(nextId());
      cache.add(model);
      return 1;
    }
  }

  @Override
  public @NotNull Optional<Table> read(final @NotNull Integer id) throws Exception {
    synchronized (this) {
      load();

      Optional<Table> optional = cache.contains(new Table(id))
        ? Optional.ofNullable(cache.ceiling(new Table(id)))
        : Optional.empty();

      return optional;
    }
  }

  @Override
  public Set<Table> read() throws Exception {
    synchronized (this) {
      load();

      final var copy = new HashSet<>(cache);

      return copy;
    }
  }

  @Override
  public @NotNull Integer update(final @NotNull Table model) throws Exception {
    synchronized (this) {
      load();

      if (cache.contains(model)) {
        final Table cachedModel = cache.ceiling(model);
        if (Objects.nonNull(cachedModel)) {
          cachedModel.copy(model);
          return 1;
        }
      }

      return 0;
    }
  }

  @Override
  public @NotNull Integer delete(final @NotNull Table model) throws Exception {
    synchronized (this) {
      load();

      return cache.remove(model) ? 1 : 0;
    }
  }
}
