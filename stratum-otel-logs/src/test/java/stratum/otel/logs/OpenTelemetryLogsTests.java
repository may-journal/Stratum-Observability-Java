package stratum.otel.logs;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.testing.exporter.InMemoryLogRecordExporter;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import stratum.Plugin;
import stratum.Stratum;

/** Requirement 0004: emitting log records. */
class OpenTelemetryLogsTests {

  /** The exporter an app configures, and the provider that delivers records to it. */
  record App(InMemoryLogRecordExporter exporter, SdkLoggerProvider provider) {
    static App start() {
      InMemoryLogRecordExporter exporter = InMemoryLogRecordExporter.create();
      SdkLoggerProvider provider =
          SdkLoggerProvider.builder()
              .addLogRecordProcessor(SimpleLogRecordProcessor.create(exporter))
              .build();
      return new App(exporter, provider);
    }

    Plugin plugin() {
      return new OpenTelemetryLogsPlugin(provider);
    }

    List<LogRecordData> records() {
      return exporter.getFinishedLogRecordItems();
    }
  }

  /** An engine for the notes product, version 2.1, with {@code plugins}. */
  static Stratum start(Plugin... plugins) {
    return new Stratum("Notes", "2.1", NotebookCatalog.CATALOG, plugins);
  }

  /**
   * A person acts in the notes app, whose code {@code publishes} through an OpenTelemetry plugin
   * wired to the app's own in-memory exporter; the one record that exporter holds after.
   */
  static LogRecordData record(Consumer<Stratum> publishes) {
    App app = App.start();
    publishes.accept(start(app.plugin()));
    List<LogRecordData> records = app.records();
    assertEquals(1, records.size());
    return records.get(0);
  }

  static String attribute(LogRecordData record, String name) {
    return record.getAttributes().get(AttributeKey.stringKey(name));
  }

  @Test
  void test0004_1() {
    LogRecordData record = record(stratum -> stratum.publish(NotebookKey.SAVED));
    assertEquals("note.saved", attribute(record, "stratum.key"));
    assertEquals(1L, record.getAttributes().get(OpenTelemetryLogsPlugin.ID));
  }

  @Test
  void test0004_2() {
    LogRecordData record = record(stratum -> stratum.publish(NotebookKey.FAILED));
    assertEquals(Severity.ERROR, record.getSeverity());
    assertEquals("ERROR", record.getSeverityText());
  }

  @Test
  void test0004_3() {
    LogRecordData record =
        record(stratum -> stratum.publish(NotebookKey.SAVED, entry(NotebookField.WORDS, "120")));
    assertEquals("120", attribute(record, "words"));
  }

  @Test
  void test0004_4() {
    LogRecordData record =
        record(
            stratum ->
                stratum.publish(NotebookKey.SAVED, entry(NotebookField.TITLE, "Dentist at noon")));
    assertEquals(OpenTelemetryLogsPlugin.PRIVATE, attribute(record, "title"));
    assertFalse(record.toString().contains("Dentist"));
  }

  @Test
  void test0004_5() {
    LogRecordData record = record(stratum -> stratum.publish(NotebookKey.SAVED));
    assertEquals("A person saved a note", record.getBodyValue().asString());
    assertEquals("Notes", attribute(record, "stratum.product"));
    assertEquals("2.1", attribute(record, "stratum.version"));
    assertEquals("Notes", record.getInstrumentationScopeInfo().getName());
    assertEquals("2.1", record.getInstrumentationScopeInfo().getVersion());
  }

  @Test
  void test0004_6() {
    App first = App.start();
    App second = App.start();
    Stratum stratum = start(first.plugin());
    stratum.add(second.plugin());
    stratum.publish(NotebookKey.SAVED);
    assertTrue(first.records().isEmpty());
    assertEquals(1, second.records().size());
  }

  @Test
  void test0004_7() {
    App app = App.start();
    start(app.plugin()).publish(NotebookKey.OPENED, entry(NotebookField.SOURCE, "search"));
    assertTrue(app.records().isEmpty());
  }
}
