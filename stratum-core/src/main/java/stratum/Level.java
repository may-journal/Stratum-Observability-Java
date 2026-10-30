package stratum;

/** How serious an event is. Each plugin maps it onto its own scale. */
public enum Level {
  DEBUG,
  INFO,
  NOTICE,
  WARNING,
  ERROR,
  FAULT
}
