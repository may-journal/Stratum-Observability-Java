package stratum;

import java.util.function.Consumer;

/**
 * Hands one snapshot to one listener or plugin, so a failure there never stops the others or
 * reaches the app. It logs the failure as a warning and carries on.
 */
final class Delivery {

  /** The name of the logger that hears of a plugin or listener that failed. */
  static final String LOGGER = "stratum";

  private Delivery() {}

  /** Calls {@code listener} with the snapshot. */
  static void toListener(Consumer<Snapshot> listener, Snapshot snapshot) {
    guard("a listener", snapshot, () -> listener.accept(snapshot));
  }

  /** Gives the snapshot to {@code plugin} when it accepts the type and chooses to publish. */
  static void toPlugin(Plugin plugin, Snapshot snapshot) {
    guard(
        "plugin " + plugin.name(),
        snapshot,
        () -> {
          if (Event.reaches(snapshot.eventType(), plugin.acceptedTypes())
              && plugin.shouldPublish(snapshot)) {
            plugin.publish(snapshot);
          }
        });
  }

  /** Runs {@code call}; an {@link Error} still reaches the app, as no engine can carry on. */
  private static void guard(String who, Snapshot snapshot, Runnable call) {
    try {
      call.run();
    } catch (RuntimeException failure) {
      System.getLogger(LOGGER)
          .log(System.Logger.Level.WARNING, who + " failed on " + snapshot.key(), failure);
    }
  }
}
