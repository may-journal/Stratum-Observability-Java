package io.github.mayjournal.stratum.otel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.mayjournal.stratum.CatalogException;
import io.github.mayjournal.stratum.Notes;
import io.github.mayjournal.stratum.Plugin;
import io.github.mayjournal.stratum.Stratum;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.testing.exporter.InMemoryLogRecordExporter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Requirement 0004: emitting log records. */
class OpenTelemetryTests {

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
      return new OpenTelemetryPlugin(provider);
    }

    List<LogRecordData> records() {
      return exporter.getFinishedLogRecordItems();
    }
  }

  /**
   * A person acts in the notes app, which publishes {@code key} through an OpenTelemetry plugin
   * wired to the app's own in-memory exporter; the one record that exporter holds after.
   */
  static LogRecordData record(String key, Map<String, String> fields) throws CatalogException {
    App app = App.start();
    Notes.stratum(app.plugin()).publish(key, fields);
    List<LogRecordData> records = app.records();
    assertEquals(1, records.size());
    return records.get(0);
  }

  static String attribute(LogRecordData record, String name) {
    return record.getAttributes().get(AttributeKey.stringKey(name));
  }

  @Test
  void test0004_1() throws CatalogException {
    LogRecordData record = record(Notes.SAVED, Map.of());
    assertEquals(Notes.SAVED, attribute(record, "stratum.key"));
    assertEquals("editor", attribute(record, "stratum.category"));
  }

  @Test
  void test0004_2() throws CatalogException {
    LogRecordData record = record("sync.failed", Map.of());
    assertEquals(Severity.ERROR, record.getSeverity());
    assertEquals("ERROR", record.getSeverityText());
  }

  @Test
  void test0004_3() throws CatalogException {
    LogRecordData record = record(Notes.SAVED, Map.of("words", "120"));
    assertEquals("120", attribute(record, "words"));
  }

  @Test
  void test0004_4() throws CatalogException {
    LogRecordData record = record(Notes.SAVED, Map.of("title", "Dentist at noon"));
    assertEquals(OpenTelemetryPlugin.PRIVATE, attribute(record, "title"));
    assertFalse(record.toString().contains("Dentist"));
  }

  @Test
  void test0004_5() throws CatalogException {
    LogRecordData record = record(Notes.SAVED, Map.of());
    assertEquals("A person saved a note", record.getBodyValue().asString());
    assertEquals("Notes", attribute(record, "stratum.product"));
    assertEquals("2.1", attribute(record, "stratum.version"));
    assertEquals("Notes", record.getInstrumentationScopeInfo().getName());
    assertEquals("2.1", record.getInstrumentationScopeInfo().getVersion());
  }

  @Test
  void test0004_6() throws CatalogException {
    App first = App.start();
    App second = App.start();
    Stratum stratum = Notes.stratum(first.plugin());
    stratum.add(second.plugin());
    stratum.publish(Notes.SAVED);
    assertTrue(first.records().isEmpty());
    assertEquals(1, second.records().size());
  }
}
