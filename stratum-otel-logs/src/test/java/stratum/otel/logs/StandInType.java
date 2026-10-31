package stratum.otel.logs;

import stratum.EventType;

/** A stand-in for the metrics module's type, which this module does not depend on. */
enum StandInType implements EventType {
  METRIC
}
