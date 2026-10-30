package stratum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * One event a catalog describes: what it means, where it belongs, which fields it may carry, and
 * which types it has. A field the event does not name is dropped when the event is published.
 * Stratum never reads a type's settings; plugins give them their meaning.
 *
 * @param meaning what happened, in words a teammate reading the event understands
 * @param category the area of the app the event belongs to, such as a screen or a feature
 * @param level how serious the event is
 * @param publicFields fields whose values may be shown anywhere, such as a count or a duration
 * @param privateFields fields whose values a plugin must keep private, such as anything a person
 *     wrote
 * @param eventTypes the types the event has, which decide the plugins it reaches
 * @param settings settings for some of those types, at most one for each type
 */
public record Event(
    String meaning,
    String category,
    Level level,
    Set<String> publicFields,
    Set<String> privateFields,
    Set<EventType> eventTypes,
    List<EventSettings> settings) {

  private static final Pattern ONE_WORD = Pattern.compile("\\S+");

  /** Copies the sets and list, so a later change to the caller's values never reaches the event. */
  public Event {
    publicFields = Set.copyOf(publicFields);
    privateFields = Set.copyOf(privateFields);
    eventTypes = Set.copyOf(eventTypes);
    settings = List.copyOf(settings);
  }

  /** An event with these fields and no types. */
  public Event(
      String meaning,
      String category,
      Level level,
      Set<String> publicFields,
      Set<String> privateFields) {
    this(meaning, category, level, publicFields, privateFields, Set.of(), List.of());
  }

  /** An event at {@code level} with no fields. */
  public Event(String meaning, String category, Level level) {
    this(meaning, category, level, Set.of(), Set.of());
  }

  /** An {@link Level#INFO} event with no fields. */
  public Event(String meaning, String category) {
    this(meaning, category, Level.INFO);
  }

  /** This event with exactly {@code types}, keeping its settings. */
  public Event types(EventType... types) {
    return new Event(
        meaning, category, level, publicFields, privateFields, Set.of(types), settings);
  }

  /** This event with {@code more}, replacing any settings it had for the same type. */
  public Event with(EventSettings more) {
    List<EventSettings> kept = new ArrayList<>(settings);
    kept.removeIf(old -> old.type().name().equals(more.type().name()));
    kept.add(more);
    return new Event(meaning, category, level, publicFields, privateFields, eventTypes, kept);
  }

  /** The event's settings of class {@code kind}, or empty when it has none. */
  public <T extends EventSettings> Optional<T> settings(Class<T> kind) {
    return first(settings, kind);
  }

  /** Why the event cannot be published under {@code key}, or null when it can. */
  String problem(String key) {
    if (!ONE_WORD.matcher(key).matches()) {
      return "its key must be one word";
    }
    if (!ONE_WORD.matcher(category).matches()) {
      return "its category must be one word";
    }
    String shared =
        publicFields.stream()
            .filter(privateFields::contains)
            .sorted()
            .findFirst()
            .map(field -> "field " + field + " is both public and private")
            .orElse(null);
    return shared == null ? untyped() : shared;
  }

  /** Why the event has settings for a type it does not have, or null when it has none. */
  private String untyped() {
    Set<String> names = names(eventTypes);
    return settings.stream()
        .map(each -> each.type().name())
        .filter(name -> !names.contains(name))
        .sorted()
        .findFirst()
        .map(name -> "it has settings for type " + name + " but not that type")
        .orElse(null);
  }

  /**
   * Whether an event with {@code types} reaches a plugin accepting {@code accepted}: always when it
   * accepts every event, else when they share a type name.
   */
  static boolean reaches(Set<EventType> accepted, Set<EventType> types) {
    Set<String> wanted = names(accepted);
    return wanted.isEmpty() || types.stream().map(EventType::name).anyMatch(wanted::contains);
  }

  static Set<String> names(Set<EventType> types) {
    return types.stream().map(EventType::name).collect(Collectors.toSet());
  }

  static <T extends EventSettings> Optional<T> first(List<EventSettings> settings, Class<T> kind) {
    return settings.stream().filter(kind::isInstance).map(kind::cast).findFirst();
  }
}
