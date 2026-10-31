package stratum.otel.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.opentelemetry.api.metrics.MeterProvider;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import stratum.Catalog;
import stratum.CatalogException;
import stratum.Event;
import stratum.Stratum;

/** Requirement 0009: checking metric settings. */
class MetricSettingsTests {

  /**
   * The message adding, to an engine with the metrics plugin, a Sharing catalog whose shared note
   * has a public {@code to}, a private {@code message}, and {@code settings}, or none when null.
   */
  static String refusal(MetricSettings settings) {
    Event shared =
        Event.of(MetricType.METRIC, "A person shared a note", 10)
            .publicFields(ShareField.TO)
            .privateFields(ShareField.MESSAGE)
            .settings(settings);
    Catalog sharing = new Catalog("Sharing", "1.0", Map.of(ShareKey.SHARED, shared));
    Stratum stratum =
        new Stratum(
            "Notes",
            "2.1",
            NotebookCatalog.CATALOG,
            new OpenTelemetryMetricsPlugin(MeterProvider.noop()));
    return assertThrows(CatalogException.class, () -> stratum.add(sharing)).getMessage();
  }

  @Test
  void test0009_1() {
    assertEquals(
        "event note.shared is invalid: it has the METRIC type but no metric settings",
        refusal(null));
  }

  @Test
  void test0009_2() {
    MetricSettings shares =
        new MetricSettings(
            MetricKind.HISTOGRAM, "notes.shares", "{share}", "Shares", null, Set.of());
    assertEquals(
        "event note.shared is invalid: metric notes.shares needs a value field", refusal(shares));
  }

  @Test
  void test0009_3() {
    MetricSettings length =
        new MetricSettings(
            MetricKind.GAUGE, "notes.length", "{char}", "Length", ShareField.MESSAGE, Set.of());
    assertEquals(
        "event note.shared is invalid: metric value field message is not a public field",
        refusal(length));
  }

  @Test
  void test0009_4() {
    MetricSettings shares =
        MetricSettings.counter(
            "notes.shares", "{share}", "Shares", Set.of(ShareField.TO, ShareField.MESSAGE));
    assertEquals(
        "event note.shared is invalid: metric attribute message is not a public field",
        refusal(shares));
  }

  @Test
  void test0009_5() {
    MetricSettings shares =
        new MetricSettings(
            MetricKind.COUNTER,
            "notes.shares",
            "{share}",
            "Shares",
            ShareField.TO,
            Set.of(NotebookField.NOTES));
    assertEquals(
        "event note.shared is invalid: metric attribute notes is not a public field",
        refusal(shares));
  }
}
