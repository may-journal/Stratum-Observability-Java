package stratum;

import java.time.Instant;
import java.util.Map;

/**
 * One published event, frozen at the moment the app published it. Plugins and listeners receive the
 * same snapshot, so a later change to the context never reaches it.
 *
 * @param key the key the app published
 * @param meaning the event's meaning, from its catalog
 * @param category the event's category, from its catalog
 * @param level the event's level, from its catalog
 * @param publicFields the fields the event names as public, with the values the app passed
 * @param privateFields the fields the event names as private, with the values the app passed
 * @param context the context the app had set when it published
 * @param time the moment the app published
 * @param product the product the service was started for
 * @param version the product's version
 */
public record Snapshot(
    String key,
    String meaning,
    String category,
    Level level,
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
}
