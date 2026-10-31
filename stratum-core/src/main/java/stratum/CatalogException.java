package stratum;

/**
 * Why Stratum refused a catalog: an event that cannot be published, or a key or id another event
 * holds. It is unchecked, so an app can build its engine as a static constant and fail at start.
 */
public final class CatalogException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String key;

  private CatalogException(String key, String message) {
    super(message);
    this.key = key;
  }

  /**
   * The event under {@code key} cannot be published, for {@code reason}. A plugin throws this from
   * {@link Plugin#check} for an event it cannot send.
   */
  public static CatalogException invalidEvent(CatalogKey key, String reason) {
    return new CatalogException(key.key(), "event " + key.key() + " is invalid: " + reason);
  }

  /** Another catalog already holds an event under this key. */
  static CatalogException duplicateKey(CatalogKey key, String catalog) {
    return new CatalogException(key.key(), "event " + key.key() + " is already in " + catalog);
  }

  /** Another event already has this id. */
  static CatalogException duplicateId(CatalogKey key, int id, String other) {
    return new CatalogException(
        key.key(), "event " + key.key() + " has id " + id + ", which " + other + " has");
  }

  /** The key of the event the catalog was refused for. */
  public String key() {
    return key;
  }
}
