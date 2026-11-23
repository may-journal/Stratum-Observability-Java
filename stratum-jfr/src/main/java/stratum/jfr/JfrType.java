package stratum.jfr;

import stratum.EventType;

/**
 * The event type the Flight Recorder plugin accepts, which a catalog gives an event to record it.
 * Another module's type named {@code JFR} matches it.
 */
public enum JfrType implements EventType {
  /** An event committed to JDK Flight Recorder. */
  JFR
}
