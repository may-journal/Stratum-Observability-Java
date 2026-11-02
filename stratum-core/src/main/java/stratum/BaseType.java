package stratum;

/** The built-in event type, for events that need no plugin-specific attributes. */
public enum BaseType implements EventType {
  /** The base type, which a plugin module's own type named {@code BASE} also matches. */
  BASE
}
