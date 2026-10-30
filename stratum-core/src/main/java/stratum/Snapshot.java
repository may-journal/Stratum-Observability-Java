package stratum;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * One published event, frozen at the moment the app published it, so a later change to the context
 * never reaches it.
 *
 * @param key the published key's text, such as {@code "note.saved"}
 * @param id the event's reference id, from its catalog
 * @param description the event's description, from its catalog
 * @param eventType the event's type, from its catalog
 * @param level the event's level, from its catalog
 * @param settings the event's plugin-owned settings, from its catalog, or null
 * @param publicFields the values the app passed for the event's public fields, by field key
 * @param privateFields the values the app passed for the event's private fields, by field key
 * @param context the context the app had set when it published
 * @param time the moment the app published
 * @param product the product the service was started for
 * @param version the product's version
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
