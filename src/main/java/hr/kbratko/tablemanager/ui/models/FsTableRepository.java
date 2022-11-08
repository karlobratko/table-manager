package hr.kbratko.tablemanager.ui.models;

import hr.kbratko.tablemanager.utils.Serializations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class FsTableRepository {
  private static final String            FILE_NAME     = "tables_cache.txt";
  private static final String            FILE_NAME_SER = "tables.ser";
  private static final String            DELIMITER     = "###";
  private static final FsTableRepository INSTANCE      = new FsTableRepository();

  private static final ObservableList<Table> TABLES = FXCollections.observableArrayList();

  @Contract(pure = true)
  private FsTableRepository() {}

  @Contract(pure = true)
  public static FsTableRepository getInstance() {return INSTANCE;}

  @Contract(pure = true)
  public ObservableList<Table> getTables() {return TABLES;}

  public void addTable(final @NotNull Table item) {
    TABLES.add(item);
  }

  public void removeTable(final @NotNull Table item) {TABLES.remove(item);}

  public void loadTables() throws Exception {
    Serializations.<List<TableProjection>>read(FILE_NAME_SER)
                  .forEach(item ->
                             TABLES.add(
                               new Table
                                 .Builder(item.getId(),
                                          item.getName(),
                                          item.getNrSeats())
                                 .description(item.getDescription())
                                 .build()));
  }

  public void saveTables() throws IOException {
    Serializations.write(
      TABLES.stream()
            .map(item -> new TableProjection(item.getId(),
                                             item.getName(),
                                             item.getNrSeats(),
                                             item.getDescription()))
            .toList(),
      FILE_NAME_SER);
  }
}
