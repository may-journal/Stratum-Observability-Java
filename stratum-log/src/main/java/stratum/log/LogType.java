package stratum.log;

import stratum.EventType;

/**
 * The event type log plugins accept. A catalog gives an event {@link #LOG} to send it as a log
 * line. Another module's type named {@code LOG} matches it, so each log plugin can define its own.
 */
public enum LogType implements EventType {
  /** An event sent as a log line. */
  LOG
}
