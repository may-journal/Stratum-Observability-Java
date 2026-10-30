package stratum.otel.metrics;

/** The kind of OpenTelemetry instrument a metric records through. */
public enum MetricKind {
  /** Adds each value, or one when the metric names no value field, to a running sum. */
  COUNTER,
  /** Records each value into a distribution, such as how long a sync took. */
  HISTOGRAM,
  /** Keeps the latest value, such as how many notes a person has. */
  GAUGE
}
