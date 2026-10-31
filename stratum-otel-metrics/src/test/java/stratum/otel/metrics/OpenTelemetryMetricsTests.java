package stratum.otel.metrics;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import stratum.Plugin;
import stratum.Stratum;

/** Requirement 0006: recording metrics. */
class OpenTelemetryMetricsTests {

  /**
   * The reader an app configures, the metrics plugin whose provider records to it, and a notes
   * service with that plugin.
   */
  record App(InMemoryMetricReader reader, Plugin plugin, Stratum stratum) {
    static App start() {
      InMemoryMetricReader reader = InMemoryMetricReader.create();
      SdkMeterProvider provider = SdkMeterProvider.builder().registerMetricReader(reader).build();
      Plugin plugin = new OpenTelemetryMetricsPlugin(provider);
      return new App(reader, plugin, new Stratum("Notes", "2.1", NotebookCatalog.CATALOG, plugin));
    }

    Collection<MetricData> metrics() {
      return reader.collectAllMetrics();
    }

    /** The one metric the reader holds, which must be named {@code name}. */
    MetricData only(String name) {
      Collection<MetricData> metrics = metrics();
      assertEquals(1, metrics.size());
      MetricData metric = metrics.iterator().next();
      assertEquals(name, metric.getName());
      return metric;
    }
  }

  static DoublePointData sum(MetricData metric) {
    return metric.getDoubleSumData().getPoints().iterator().next();
  }

  @Test
  void test0006_1() {
    App app = App.start();
    app.stratum().publish(NotebookKey.OPENED, entry(NotebookField.SOURCE, "search"));
    app.stratum().publish(NotebookKey.OPENED, entry(NotebookField.SOURCE, "search"));
    DoublePointData point = sum(app.only("notes.opened"));
    assertEquals(2, point.getValue());
    assertEquals(Attributes.of(AttributeKey.stringKey("source"), "search"), point.getAttributes());
  }

  @Test
  void test0006_2() {
    App app = App.start();
    app.stratum().publish(NotebookKey.TYPED, entry(NotebookField.WORDS, "120"));
    MetricData metric = app.only("notes.words");
    assertEquals(120, sum(metric).getValue());
    assertEquals("{word}", metric.getUnit());
  }

  @Test
  void test0006_3() {
    App app = App.start();
    app.stratum()
        .publish(
            NotebookKey.SYNCED,
            entry(NotebookField.MILLIS, "420"),
            entry(NotebookField.KIND, "full"));
    MetricData metric = app.only("notes.sync.duration");
    HistogramPointData point = metric.getHistogramData().getPoints().iterator().next();
    assertEquals(List.of(1L, 420.0), List.of(point.getCount(), point.getSum()));
    assertEquals(Attributes.of(AttributeKey.stringKey("kind"), "full"), point.getAttributes());
    assertEquals("ms", metric.getUnit());
    assertEquals("How long a sync took", metric.getDescription());
  }

  @Test
  void test0006_4() {
    App app = App.start();
    app.stratum().publish(NotebookKey.COUNTED, entry(NotebookField.NOTES, "5"));
    app.stratum().publish(NotebookKey.COUNTED, entry(NotebookField.NOTES, "7"));
    MetricData metric = app.only("notes.count");
    assertEquals(7, metric.getDoubleGaugeData().getPoints().iterator().next().getValue());
  }

  @Test
  void test0006_5() {
    App app = App.start();
    assertTrue(app.stratum().publish(NotebookKey.TYPED, entry(NotebookField.WORDS, "many")));
    assertTrue(app.stratum().publish(NotebookKey.SYNCED, entry(NotebookField.KIND, "full")));
    assertTrue(app.metrics().isEmpty());
  }

  @Test
  void test0006_6() {
    App app = App.start();
    app.stratum().publish(NotebookKey.SAVED, entry(NotebookField.WORDS, "120"));
    assertTrue(app.metrics().isEmpty());
  }

  @Test
  void test0006_7() {
    App app = App.start();
    app.stratum()
        .publish(
            NotebookKey.OPENED,
            entry(NotebookField.SOURCE, "search"),
            entry(NotebookField.TITLE, "Dentist at noon"));
    DoublePointData point = sum(app.only("notes.opened"));
    assertEquals(Attributes.of(AttributeKey.stringKey("source"), "search"), point.getAttributes());
  }

  @Test
  void test0006_8() {
    App first = App.start();
    App second = App.start();
    first.stratum().add(second.plugin());
    first.stratum().publish(NotebookKey.COUNTED, entry(NotebookField.NOTES, "5"));
    assertTrue(first.metrics().isEmpty());
    assertEquals(1, second.metrics().size());
  }
}
