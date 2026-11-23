package stratum.jfr;

import stratum.CatalogKey;

/** The keys of {@link FlightCatalog}. */
enum FlightKey implements CatalogKey {
  SAVED("note.saved"),
  FAILED("sync.failed"),
  OPENED("note.opened");

  private final String key;

  FlightKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
