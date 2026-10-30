package stratum;

/**
 * Settings a catalog gives the plugins that take one of an event's types. Stratum never reads them:
 * the plugin module that defines the settings class gives them their meaning.
 */
public interface EventSettings {

  /** The type whose plugins read these settings. */
  EventType type();
}
