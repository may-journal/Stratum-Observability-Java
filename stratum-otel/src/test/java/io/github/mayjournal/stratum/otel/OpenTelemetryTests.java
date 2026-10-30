package io.github.mayjournal.stratum.otel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.mayjournal.stratum.Catalog;
import io.github.mayjournal.stratum.CatalogException;
import io.github.mayjournal.stratum.Event;
import io.github.mayjournal.stratum.Level;
import io.github.mayjournal.stratum.Stratum;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.testing.exporter.InMemoryLogRecordExporter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Requirement 0004: emitting log records. */
class OpenTelemetryTests {

  /** The key a person's saved note publishes under. */
  static final String SAVED = "note.saved";

  /** The catalog every requirement starts from: a note a person saves, and a failed sync. */
  static final Catalog NOTES =
      new Catalog(
          "Notes",
          "1.0",
          Map.of(
              SAVED,
              new Event("A person saved a note", "editor", Level.INFO, Set.of("words"), Set.of("title")),
              "sync.failed",
              new Event("The notes failed to sync", "sync", Level.ERROR)));

  /**
   * A person acts in the notes app, which publishes {@code key} through an OpenTelemetry plugin
   * wired to the app's own in-memory exporter; the records that exporter holds after.
   */
  static List<LogRecordData> publish(String key, Map<String, String> fields)
      throws CatalogException {
    InMemoryLogRecordExporter exporter = InMemoryLogRecordExporter.create();
    try (SdkLoggerProvider provider =
        SdkLoggerProvider.builder()
            .addLogRecordProcessor(SimpleLogRecordProcessor.create(exporter))
            .build()) {
      Stratum stratum =
          new Stratum("Notes", "2.1", List.of(NOTES), List.of(new OpenTelemetryPlugin(provider)));
      stratum.publish(key, fields);
      return exporter.getFinishedLogRecordItems();
    }
  }

  /** The one record the exporter holds after a person acts. */
  static LogRecordData record(String key, Map<String, String> fields) throws CatalogException {
    List<LogRecordData> records = publish(key, fields);
    assertEquals(1, records.size());
    return records.get(0);
  }

  static String attribute(LogRecordData record, String name) {
    return record.getAttributes().get(AttributeKey.stringKey(name));
  }

  @Test
  void test0004_1() throws CatalogException {
    LogRecordData record = record(SAVED, Map.of());
    assertEquals(SAVED, attribute(record, "stratum.key"));
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
    LogRecordData record = record(SAVED, Map.of("words", "120"));
    assertEquals("120", attribute(record, "words"));
  }

  @Test
  void test0004_4() throws CatalogException {
    LogRecordData record = record(SAVED, Map.of("title", "Dentist at noon"));
    assertEquals(OpenTelemetryPlugin.PRIVATE, attribute(record, "title"));
    assertFalse(record.toString().contains("Dentist"));
  }

  @Test
  void test0004_5() throws CatalogException {
    LogRecordData record = record(SAVED, Map.of());
    assertEquals("A person saved a note", record.getBodyValue().asString());
    assertEquals("Notes", attribute(record, "stratum.product"));
    assertEquals("2.1", attribute(record, "stratum.version"));
    assertEquals("Notes", record.getInstrumentationScopeInfo().getName());
    assertEquals("2.1", record.getInstrumentationScopeInfo().getVersion());
  }
}
