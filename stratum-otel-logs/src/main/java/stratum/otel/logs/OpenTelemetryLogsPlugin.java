package stratum.otel.logs;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.LoggerProvider;
import io.opentelemetry.api.logs.Severity;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import stratum.EventType;
import stratum.Level;
import stratum.Plugin;
import stratum.Snapshot;

/**
 * Emits each snapshot whose event has the {@link OpenTelemetryLogType#LOG} type as an OpenTelemetry
 * log record through the app's own {@link LoggerProvider}. The record's body is the event's
 * description, its severity follows the event's level, and its attributes carry the key, id,
 * product, version, context and public fields in the clear. Private fields appear by name only,
 * with their values replaced by {@value #PRIVATE}.
 *
 * <p>The plugin owns no exporter or endpoint. Where records go, and whether they leave the process
 * at all, is decided by the provider the app passes in.
 */
public final class OpenTelemetryLogsPlugin implements Plugin {

  /** The name this plugin registers under. */
  public static final String NAME = "opentelemetry-logs";

  /** The value every private field carries in a record, in place of what a person wrote. */
  public static final String PRIVATE = "<private>";

  /** The event's catalog key. */
  public static final AttributeKey<String> KEY = AttributeKey.stringKey("stratum.key");

  /** The event's reference id. */
  public static final AttributeKey<Long> ID = AttributeKey.longKey("stratum.id");

  /** The product the service was started for. */
  public static final AttributeKey<String> PRODUCT = AttributeKey.stringKey("stratum.product");

  /** The product's version. */
  public static final AttributeKey<String> VERSION = AttributeKey.stringKey("stratum.version");

  /**
   * The OpenTelemetry severity for each event level, following the specification's syslog mapping:
   * notice is one step above info, and a fault is fatal.
   */
  private static final Map<Level, Severity> SEVERITIES = severities();

  private final LoggerProvider provider;

  /**
   * A plugin that logs through {@code provider}, whose exporters the app configures.
   *
   * @param provider the provider whose loggers receive every record; never null
   */
  public OpenTelemetryLogsPlugin(LoggerProvider provider) {
    this.provider = Objects.requireNonNull(provider, "provider");
  }

  @Override
  public String name() {
    return NAME;
  }

  /** Takes only events with the {@link OpenTelemetryLogType#LOG} type. */
  @Override
  public Set<EventType> acceptedTypes() {
    return Set.of(OpenTelemetryLogType.LOG);
  }

  @Override
  public void publish(Snapshot snapshot) {
    Logger logger =
        provider
            .loggerBuilder(snapshot.product())
            .setInstrumentationVersion(snapshot.version())
            .build();
    logger
        .logRecordBuilder()
        .setTimestamp(snapshot.time())
        .setBody(snapshot.description())
        .setSeverity(severity(snapshot.level()))
        .setSeverityText(snapshot.level().name())
        .setAllAttributes(attributes(snapshot))
        .emit();
  }

  /** The severity a record carries for {@code level}. */
  static Severity severity(Level level) {
    return SEVERITIES.get(level);
  }

  /**
   * The record's attributes: the event's identity, then its context, then its public fields, then
   * its private fields redacted. A later group wins a name the groups share.
   */
  static Attributes attributes(Snapshot snapshot) {
    AttributesBuilder builder =
        Attributes.builder()
            .put(KEY, snapshot.key())
            .put(ID, (long) snapshot.id())
            .put(PRODUCT, snapshot.product())
            .put(VERSION, snapshot.version());
    snapshot.context().forEach(builder::put);
    snapshot.publicFields().forEach(builder::put);
    snapshot.privateFields().keySet().forEach(name -> builder.put(name, PRIVATE));
    return builder.build();
  }

  private static Map<Level, Severity> severities() {
    Map<Level, Severity> map = new EnumMap<>(Level.class);
    map.put(Level.DEBUG, Severity.DEBUG);
    map.put(Level.INFO, Severity.INFO);
    map.put(Level.NOTICE, Severity.INFO2);
    map.put(Level.WARNING, Severity.WARN);
    map.put(Level.ERROR, Severity.ERROR);
    map.put(Level.FAULT, Severity.FATAL);
    return Map.copyOf(map);
  }
}
