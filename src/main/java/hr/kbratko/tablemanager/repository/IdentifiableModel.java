package hr.kbratko.tablemanager.repository;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public abstract class IdentifiableModel<K extends Comparable<K>> implements Identifiable<K>, Comparable<Identifiable<K>>, Serializable {
  
  @Serial
  private static final long serialVersionUID = 1L;
  
  protected K id;

  protected IdentifiableModel() {}

  protected IdentifiableModel(final K id) {this.id = id;}

  @Override
  public K getId() {return id;}

  @Override
  public void setId(@NotNull final K id) {this.id = id;}

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (Objects.isNull(o) || getClass() != o.getClass()) return false;
    final IdentifiableModel<?> that = (IdentifiableModel<?>)o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public int compareTo(@NotNull final Identifiable<K> o) {
    return id.compareTo(o.getId());
  }
}
