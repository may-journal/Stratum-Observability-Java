package stratum.otel.metrics;

import java.util.Set;
import stratum.CatalogField;
import stratum.EventSettings;
import stratum.EventType;

/**
 * The metric a catalog entry of the {@link MetricType#METRIC} type records, attached with {@code
 * Event.settings}. Each field it names must be public, and a null value field makes a counter that
 * adds one per event.
 */
public record MetricSettings(
    MetricKind kind,
    String name,
    String unit,
    String description,
    CatalogField valueField,
    Set<CatalogField> attributeFields)
    implements EventSettings {

  /**
   * Copies the attribute fields, so a later change to the caller's set never reaches the settings.
   */
  public MetricSettings {
    attributeFields = Set.copyOf(attributeFields);
  }

  /** A counter that adds one each time the event is published. */
  public static MetricSettings counter(
      String name, String unit, String description, Set<CatalogField> attributeFields) {
    return new MetricSettings(MetricKind.COUNTER, name, unit, description, null, attributeFields);
  }

  @Override
  public EventType type() {
    return MetricType.METRIC;
  }

  /** Why the metric cannot be recorded from an event with {@code publicFields}, or null. */
  String problem(Set<CatalogField> publicFields) {
    if (valueField == null) {
      return kind == MetricKind.COUNTER
          ? notPublic(publicFields)
          : "metric " + name + " needs a value field";
    }
    if (!publicFields.contains(valueField)) {
      return "metric value field " + valueField.key() + " is not a public field";
    }
    return notPublic(publicFields);
  }

  private String notPublic(Set<CatalogField> publicFields) {
    return attributeFields.stream()
        .filter(field -> !publicFields.contains(field))
        .map(CatalogField::key)
        .sorted()
        .findFirst()
        .map(field -> "metric attribute " + field + " is not a public field")
        .orElse(null);
  }
}
