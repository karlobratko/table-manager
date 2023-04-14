package hr.kbratko.tablemanager.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.zip.GZIPOutputStream;

public class Streams {
  private Streams() {
    throw new RuntimeException("No Streams instance for you!!!");
  }

  public static FileInputStream fileInput(String filename) throws FileNotFoundException {
    return new FileInputStream(filename);
  }

  public static FileInputStream fileInput(File file) throws FileNotFoundException {
    return new FileInputStream(file);
  }

  public static DigestInputStream digestInput(InputStream in, MessageDigest digest) {
    return new DigestInputStream(in, digest);
  }

  public static BufferedInputStream bufferedInput(InputStream in) {
    return new BufferedInputStream(in);
  }

  public static DataInputStream dataInput(InputStream in) {
    return new DataInputStream(in);
  }

  public static ObjectInputStream objectInput(InputStream in) throws IOException {
    return new ObjectInputStream(in);
  }

  public static FileOutputStream fileOutput(File file) throws FileNotFoundException {
    return new FileOutputStream(file);
  }

  public static GZIPOutputStream gzipOutput(OutputStream out) throws IOException {
    return new GZIPOutputStream(out);
  }

  public static BufferedOutputStream bufferedOutput(OutputStream out) {
    return new BufferedOutputStream(out);
  }

  public static DataOutputStream dataOutput(OutputStream out) {
    return new DataOutputStream(out);
  }

  public static ObjectOutputStream objectOutput(OutputStream out) throws IOException {
    return new ObjectOutputStream(out);
  }
}
