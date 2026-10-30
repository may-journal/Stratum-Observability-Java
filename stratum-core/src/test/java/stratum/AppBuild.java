package stratum;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * Compiles app code against Stratum and the notes catalog, as the app's own build would, to show
 * which mistakes the catalog stops at compile time.
 */
final class AppBuild {

  private AppBuild() {}

  /**
   * The compiler's errors for an app class whose one method runs {@code statement} with a {@code
   * stratum} service in scope, or none when it compiles. The classes it writes go to {@code out}.
   */
  static List<String> errors(Path out, String statement) throws URISyntaxException {
    String source =
        "package app;\n"
            + "import stratum.*;\n"
            + "class App {\n"
            + "  void run(Stratum stratum) {\n"
            + statement
            + "\n  }\n"
            + "}\n";
    JavaFileObject app =
        new SimpleJavaFileObject(URI.create("string:///app/App.java"), JavaFileObject.Kind.SOURCE) {
          @Override
          public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
          }
        };
    String classPath =
        where(Stratum.class) + java.io.File.pathSeparator + where(NotesCatalog.class);
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> found = new DiagnosticCollector<>();
    List<String> options = List.of("-classpath", classPath, "-d", out.toString());
    compiler.getTask(null, null, found, options, null, List.of(app)).call();
    return found.getDiagnostics().stream()
        .filter(each -> each.getKind() == Diagnostic.Kind.ERROR)
        .map(each -> each.getMessage(null))
        .toList();
  }

  private static String where(Class<?> type) throws URISyntaxException {
    return Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
  }
}
