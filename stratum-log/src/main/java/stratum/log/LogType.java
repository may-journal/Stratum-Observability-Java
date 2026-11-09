package stratum.log;

import stratum.EventType;

/**
 * The event type log plugins accept, which a catalog gives an event to send it as a log line.
 * Another module's type named {@code LOG} matches it.
 */
public enum LogType implements EventType {
  /** An event sent as a log line. */
  LOG
}
