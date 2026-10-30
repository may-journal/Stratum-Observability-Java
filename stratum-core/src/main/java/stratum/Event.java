package stratum;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * One catalog item: the event an app publishes under one key. A catalog declares each event in one
 * expression, such as {@code Event.of(BaseType.BASE, "A person saved a note",
 * 2).publicFields(NoteField.WORDS)}. Each method returns a new event, so it never changes.
 *
 * @param eventType the type that decides the plugins the event reaches
 * @param description what happened, in words a teammate reading the event understands
 * @param id the reference id that queries find the event by, unique in one service
 * @param level how serious the event is
 * @param publicFields fields whose values may be shown anywhere, such as a count
 * @param privateFields fields whose values a plugin must keep private, such as a title
 * @param settings the plugin-owned attributes for the event's type, or null when it has none
 */
public record Event(
    EventType eventType,
    String description,
    int id,
    Level level,
    Set<CatalogField> publicFields,
    Set<CatalogField> privateFields,
    EventSettings settings) {

  /** Copies the sets, and refuses a null type, description or level. */
  public Event {
    Objects.requireNonNull(eventType, "eventType");
    Objects.requireNonNull(description, "description");
    Objects.requireNonNull(level, "level");
    publicFields = Set.copyOf(publicFields);
    privateFields = Set.copyOf(privateFields);
  }

  /** An {@link Level#INFO} event of {@code eventType} with no fields or settings. */
  public static Event of(EventType eventType, String description, int id) {
    return new Event(eventType, description, id, Level.INFO, Set.of(), Set.of(), null);
  }

  /** This event at {@code level}. */
  public Event level(Level level) {
    return new Event(eventType, description, id, level, publicFields, privateFields, settings);
  }

  /** This event with exactly these public fields. */
  public Event publicFields(CatalogField... names) {
    return new Event(eventType, description, id, level, Set.of(names), privateFields, settings);
  }

  /** This event with exactly these private fields. */
  public Event privateFields(CatalogField... names) {
    return new Event(eventType, description, id, level, publicFields, Set.of(names), settings);
  }

  /** This event with {@code settings}, which the plugin module that defines them reads. */
  public Event settings(EventSettings settings) {
    return new Event(eventType, description, id, level, publicFields, privateFields, settings);
  }

  /** The event's settings of class {@code kind}, or empty when it has none. */
  public <T extends EventSettings> Optional<T> settings(Class<T> kind) {
    return Optional.ofNullable(settings).filter(kind::isInstance).map(kind::cast);
  }

  /** Why the event cannot be published, or null when it can. */
  String problem() {
    String shared =
        publicFields.stream()
            .filter(field -> keys(privateFields).contains(field.key()))
            .map(CatalogField::key)
            .sorted()
            .findFirst()
            .map(field -> "field " + field + " is both public and private")
            .orElse(null);
    if (shared != null || settings == null) {
      return shared;
    }
    String type = settings.type().name();
    return type.equals(eventType.name())
        ? null
        : "it has settings for type " + type + " but is of type " + eventType.name();
  }

  /** The values in {@code given} for one of {@code fields}, by field key. */
  static Map<String, String> only(Map<CatalogField, String> given, Set<CatalogField> fields) {
    Map<String, String> kept = new HashMap<>();
    given.forEach(
        (field, value) -> {
          if (fields.contains(field)) {
            kept.put(field.key(), value);
          }
        });
    return kept;
  }

  private static Set<String> keys(Set<CatalogField> fields) {
    return fields.stream().map(CatalogField::key).collect(Collectors.toSet());
  }

  /** Whether an event of {@code type} reaches a plugin accepting {@code accepted}, by name. */
  static boolean reaches(EventType type, Set<EventType> accepted) {
    return accepted.isEmpty()
        || accepted.stream().anyMatch(each -> each.name().equals(type.name()));
  }
}
