package stratum;

/** A destination for published events, such as an OpenTelemetry logger. */
public interface Plugin {

  /** Names the plugin. Adding a second plugin with the same name replaces the first. */
  String name();

  /** Sends the snapshot to the plugin's destination. */
  void publish(Snapshot snapshot);

  /** Whether this plugin publishes the snapshot. Every snapshot by default. */
  default boolean accepts(Snapshot snapshot) {
    return true;
  }
}
