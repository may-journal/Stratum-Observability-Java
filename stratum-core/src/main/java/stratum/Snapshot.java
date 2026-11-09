package stratum;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * One published event, frozen when the app published it, so a later change to the context never
 * reaches it. It holds the catalog's values, the field values the app passed, and the context.
 */
public record Snapshot(
    String key,
    int id,
    String description,
    EventType eventType,
    Level level,
    EventSettings settings,
    Map<String, String> publicFields,
    Map<String, String> privateFields,
    Map<String, String> context,
    Instant time,
    String product,
    String version) {

  /** Copies the maps, so the snapshot stays frozen. */
  public Snapshot {
    publicFields = Map.copyOf(publicFields);
    privateFields = Map.copyOf(privateFields);
    context = Map.copyOf(context);
  }

  /** The snapshot's settings of class {@code kind}, or empty when it carries none. */
  public <T extends EventSettings> Optional<T> settings(Class<T> kind) {
    return Optional.ofNullable(settings).filter(kind::isInstance).map(kind::cast);
  }
}
