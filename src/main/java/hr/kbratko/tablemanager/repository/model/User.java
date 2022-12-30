package hr.kbratko.tablemanager.repository.model;

import hr.kbratko.tablemanager.repository.Copyable;
import hr.kbratko.tablemanager.repository.IdentifiableModel;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class User
  extends IdentifiableModel<Integer>
  implements Copyable<User>, Serializable {
  
  @Serial
  private static final long serialVersionUID = 1L;

  // properties

  private UserType type;
  private String   firstName;
  private String   lastName;
  private String   email;
  private String   password;

  // builder

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Integer  id;
    private UserType type;
    private String   firstName;
    private String   lastName;
    private String   email;
    private String   password;

    private Builder() {}

    public @NotNull Builder id(final @NotNull Integer id) {
      this.id = id;
      return this;
    }

    public @NotNull Builder type(final @NotNull UserType type) {
      this.type = type;
      return this;
    }

    public @NotNull Builder firstName(final @NotNull String firstName) {
      this.firstName = firstName;
      return this;
    }

    public @NotNull Builder lastName(final @NotNull String lastName) {
      this.lastName = lastName;
      return this;
    }

    public @NotNull Builder email(final @NotNull String email) {
      this.email = email;
      return this;
    }

    public @NotNull Builder password(final @NotNull String password) {
      this.password = password;
      return this;
    }

    public @NotNull User build() {return new User(this);}
  }

  // constructors

  private User(final @NotNull Builder builder) {
    super(builder.id);
    this.type      = builder.type;
    this.firstName = builder.firstName;
    this.lastName  = builder.lastName;
    this.email     = builder.email;
    this.password  = builder.password;
  }

  public User(final @NotNull Integer id) {super(id);}

  // getters and setters

  public UserType getType() {return type;}

  public void setType(final @NotNull UserType type) {this.type = type;}

  public String getFirstName() {return firstName;}

  public void setFirstName(final @NotNull String firstName) {this.firstName = firstName;}

  public String getLastName() {return lastName;}

  public void setLastName(final @NotNull String lastName) {this.lastName = lastName;}

  public String getFullName() {return "%s %s".formatted(firstName, lastName);}

  public String getEmail() {return email;}

  public void setEmail(final @NotNull String email) {this.email = email;}

  public String getPassword() {return password;}

  public void setPassword(final @NotNull String password) {this.password = password;}

  // overrides

  @Override
  public void copy(final @NotNull User from) {
    this.type      = from.type;
    this.firstName = from.firstName;
    this.lastName  = from.lastName;
    this.email     = from.email;
    this.password  = from.password;
  }
}
