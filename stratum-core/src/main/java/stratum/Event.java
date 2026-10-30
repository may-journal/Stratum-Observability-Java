package stratum;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * One event a catalog describes: what it means, where it belongs, and which fields it may carry. A
 * field the event does not name is dropped when the event is published.
 *
 * @param meaning what happened, in words a teammate reading the log understands
 * @param category the area of the app the event belongs to, such as a logger name
 * @param level how serious the event is
 * @param publicFields fields whose values may be shown anywhere, such as a count or a duration
 * @param privateFields fields whose values a plugin must keep private, such as anything a person
 *     wrote
 */
public record Event(
    String meaning,
    String category,
    Level level,
    Set<String> publicFields,
    Set<String> privateFields) {

  private static final Pattern ONE_WORD = Pattern.compile("\\S+");

  /** Copies the field sets, so a later change to the caller's sets never reaches the event. */
  public Event {
    publicFields = Set.copyOf(publicFields);
    privateFields = Set.copyOf(privateFields);
  }

  /** An event at {@code level} with no fields. */
  public Event(String meaning, String category, Level level) {
    this(meaning, category, level, Set.of(), Set.of());
  }

  /** An {@link Level#INFO} event with no fields. */
  public Event(String meaning, String category) {
    this(meaning, category, Level.INFO);
  }

  /** Why the event cannot be published under {@code key}, or null when it can. */
  String problem(String key) {
    if (!ONE_WORD.matcher(key).matches()) {
      return "its key must be one word";
    }
    if (!ONE_WORD.matcher(category).matches()) {
      return "its category must be one word";
    }
    return publicFields.stream()
        .filter(privateFields::contains)
        .sorted()
        .findFirst()
        .map(field -> "field " + field + " is both public and private")
        .orElse(null);
  }
}
