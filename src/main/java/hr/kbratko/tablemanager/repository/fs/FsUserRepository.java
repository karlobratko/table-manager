package hr.kbratko.tablemanager.repository.fs;

import hr.kbratko.tablemanager.repository.UserRepository;
import hr.kbratko.tablemanager.repository.model.User;
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

public class FsUserRepository
  implements FsRepository<User>,
  UserRepository {
  private static final String FILE_NAME = "users.ser";

  private final @NotNull TreeSet<User> cache = new TreeSet<>();

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
          User user = (User) ois.readObject();
          cache.add(user);
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
      for (User user : cache)
        oos.writeObject(user);
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
  public @NotNull Integer create(final @NotNull User model) throws Exception {
    synchronized (this) {
      load();

      final Optional<User> check = cache
        .stream()
        .filter(other -> other.getEmail().equals(model.getEmail()))
        .findFirst();
      if (check.isPresent())
        return 0;

      model.setId(nextId());
      cache.add(model);
      return 1;
    }
  }

  @Override
  public @NotNull Optional<User> read(final @NotNull Integer id) throws Exception {
    synchronized (this) {
      load();

      return cache.contains(new User(id))
        ? Optional.ofNullable(cache.ceiling(new User(id)))
        : Optional.empty();
    }
  }

  @Override
  public Set<User> read() throws Exception {
    synchronized (this) {
      load();

      return cache;
    }
  }

  @Override
  public @NotNull Integer update(final @NotNull User model) throws Exception {
    synchronized (this) {
      load();

      if (cache.contains(model)) {
        final User cachedModel = cache.ceiling(model);
        if (Objects.nonNull(cachedModel)) {
          cachedModel.copy(model);
          return 1;
        }
      }

      return 0;
    }
  }

  @Override
  public @NotNull Integer delete(final @NotNull User model) throws Exception {
    synchronized (this) {
      load();

      return cache.remove(model) ? 1 : 0;
    }
  }

  @Override
  public @NotNull Optional<User> login(final @NotNull String email, final @NotNull String password) throws Exception {
    synchronized (this) {
      load();

      return cache
        .stream()
        .filter(user -> email.equals(user.getEmail()) && password.equals(user.getPassword()))
        .findFirst();
    }
  }
}
