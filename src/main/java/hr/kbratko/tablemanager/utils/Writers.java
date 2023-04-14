package hr.kbratko.tablemanager.utils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

public class Writers {
  private Writers() {
    throw new RuntimeException("No Writers instance for you!!!");
  }

  public static OutputStreamWriter outputStream(OutputStream out, Charset charset) {
    return new OutputStreamWriter(out, charset);
  }

  public static OutputStreamWriter outputStream(OutputStream out, String charsetName) throws UnsupportedEncodingException {
    return new OutputStreamWriter(out, charsetName);
  }

  public static BufferedWriter buffered(Writer out) {
    return new BufferedWriter(out);
  }

  public static BufferedWriter buffered(Writer out, int size) {
    return new BufferedWriter(out, size);
  }
}
