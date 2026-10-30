package stratum;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * One published event, frozen at the moment the app published it, so a later change to the context
 * never reaches it. A plugin with accepted types receives only those of the event's types and their
 * settings; a plugin that accepts every event, and every listener, receives them all.
 *
 * @param key the key the app published
 * @param meaning the event's meaning, from its catalog
 * @param category the event's category, from its catalog
 * @param level the event's level, from its catalog
 * @param eventTypes the event's types, from its catalog
 * @param settings the settings for those types, from its catalog
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
    Set<EventType> eventTypes,
    List<EventSettings> settings,
    Map<String, String> publicFields,
    Map<String, String> privateFields,
    Map<String, String> context,
    Instant time,
    String product,
    String version) {

  /** Copies the sets, list and maps, so the snapshot stays frozen. */
  public Snapshot {
    eventTypes = Set.copyOf(eventTypes);
    settings = List.copyOf(settings);
    publicFields = Map.copyOf(publicFields);
    privateFields = Map.copyOf(privateFields);
    context = Map.copyOf(context);
  }

  /** The snapshot's settings of class {@code kind}, or empty when it carries none. */
  public <T extends EventSettings> Optional<T> settings(Class<T> kind) {
    return Event.first(settings, kind);
  }

  /** The copy a plugin accepting {@code accepted} receives: only those types and their settings. */
  Snapshot forPlugin(Set<EventType> accepted) {
    if (accepted.isEmpty()) {
      return this;
    }
    Set<String> wanted = Event.names(accepted);
    return new Snapshot(
        key,
        meaning,
        category,
        level,
        eventTypes.stream()
            .filter(type -> wanted.contains(type.name()))
            .collect(Collectors.toSet()),
        settings.stream().filter(each -> wanted.contains(each.type().name())).toList(),
        publicFields,
        privateFields,
        context,
        time,
        product,
        version);
  }
}
