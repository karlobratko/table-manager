package hr.kbratko.tablemanager.repository;

import hr.kbratko.tablemanager.repository.model.User;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Integer, User> {
  @NotNull Optional<User> login(final @NotNull String email, final @NotNull String password) throws Exception;
}
