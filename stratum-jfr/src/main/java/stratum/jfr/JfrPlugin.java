package stratum.jfr;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import stratum.EventType;
import stratum.Plugin;
import stratum.Snapshot;

/**
 * Commits each {@link JfrType#JFR} snapshot as one JDK Flight Recorder event named {@value #EVENT}.
 * The event sits on the timeline beside the JVM's own, and nothing is built while no recording
 * runs.
 */
public final class JfrPlugin implements Plugin {

  /** The name this plugin registers under. */
  public static final String NAME = "jfr";

  /** The name of the flight event, which a recording enables and a reader filters on. */
  public static final String EVENT = "stratum.Event";

  /** The value every private field carries in an event, in place of what a person wrote. */
  public static final String PRIVATE = "<private>";

  /** A plugin that commits to the JVM's own Flight Recorder. */
  public JfrPlugin() {}

  @Override
  public String name() {
    return NAME;
  }

  /** Takes only events with the {@link JfrType#JFR} type. */
  @Override
  public Set<EventType> acceptedTypes() {
    return Set.of(JfrType.JFR);
  }

  /** Says no while no recording has the event enabled, so an idle app builds no event. */
  @Override
  public boolean shouldPublish(Snapshot snapshot) {
    return jdk.jfr.EventType.getEventType(FlightEvent.class).isEnabled();
  }

  @Override
  public void publish(Snapshot snapshot) {
    new FlightEvent(snapshot, fields(snapshot)).commit();
  }

  /**
   * The event's fields as text: its context, public fields and redacted private fields as {@code
   * name=value} pairs in name order. A later group wins a name the groups share.
   */
  static String fields(Snapshot snapshot) {
    Map<String, String> pairs = new TreeMap<>(snapshot.context());
    pairs.putAll(snapshot.publicFields());
    snapshot.privateFields().keySet().forEach(name -> pairs.put(name, PRIVATE));
    return pairs.entrySet().stream()
        .map(pair -> pair.getKey() + "=" + pair.getValue())
        .collect(Collectors.joining(" "));
  }
}
