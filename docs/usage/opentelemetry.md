# Using OpenTelemetry

Stratum has two OpenTelemetry plugins. `OpenTelemetryLogsPlugin` in `stratum-otel-logs` sends events as log records. `OpenTelemetryMetricsPlugin` in `stratum-otel-metrics` sends events as counters, histograms and gauges. An app adds either plugin, or both.

Stratum also has a text log plugin, `LogPlugin` in `stratum-log`, which writes plain lines through `System.Logger`. It needs no server and no SDK. An app can add it beside the OpenTelemetry plugins.

| Plugin | Module | Takes | Sends through |
| --- | --- | --- | --- |
| `LogPlugin` | `stratum-log` | `LogType.LOG` events | `System.Logger`, as one text line |
| `OpenTelemetryLogsPlugin` | `stratum-otel-logs` | `OpenTelemetryLogType.LOG` events | The app's `LoggerProvider`, as a log record |
| `OpenTelemetryMetricsPlugin` | `stratum-otel-metrics` | `MetricType.METRIC` events | The app's `MeterProvider`, as a measurement |

## Who owns what

The app owns the OpenTelemetry SDK. It builds the `SdkLoggerProvider` and `SdkMeterProvider`, picks the exporters, and shuts them down. Each plugin takes one provider and sends through it, so it opens no connection of its own.

Both plugin modules depend only on `io.opentelemetry:opentelemetry-api`. The app adds the SDK and exporter artifacts itself, at the versions it already runs.

## The catalog decides

Each catalog entry has one event type. The logs plugins accept the `LOG` type, and the metrics plugin accepts `MetricType.METRIC`. Types match by name, so `LogType.LOG` and `OpenTelemetryLogType.LOG` reach both log plugins.

The plugin list never widens what an event sends. An event whose type no added plugin accepts reaches no plugin. Its `publish` call still returns true, because a catalog holds the key.

## How a snapshot becomes a log record

The logs plugin gets a logger named for the product, at the product's version. Each record carries the following.

| Record part | What it holds |
| --- | --- |
| Body | The event's description. |
| Timestamp | The moment the app published. |
| Severity | The event's level, from the table below. |
| Severity text | The level's name, such as `ERROR`. |
| Attributes | `stratum.key`, `stratum.id`, `stratum.product`, `stratum.version`, the context and the public fields. |
| Private fields | Their names, each with the value `<private>`. |

| Level | Severity |
| --- | --- |
| `DEBUG` | `DEBUG` |
| `INFO` | `INFO` |
| `NOTICE` | `INFO2` |
| `WARNING` | `WARN` |
| `ERROR` | `ERROR` |
| `FAULT` | `FATAL` |

## How a snapshot becomes a metric

The metrics plugin gets a meter named for the product, at the product's version. The event's `MetricSettings` name the `MetricKind`, the metric's name, unit and description. It also names, by the app's `CatalogField` constants, the public field that holds the value and the public fields that become attributes.

| Kind | What one event does |
| --- | --- |
| `COUNTER` | Adds the value field's number, or adds one when the metric names no value field. |
| `HISTOGRAM` | Records the value field's number into a distribution. |
| `GAUGE` | Sets the latest value, through the synchronous gauge. |

Only the attribute fields become attributes. Context, the key and private fields stay out, which keeps cardinality low. A value that is missing or is not a number records nothing, and publishing goes on.

The metrics plugin checks each metric event when its catalog or the plugin is added. An event with the metric type and no `MetricSettings` fails with `CatalogException`. So does a value field or attribute field that is not one of the event's public fields.

## A catalog with a counter and a histogram

`SAVED` has the log type. `OPENED` has the metric type, with a counter that adds one. `SYNCED` has the metric type, with a histogram. The key enum, the field enum and the catalog each sit in their own file.

### NoteKey.java

```java
import stratum.CatalogKey;

public enum NoteKey implements CatalogKey {
  SAVED("note.saved"),
  OPENED("note.opened"),
  SYNCED("sync.finished");

  private final String key;

  NoteKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
```

### NoteField.java

```java
import stratum.CatalogField;

public enum NoteField implements CatalogField {
  WORDS("words"),
  TITLE("title"),
  SOURCE("source"),
  MILLIS("millis"),
  KIND("kind");

  private final String key;

  NoteField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
```

### NotesCatalog.java

```java
import java.util.Map;
import java.util.Set;
import stratum.Catalog;
import stratum.Event;
import stratum.otel.logs.OpenTelemetryLogType;
import stratum.otel.metrics.MetricKind;
import stratum.otel.metrics.MetricSettings;
import stratum.otel.metrics.MetricType;

public final class NotesCatalog {
  public static final Catalog CATALOG = new Catalog("Notes", "1.0", Map.of(
      NoteKey.SAVED, Event.of(OpenTelemetryLogType.LOG, "A person saved a note", 1)
          .publicFields(NoteField.WORDS)
          .privateFields(NoteField.TITLE),
      NoteKey.OPENED, Event.of(MetricType.METRIC, "A person opened a note", 2)
          .publicFields(NoteField.SOURCE)
          .settings(MetricSettings.counter("notes.opened", "{note}", "Notes a person opened",
              Set.of(NoteField.SOURCE))),
      NoteKey.SYNCED, Event.of(MetricType.METRIC, "The notes finished syncing", 3)
          .publicFields(NoteField.MILLIS, NoteField.KIND)
          .settings(new MetricSettings(MetricKind.HISTOGRAM, "notes.sync.duration", "ms",
              "How long a sync took", NoteField.MILLIS, Set.of(NoteField.KIND)))));

  private NotesCatalog() {}
}
```

With all three plugins added, each event reaches these places.

| Event | Text log plugin | OpenTelemetry logs plugin | OpenTelemetry metrics plugin |
| --- | --- | --- | --- |
| `note.saved` | One text line | One log record | Nothing |
| `note.opened` | Nothing | Nothing | The `notes.opened` counter |
| `sync.finished` | Nothing | Nothing | The `notes.sync.duration` histogram |

## Wiring both plugins

Add these artifacts to the app's build, with the OpenTelemetry BOM setting their versions.

- `stratum:stratum-otel-logs` and `stratum:stratum-otel-metrics`
- `io.opentelemetry:opentelemetry-sdk`
- `io.opentelemetry:opentelemetry-exporter-otlp`

This app sends logs and metrics over OTLP to a collector at the default endpoint, `localhost:4317`. An app that wants only logs or only metrics adds just that plugin and its provider. To also write text lines, add `new LogPlugin()` from `stratum.log` to the same plugin list.

### NotesStratum.java

The app builds the providers once, beside the engine that uses them.

```java
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import stratum.Stratum;
import stratum.otel.logs.OpenTelemetryLogsPlugin;
import stratum.otel.metrics.OpenTelemetryMetricsPlugin;

public final class NotesStratum {
  public static final SdkLoggerProvider LOGGERS = SdkLoggerProvider.builder()
      .addLogRecordProcessor(
          BatchLogRecordProcessor.builder(OtlpGrpcLogRecordExporter.getDefault()).build())
      .build();

  public static final SdkMeterProvider METERS = SdkMeterProvider.builder()
      .registerMetricReader(
          PeriodicMetricReader.builder(OtlpGrpcMetricExporter.getDefault()).build())
      .build();

  public static final Stratum STRATUM = new Stratum("Notes", "2.1", NotesCatalog.CATALOG,
      new OpenTelemetryLogsPlugin(LOGGERS),
      new OpenTelemetryMetricsPlugin(METERS));

  private NotesStratum() {}
}
```

### App code

```java
import static java.util.Map.entry;

NotesStratum.STRATUM.publish(NoteKey.SAVED, entry(NoteField.WORDS, "120"), entry(NoteField.TITLE, "Dentist at noon"));
NotesStratum.STRATUM.publish(NoteKey.OPENED, entry(NoteField.SOURCE, "search"));
NotesStratum.STRATUM.publish(NoteKey.SYNCED, entry(NoteField.MILLIS, "420"), entry(NoteField.KIND, "full"));

// When the app stops, after its last publish:
NotesStratum.LOGGERS.shutdown().join(10, TimeUnit.SECONDS);
NotesStratum.METERS.shutdown().join(10, TimeUnit.SECONDS);
```

Shutting each provider down flushes what its exporter still holds.
