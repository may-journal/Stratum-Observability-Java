package stratum;

import java.util.Set;

/** A destination for published events, such as a crash reporter or an analytics backend. */
public interface Plugin {

  /** Names the plugin. Adding a second plugin with the same name replaces the first. */
  String name();

  /** Sends the snapshot to the plugin's destination. */
  void publish(Snapshot snapshot);

  /**
   * The event types this plugin takes, matched by name. An empty set, the default, accepts every
   * event; otherwise the plugin takes only events that have one of these types.
   */
  default Set<EventType> acceptedTypes() {
    return Set.of();
  }

  /**
   * Whether this plugin publishes the snapshot. Stratum asks before it calls {@link #publish}. By
   * default, every snapshot when {@link #acceptedTypes} is empty, else those that share a type with
   * it. Override it to filter on what the event carries.
   */
  default boolean shouldPublish(Snapshot snapshot) {
    return Event.reaches(acceptedTypes(), snapshot.eventTypes());
  }

  /**
   * Checks an event whose types this plugin accepts when its catalog or this plugin is added, so a
   * bad setting fails then rather than when the event is published. Accepts every event by default.
   *
   * @param key the key the event is published under
   * @param event the event, whose {@link Event#settings(Class)} holds its settings
   * @throws CatalogException when this plugin cannot send the event, from {@link
   *     CatalogException#invalidEvent}
   */
  default void check(String key, Event event) throws CatalogException {}
}
