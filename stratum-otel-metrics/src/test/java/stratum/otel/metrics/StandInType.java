package stratum.otel.metrics;

import stratum.EventType;

/** A stand-in for a log module's type, which this module does not depend on. */
enum StandInType implements EventType {
  LOG
}
