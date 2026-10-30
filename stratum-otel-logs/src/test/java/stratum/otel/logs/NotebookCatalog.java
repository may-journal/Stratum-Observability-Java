package stratum.otel.logs;

import java.util.Map;
import stratum.Catalog;
import stratum.Event;
import stratum.Level;

/** The notes product's catalog as the OpenTelemetry logs plugin sees it. */
final class NotebookCatalog {

  static final Catalog CATALOG =
      new Catalog(
          "Notes",
          "1.0",
          Map.of(
              NotebookKey.SAVED,
              Event.of(OpenTelemetryLogType.LOG, "A person saved a note", 1)
                  .publicFields(NotebookField.WORDS)
                  .privateFields(NotebookField.TITLE),
              NotebookKey.FAILED,
              Event.of(OpenTelemetryLogType.LOG, "The notes failed to sync", 2).level(Level.ERROR),
              NotebookKey.OPENED,
              Event.of(StandInType.METRIC, "A person opened a note", 3)
                  .publicFields(NotebookField.SOURCE)));

  private NotebookCatalog() {}
}
