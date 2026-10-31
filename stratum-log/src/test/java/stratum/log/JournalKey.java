package stratum.log;

import stratum.CatalogKey;

/** The keys of {@link JournalCatalog}. */
enum JournalKey implements CatalogKey {
  SAVED("note.saved"),
  FAILED("sync.failed"),
  OPENED("note.opened");

  private final String key;

  JournalKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
