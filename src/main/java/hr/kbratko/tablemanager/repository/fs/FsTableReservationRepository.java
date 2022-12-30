package hr.kbratko.tablemanager.repository.fs;

import hr.kbratko.tablemanager.repository.TableReservationRepository;
import hr.kbratko.tablemanager.repository.model.TableReservation;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FsTableReservationRepository
  implements FsRepository<TableReservation>,
             TableReservationRepository {
  private static final String FILE_NAME = "table_reservations.ser";

  private final @NotNull TreeSet<TableReservation> cache = new TreeSet<>();

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
          TableReservation tableReservation = (TableReservation)ois.readObject();
          cache.add(tableReservation);
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
      for (TableReservation tableReservation : cache)
        oos.writeObject(tableReservation);
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
  public @NotNull Integer create(final @NotNull TableReservation model) throws Exception {
    load();

    model.setId(nextId());
    cache.add(model);
    return 1;
  }

  @Override
  public @NotNull Integer create(final @NotNull Collection<TableReservation> models) throws Exception {
    load();
    
    Integer createdCount = 0;
    for (final var model : models) {
      model.setId(nextId());
      cache.add(model);
      createdCount++;
    }
    
    return createdCount;
  }

  @Override
  public @NotNull Optional<TableReservation> read(final @NotNull Integer id) throws Exception {
    load();

    return cache.contains(new TableReservation(id))
      ? Optional.ofNullable(cache.ceiling(new TableReservation(id)))
      : Optional.empty();
  }

  @Override
  public Set<TableReservation> read() throws Exception {
    load();

    return cache;
  }

  @Override
  public @NotNull Integer update(final @NotNull TableReservation model) throws Exception {
    load();

    if (cache.contains(model)) {
      final TableReservation cachedModel = cache.ceiling(model);
      if (Objects.nonNull(cachedModel)) {
        cachedModel.copy(model);
        return 1;
      }
    }

    return 0;
  }

  @Override
  public @NotNull Integer delete(final @NotNull TableReservation model) throws Exception {
    load();

    return cache.remove(model) ? 1 : 0;
  }

  @Override
  public @NotNull Integer delete(final @NotNull Collection<TableReservation> models) throws Exception {
    load();

    Integer deletedCount = 0;
    for (final var model : models) {
      cache.remove(model);
      deletedCount++;
    }
    
    return deletedCount;
  }
}
