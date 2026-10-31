package stratum.otel.metrics;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import stratum.CatalogException;
import stratum.CatalogField;
import stratum.CatalogKey;
import stratum.Event;
import stratum.EventType;
import stratum.Plugin;
import stratum.Snapshot;

/**
 * Records each snapshot whose event has the {@link MetricType#METRIC} type through the app's own
 * {@link MeterProvider}. The event's {@link MetricSettings} name the instrument, its name, unit and
 * description, the public field that holds the value, and the public fields that become attributes.
 * A counter with no value field adds one per event. A value that is missing or is not a number
 * records nothing. The plugin refuses a catalog whose metric event has no settings, or settings
 * that name a field the event does not hold as public, so every snapshot it takes carries valid
 * settings.
 *
 * <p>The plugin owns no reader or exporter. Where measurements go, and whether they leave the
 * process at all, is decided by the provider the app passes in.
 */
public final class OpenTelemetryMetricsPlugin implements Plugin {

  /** The name this plugin registers under. */
  public static final String NAME = "opentelemetry-metrics";

  /** How each instrument kind records one value. */
  private static final Map<MetricKind, Measure> MEASURES = measures();

  private final MeterProvider provider;

  /**
   * A plugin that records through {@code provider}, whose readers and exporters the app configures.
   *
   * @param provider the provider whose meters receive every measurement; never null
   */
  public OpenTelemetryMetricsPlugin(MeterProvider provider) {
    this.provider = Objects.requireNonNull(provider, "provider");
  }

  @Override
  public String name() {
    return NAME;
  }

  /** Takes only events with the {@link MetricType#METRIC} type. */
  @Override
  public Set<EventType> acceptedTypes() {
    return Set.of(MetricType.METRIC);
  }

  /** Refuses a metric event with no {@link MetricSettings}, or with settings it cannot record. */
  @Override
  public void check(CatalogKey key, Event event) {
    Optional<MetricSettings> settings = event.settings(MetricSettings.class);
    if (settings.isEmpty()) {
      throw CatalogException.invalidEvent(key, "it has the METRIC type but no metric settings");
    }
    String reason = settings.get().problem(event.publicFields());
    if (reason != null) {
      throw CatalogException.invalidEvent(key, reason);
    }
  }

  @Override
  public void publish(Snapshot snapshot) {
    MetricSettings metric = snapshot.settings(MetricSettings.class).orElseThrow();
    Meter meter =
        provider
            .meterBuilder(snapshot.product())
            .setInstrumentationVersion(snapshot.version())
            .build();
    try {
      double value = value(snapshot, metric);
      MEASURES.get(metric.kind()).take(meter, metric, value, attributes(snapshot, metric));
    } catch (NumberFormatException notNumber) {
      // A value that is missing or not a number records nothing, and publishing goes on.
    }
  }

  /** The value to record: one for a counter with no value field, else the field's number. */
  static double value(Snapshot snapshot, MetricSettings metric) {
    CatalogField field = metric.valueField();
    return field == null
        ? 1
        : Double.parseDouble(snapshot.publicFields().getOrDefault(field.key(), ""));
  }

  /** The measurement's attributes: the metric's attribute fields the app passed, and no others. */
  static Attributes attributes(Snapshot snapshot, MetricSettings metric) {
    AttributesBuilder builder = Attributes.builder();
    Set<String> keys =
        metric.attributeFields().stream().map(CatalogField::key).collect(Collectors.toSet());
    snapshot.publicFields().entrySet().stream()
        .filter(field -> keys.contains(field.getKey()))
        .forEach(field -> builder.put(field.getKey(), field.getValue()));
    return builder.build();
  }

  /** Records one value through the instrument a metric names. */
  @FunctionalInterface
  interface Measure {
    void take(Meter meter, MetricSettings metric, double value, Attributes attributes);
  }

  private static Map<MetricKind, Measure> measures() {
    Map<MetricKind, Measure> map = new EnumMap<>(MetricKind.class);
    map.put(
        MetricKind.COUNTER,
        (meter, metric, value, attributes) ->
            meter
                .counterBuilder(metric.name())
                .ofDoubles()
                .setUnit(metric.unit())
                .setDescription(metric.description())
                .build()
                .add(value, attributes));
    map.put(
        MetricKind.HISTOGRAM,
        (meter, metric, value, attributes) ->
            meter
                .histogramBuilder(metric.name())
                .setUnit(metric.unit())
                .setDescription(metric.description())
                .build()
                .record(value, attributes));
    map.put(
        MetricKind.GAUGE,
        (meter, metric, value, attributes) ->
            meter
                .gaugeBuilder(metric.name())
                .setUnit(metric.unit())
                .setDescription(metric.description())
                .build()
                .set(value, attributes));
    return Map.copyOf(map);
  }
}
