package hr.kbratko.tablemanager.repository.fs;

import hr.kbratko.tablemanager.repository.TableRepository;
import hr.kbratko.tablemanager.repository.model.Table;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class FsTableRepository
  implements FsRepository<Table>,
             TableRepository {
  private static final String FILE_NAME = "tables.ser";

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
          Table table = (Table)ois.readObject();
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
    save();
  }

  @Override
  public @NotNull Integer nextId() {
    return cache.isEmpty()
      ? 1
      : cache.last().getId() + 1;
  }

  @Override
  public @NotNull Integer create(final @NotNull Table model) throws Exception {
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

  @Override
  public @NotNull Optional<Table> read(final @NotNull Integer id) throws Exception {
    load();

    return cache.contains(new Table(id))
      ? Optional.ofNullable(cache.ceiling(new Table(id)))
      : Optional.empty();
  }

  @Override
  public Set<Table> read() throws Exception {
    load();

    return cache;
  }

  @Override
  public @NotNull Integer update(final @NotNull Table model) throws Exception {
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

  @Override
  public @NotNull Integer delete(final @NotNull Table model) throws Exception {
    load();

    return cache.remove(model) ? 1 : 0;
  }
}
