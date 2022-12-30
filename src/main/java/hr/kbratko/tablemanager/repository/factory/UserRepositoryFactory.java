package hr.kbratko.tablemanager.repository.factory;

import hr.kbratko.tablemanager.repository.UserRepository;
import hr.kbratko.tablemanager.repository.fs.FsUserRepository;

public class UserRepositoryFactory {
  private static final UserRepository repository = new FsUserRepository();

  private UserRepositoryFactory() {throw new AssertionError("No hr.kbratko.tablemanager.repository.factory.UserRepositoryFactory instances for you!");}

  public static UserRepository getInstance() {return repository;}
}
