package stratum;

import java.util.Map;

/**
 * A named, versioned set of events, keyed by the name the app publishes each one under.
 *
 * @param name the catalog's name, unique in one service
 * @param version the catalog's version, as the app numbers it
 * @param events each event by the key the app publishes it under
 */
public record Catalog(String name, String version, Map<String, Event> events) {

  /** Copies the events, so a later change to the caller's map never reaches the catalog. */
  public Catalog {
    events = Map.copyOf(events);
  }
}
