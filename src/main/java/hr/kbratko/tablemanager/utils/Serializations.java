package hr.kbratko.tablemanager.utils;

import org.jetbrains.annotations.Contract;

import java.io.*;

public final class Serializations {
  @Contract(value = " -> fail", pure = true)
  private Serializations() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Serializations instances for you!");}

  public static <T> void write(T t, String fileName) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
      oos.writeObject(t);
    }
  }

  public static <T> T read(String fileName) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
      return (T)ois.readObject();
    }
  }
}
