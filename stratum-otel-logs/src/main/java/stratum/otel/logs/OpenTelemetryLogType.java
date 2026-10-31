package stratum.otel.logs;

import stratum.EventType;

/**
 * The event type the OpenTelemetry logs plugin accepts. Another module's type named {@code LOG},
 * such as the text log plugin's, matches it, so an app lists the log type once and reaches both.
 */
public enum OpenTelemetryLogType implements EventType {
  /** An event emitted as a log record. */
  LOG
}
