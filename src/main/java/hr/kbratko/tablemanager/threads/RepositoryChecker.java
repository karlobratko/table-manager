package hr.kbratko.tablemanager.threads;

import hr.kbratko.tablemanager.repository.CrudRepository;
import hr.kbratko.tablemanager.repository.Identifiable;
import hr.kbratko.tablemanager.repository.factory.TableRepositoryFactory;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositoryChecker<K extends Comparable<K>, T extends Identifiable<K>> {
  private static final int THREAD_POOL_SIZE = 100;

  private static final Logger logger = Logger.getLogger(RepositoryChecker.class.getName());

  private final CrudRepository<K, T> repository;
  private final ExecutorService executor;

  private boolean shouldRun = true;

  private RepositoryChecker(CrudRepository<K, T> repository) {
    this.repository = repository;
    this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  }

  public static <K extends Comparable<K>, T extends Identifiable<K>> RepositoryChecker<K, T> from(CrudRepository<K, T> repository) {
    return new RepositoryChecker<>(repository);
  }

  public void start() {
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      executor.submit(() -> {
        while (shouldRun) {
          try {
            Set<T> data = repository.read();
            logger.info("Repository has %d elements. Thread %d".formatted(data.size(), Thread.currentThread().getId()));
          } catch (Exception e) {
            logger.log(Level.WARNING, "Could not read data from repository", e);
          }
        }
      });
    }
  }

  public void stop() {
    shouldRun = false;

    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS))
          logger.warning("Pool did not terminate");
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {
    RepositoryChecker.from(TableRepositoryFactory.getInstance()).start();
  }
}
