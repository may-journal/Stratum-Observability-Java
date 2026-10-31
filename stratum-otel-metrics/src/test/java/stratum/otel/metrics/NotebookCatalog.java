package stratum.otel.metrics;

import java.util.Map;
import java.util.Set;
import stratum.Catalog;
import stratum.Event;

/** The notes product's catalog as the OpenTelemetry metrics plugin sees it. */
final class NotebookCatalog {

  static final Catalog CATALOG =
      new Catalog(
          "Notes",
          "1.0",
          Map.of(
              NotebookKey.SAVED,
              Event.of(StandInType.LOG, "A person saved a note", 1)
                  .publicFields(NotebookField.WORDS)
                  .privateFields(NotebookField.TITLE),
              NotebookKey.SYNCED,
              Event.of(MetricType.METRIC, "The notes finished syncing", 2)
                  .publicFields(NotebookField.MILLIS, NotebookField.KIND)
                  .settings(
                      new MetricSettings(
                          MetricKind.HISTOGRAM,
                          "notes.sync.duration",
                          "ms",
                          "How long a sync took",
                          NotebookField.MILLIS,
                          Set.of(NotebookField.KIND))),
              NotebookKey.OPENED,
              Event.of(MetricType.METRIC, "A person opened a note", 3)
                  .publicFields(NotebookField.SOURCE)
                  .privateFields(NotebookField.TITLE)
                  .settings(
                      MetricSettings.counter(
                          "notes.opened",
                          "{note}",
                          "Notes a person opened",
                          Set.of(NotebookField.SOURCE))),
              NotebookKey.TYPED,
              Event.of(MetricType.METRIC, "A person typed words", 4)
                  .publicFields(NotebookField.WORDS)
                  .settings(
                      new MetricSettings(
                          MetricKind.COUNTER,
                          "notes.words",
                          "{word}",
                          "Words a person typed",
                          NotebookField.WORDS,
                          Set.of())),
              NotebookKey.COUNTED,
              Event.of(MetricType.METRIC, "The app counted the notes", 5)
                  .publicFields(NotebookField.NOTES)
                  .settings(
                      new MetricSettings(
                          MetricKind.GAUGE,
                          "notes.count",
                          "{note}",
                          "Notes a person keeps",
                          NotebookField.NOTES,
                          Set.of()))));

  private NotebookCatalog() {}
}
