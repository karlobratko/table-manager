package hr.kbratko.tablemanager.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Readers {
  private Readers() {
    throw new RuntimeException("No Readers instance for you!!!");
  }

  public static InputStreamReader inputStream(InputStream is) {
    return new InputStreamReader(is);
  }

  public static BufferedReader buffered(Reader in) {
    return new BufferedReader(in);
  }

  public static InputStreamReader inputStream(InputStream is, String charsetName) throws UnsupportedEncodingException {
    return new InputStreamReader(is, charsetName);
  }

  public static InputStreamReader inputStream(InputStream is, Charset charset) {
    return new InputStreamReader(is, charset);
  }

  public static BufferedReader buffered(Reader in, int size) {
    return new BufferedReader(in, size);
  }
}
