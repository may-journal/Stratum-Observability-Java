package stratum.log;

import java.lang.System.Logger;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import stratum.EventType;
import stratum.Level;
import stratum.Plugin;
import stratum.Snapshot;

/**
 * Writes each {@link LogType#LOG} snapshot as one line through {@link System.Logger}, named for the
 * product, at the event's level. The line is the key, then context and public fields as {@code
 * name=value} pairs, with private values shown as {@value #PRIVATE}.
 */
public final class LogPlugin implements Plugin {

  /** The name this plugin registers under. */
  public static final String NAME = "log";

  /** The value every private field carries in a line, in place of what a person wrote. */
  public static final String PRIVATE = "<private>";

  /** The logger level for each event level: notice reads as info, and a fault as an error. */
  private static final Map<Level, Logger.Level> LEVELS = levels();

  /** A plugin that writes through the loggers {@link System#getLogger(String)} returns. */
  public LogPlugin() {}

  @Override
  public String name() {
    return NAME;
  }

  /** Takes only events with the {@link LogType#LOG} type. */
  @Override
  public Set<EventType> acceptedTypes() {
    return Set.of(LogType.LOG);
  }

  @Override
  public void publish(Snapshot snapshot) {
    System.getLogger(snapshot.product()).log(level(snapshot.level()), line(snapshot));
  }

  /** The logger level a line has for {@code level}. */
  static Logger.Level level(Level level) {
    return LEVELS.get(level);
  }

  /**
   * The line for a snapshot: its key, then its context, public fields and redacted private fields
   * in name order. A later group wins a name the groups share.
   */
  static String line(Snapshot snapshot) {
    Map<String, String> fields = new TreeMap<>(snapshot.context());
    fields.putAll(snapshot.publicFields());
    snapshot.privateFields().keySet().forEach(name -> fields.put(name, PRIVATE));
    return Stream.concat(
            Stream.of(snapshot.key()),
            fields.entrySet().stream().map(field -> field.getKey() + "=" + field.getValue()))
        .collect(Collectors.joining(" "));
  }

  private static Map<Level, Logger.Level> levels() {
    Map<Level, Logger.Level> map = new EnumMap<>(Level.class);
    map.put(Level.DEBUG, Logger.Level.DEBUG);
    map.put(Level.INFO, Logger.Level.INFO);
    map.put(Level.NOTICE, Logger.Level.INFO);
    map.put(Level.WARNING, Logger.Level.WARNING);
    map.put(Level.ERROR, Logger.Level.ERROR);
    map.put(Level.FAULT, Logger.Level.ERROR);
    return Map.copyOf(map);
  }
}
