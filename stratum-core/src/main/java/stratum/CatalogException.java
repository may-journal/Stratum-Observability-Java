package stratum;

/** Why Stratum refused a catalog: an event that cannot be published, or a key another holds. */
public final class CatalogException extends Exception {

  private static final long serialVersionUID = 1L;

  private final String key;

  private CatalogException(String key, String message) {
    super(message);
    this.key = key;
  }

  /**
   * An event in the catalog cannot be published, for {@code reason}. A plugin throws this from
   * {@link Plugin#check} for an event it cannot send.
   */
  public static CatalogException invalidEvent(String key, String reason) {
    return new CatalogException(key, "event " + key + " is invalid: " + reason);
  }

  /** Another catalog already holds an event under this key. */
  static CatalogException duplicateKey(String key, String catalog) {
    return new CatalogException(key, "event " + key + " is already in catalog " + catalog);
  }

  /** The key of the event the catalog was refused for. */
  public String key() {
    return key;
  }
}
