package stratum.otel.metrics;

import stratum.CatalogKey;

/** The keys of {@link NotebookCatalog}. */
enum NotebookKey implements CatalogKey {
  SAVED("note.saved"),
  SYNCED("sync.finished"),
  OPENED("note.opened"),
  TYPED("words.typed"),
  COUNTED("notes.counted");

  private final String key;

  NotebookKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
