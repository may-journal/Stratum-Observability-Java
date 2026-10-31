package stratum.otel.logs;

import stratum.CatalogKey;

/** The keys of {@link NotebookCatalog}. */
enum NotebookKey implements CatalogKey {
  SAVED("note.saved"),
  FAILED("sync.failed"),
  OPENED("note.opened");

  private final String key;

  NotebookKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
