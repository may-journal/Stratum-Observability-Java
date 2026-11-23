package stratum.jfr;

import java.util.Map;
import stratum.BaseType;
import stratum.Catalog;
import stratum.Event;
import stratum.Level;

/** The notes product's catalog as the Flight Recorder plugin sees it. */
final class FlightCatalog {

  static final Catalog CATALOG =
      new Catalog(
          "Notes",
          "1.0",
          Map.of(
              FlightKey.SAVED,
              Event.of(JfrType.JFR, "A person saved a note", 1)
                  .publicFields(FlightField.WORDS)
                  .privateFields(FlightField.TITLE),
              FlightKey.FAILED,
              Event.of(JfrType.JFR, "The notes failed to sync", 2).level(Level.ERROR),
              FlightKey.OPENED,
              Event.of(BaseType.BASE, "A person opened a note", 3)
                  .publicFields(FlightField.SOURCE)));

  private FlightCatalog() {}
}
