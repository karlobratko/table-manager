package hr.kbratko.tablemanager.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class Reflections {
  @Contract(value = " -> fail", pure = true)
  private Reflections() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Reflections instances for you!");}

  @Contract(pure = true)
  public static @NotNull Package getPackage(final @NotNull Class<?> clazz) {
    return clazz.getPackage();
  }

  @Contract(pure = true)
  public static int getModifiers(final @NotNull Class<?> clazz) {
    return clazz.getModifiers();
  }

  @Contract(pure = true)
  public static int getModifiers(final @NotNull Field field) {
    return field.getModifiers();
  }

  @Contract(pure = true)
  public static int getModifiers(final @NotNull Method method) {
    return method.getModifiers();
  }

  public static @NotNull List<Class<?>> getSupers(final @NotNull Class<?> clazz) {
    Class<?> parent = clazz.getSuperclass();
    if (parent == null) {
      return Collections.mutableListOf();
    }
    else {
      final var            supers  = getSupers(parent);
      final List<Class<?>> classes = Collections.mutableListOf(parent);
      classes.addAll(supers);
      return classes;
    }
  }

  @Contract(pure = true)
  public static @NotNull List<Class<?>> getInterfaces(final @NotNull Class<?> clazz) {
    return Arrays.stream(clazz.getInterfaces()).toList();
  }

  @Contract(pure = true)
  public static @NotNull List<Field> getFields(final @NotNull Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredFields()).toList();
  }

  @Contract(pure = true)
  public static @NotNull List<Method> getMethods(final @NotNull Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredMethods()).toList();
  }
}
