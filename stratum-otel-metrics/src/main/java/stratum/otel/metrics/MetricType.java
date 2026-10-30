package stratum.otel.metrics;

import stratum.EventType;

/** The event type the OpenTelemetry metrics plugin accepts. */
public enum MetricType implements EventType {
  /** An event recorded as a metric, through the {@link MetricSettings} its catalog gives it. */
  METRIC
}
