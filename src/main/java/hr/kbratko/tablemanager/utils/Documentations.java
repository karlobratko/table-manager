package hr.kbratko.tablemanager.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class Documentations {
  private static final int TAB_SIZE = 2;

  @Contract(value = " -> fail", pure = true)
  private Documentations() {throw new AssertionError("No hr.kbratko.tablemanager.utils.Documentations instances for you!");}


  @Contract(pure = true)
  public static void generate(final @NotNull String srcDir,
                              final @NotNull String dstDir) throws IOException {
    final var docFile = new File(dstDir);

    final var writer = new FileWriter(docFile);

    writer.write("<!DOCTYPE html>");
    writer.write("<html>");
    writer.write("<head>");
    writer.write("<title>Documentation</title>");
    writer.write("</head>");
    writer.write("<body>");
    writer.write("<h1>Documentation</h1>");

    final var paths = Files.walk(Paths.get(srcDir))
                           .filter(path -> path.getFileName().toString().endsWith(".class"))
                           .toList();

    for (final var path : paths) {
      String[] tokens = path.toString().split(Pattern.quote(System.getProperty("file.separator")));


      final var classNameBuilder = new StringBuilder();
      boolean   canBuildPath     = false;
      for (final var token : tokens) {
        if ("classes".equals(token)) {
          canBuildPath = true;
          continue;
        }

        if (canBuildPath) {
          if (token.endsWith(".class"))
            classNameBuilder.append(token, 0, token.indexOf(".class"));
          else {
            classNameBuilder.append(token)
                            .append(".");
          }
        }
      }

      if ("module-info".equals(classNameBuilder.toString()))
        continue;

      System.out.println(classNameBuilder);

      try {
        Class<?> clazz = Class.forName(classNameBuilder.toString());

        writer.write("<hr/>");
        writer.write("<br/>");

        writePackage(writer, clazz);

        writeModifiers(writer, clazz);


        writeName(writer, clazz);

        writeSupers(writer, clazz);

        writeInterfaces(writer, clazz);

        writeCtors(writer, clazz);

        writeFields(writer, clazz);

        writeMethods(writer, clazz);

        writer.write("<br/><br/>");
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }

    }
    
    writer.write("</body>");
    writer.write("</html>");
    writer.close();
  }

  private static void writePackage(final @NotNull FileWriter writer, final @NotNull Class<?> clazz) throws IOException {
    writer.write("<span>");
    writer.write(Reflections.getPackage(clazz).toString());
    writer.write("</span>");
  }

  private static void writeModifiers(final @NotNull FileWriter writer, final @NotNull Class<?> clazz) throws IOException {
    final var modifier = Modifier.toString(clazz.getModifiers());
    if (modifier.length() > 0) {
      writer.write("<br/><br/>");
      writer.write("<span>");
      writer.write(modifier);
      writer.write("</span>");
      writer.write("&nbsp;");
    }
  }

  private static void writeName(final @NotNull FileWriter writer, final @NotNull Class<?> clazz) throws IOException {
    writer.write("<span>");
    writer.write(clazz.getSimpleName());
    writer.write("</span>");
  }

  private static void writeSupers(final @NotNull FileWriter writer, final @NotNull Class<?> clazz) throws IOException {
    final var supers = Reflections.getSupers(clazz);
    if (supers.size() > 0) {
      writer.write("<br/><br/>");
      writer.write("&nbsp;".repeat(TAB_SIZE));

      writer.write("<span>");
      writer.write("extends");


      int     spaces = TAB_SIZE + "extends".length() + 1;
      boolean fst    = true;
      for (final var sup : supers) {
        if (fst) {
          fst = false;
          writer.write("&nbsp;".repeat(1));
        }
        else {
          spaces += TAB_SIZE;
          writer.write("<br/>");
          writer.write("&nbsp;".repeat(spaces));
        }

        writer.write(sup.getName());
      }
      writer.write("</span>");
    }
  }

  private static void writeInterfaces(final @NotNull FileWriter writer, final @NotNull Class<?> clazz) throws IOException {
    final var interfaces = Arrays.stream(clazz.getInterfaces()).toList();
    if (interfaces.size() > 0) {
      writer.write("<br/><br/>");
      writer.write("&nbsp;".repeat(TAB_SIZE));

      writer.write("<span>");
      writer.write("implements");

      int     spaces = TAB_SIZE + "implements".length() + 1;
      boolean fst    = true;
      for (final var intf : interfaces) {
        if (fst) {
          fst = false;
          writer.write("&nbsp;".repeat(1));
        }
        else {
          writer.write("<br/>");
          writer.write("&nbsp;".repeat(spaces));
        }

        writer.write(intf.getName());
      }

      writer.write("</span>");
    }
  }

  private static void writeCtors(final @NotNull FileWriter writer, final @NotNull Class<?> clazz) throws IOException {
    final var ctors = Arrays.stream(clazz.getDeclaredConstructors()).toList();

    writeExecutable(writer, ctors, "constructors");
  }

  private static void writeMethods(final FileWriter writer, final Class<?> clazz) throws IOException {
    final var methods = Reflections.getMethods(clazz);
    
    writeExecutable(writer, methods, "methods");
  }

  private static <T extends Executable> void writeExecutable(final @NotNull FileWriter writer,
                                      final @NotNull List<T> execs,
                                      final @NotNull String title) throws IOException {
    if (execs.size() > 0) {
      writer.write("<br/><br/>");
      writer.write("&nbsp;".repeat(TAB_SIZE));

      writer.write("<span>");
      writer.write(title);

      int     spaces  = TAB_SIZE + title.length() + 1;
      boolean fstCtor = true;
      for (final var exec : execs) {
        if (fstCtor) {
          fstCtor = false;
          writer.write("<br/>");
        }
        else
          writer.write("<br/><br/>");

        writeAnnotations(writer, exec, spaces);

        writer.write("&nbsp;".repeat(spaces));

        final var modifs = Modifier.toString(exec.getModifiers());
        writer.write(modifs);
        writer.write("&nbsp;");

        final var execSimpleName = exec.getName().substring(exec.getName().lastIndexOf('.') + 1);
        writer.write(execSimpleName);

        final int paramSpaces = spaces + execSimpleName.length() + modifs.length() + 2;
        writeParameters(writer, exec, paramSpaces);

        writeExceptions(writer, exec);
      }
      writer.write("</span>");
    }
  }

  private static void writeAnnotations(final @NotNull FileWriter writer,
                                       final @NotNull Executable exec,
                                       final int spaces) throws IOException {
    final var annots = Arrays.stream(exec.getAnnotations()).toList();
    for (final var annot : annots) {
      writer.write("&nbsp;".repeat(spaces));
      writer.write(annot.toString());
      writer.write("<br/>");
    }
  }

  private static void writeParameters(final @NotNull FileWriter writer,
                                  final @NotNull Executable exec,
                                  final int spaces) throws IOException {
    writer.write("(");
    
    final var params   = Arrays.stream(exec.getParameters()).toList();
    boolean   fstParam = true;
    for (int i = 0; i < params.size(); i++) {
      if (fstParam) {
        fstParam = false;
      }
      else {
        writer.write("<br/>");
        writer.write("&nbsp;".repeat(spaces));
      }
        
      writer.write(params.get(i).getType().getSimpleName());

      final var annots = Arrays.stream(params.get(i).getAnnotations()).toList();
      for (final var annot : annots) {
        writer.write("&nbsp;");
        writer.write(annot.toString());
      }

      writer.write("&nbsp;");
      writer.write("var");
      writer.write(String.valueOf(i));

      if (i != params.size() - 1)
        writer.write(",");
    }
    writer.write(")");
  }
  
  private static void writeExceptions(final @NotNull FileWriter writer,
                                      final @NotNull Executable exec) throws IOException {
    final var excepts = Arrays.stream(exec.getExceptionTypes()).toList();
    if (excepts.size() > 0) {
      writer.write("&nbsp;");
      writer.write("throws");

      for (int i = 0; i < excepts.size(); i++) {
        writer.write("&nbsp;");
        writer.write(excepts.get(i).getSimpleName());

        if (i != excepts.size() - 1)
          writer.write(",");
      }
    }
  }

  private static void writeFields(final @NotNull FileWriter writer,
                                  final @NotNull Class<?> clazz) throws IOException {
    final var fields = Reflections.getFields(clazz);
    if (fields.size() > 0) {
      writer.write("<br/><br/>");
      writer.write("&nbsp;".repeat(TAB_SIZE));

      writer.write("<span>");
      writer.write("fields");

      int     spaces   = TAB_SIZE + "fields".length() + 1;
      boolean fstField = true;
      for (final var field : fields) {
        if (fstField) {
          fstField = false;
          writer.write("<br/>");
        }
        else {
          writer.write("<br/><br/>");
        }

        final var annots = Arrays.stream(field.getAnnotations()).toList();
        for (final var annot : annots) {
          writer.write("&nbsp;".repeat(spaces));
          writer.write(annot.toString());
          writer.write("<br/>");
        }

        writer.write("&nbsp;".repeat(spaces));
        writer.write(Modifier.toString(field.getModifiers()));
        writer.write("&nbsp;");
        writer.write(field.getType().getSimpleName());
        writer.write("&nbsp;");
        writer.write(field.getName());
      }
      writer.write("</span>");
    }
  }
}
