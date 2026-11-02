package stratum;

import java.util.Set;

/** A destination for published events, such as a crash reporter or an analytics backend. */
public interface Plugin {

  /** Names the plugin, so adding a second plugin with the same name replaces the first. */
  String name();

  /** Sends the snapshot to the plugin's destination. */
  void publish(Snapshot snapshot);

  /**
   * The event types this plugin takes, matched by name. An empty set, the default, accepts every
   * event; otherwise the plugin takes only events of one of these types.
   */
  default Set<EventType> acceptedTypes() {
    return Set.of();
  }

  /**
   * Whether this plugin publishes the snapshot, asked only for events of an accepted type. It says
   * yes by default; override it to filter on what the event carries or to skip when the destination
   * is down.
   */
  default boolean shouldPublish(Snapshot snapshot) {
    return true;
  }

  /**
   * Checks an event of an accepted type when its catalog or this plugin is added, so a bad setting
   * fails early.
   *
   * @param key the event's key @param event the event checked @throws CatalogException when the
   *     plugin cannot send it
   */
  default void check(CatalogKey key, Event event) {}
}
