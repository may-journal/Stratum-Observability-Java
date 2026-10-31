package stratum;

/** The keys of the sharing catalog, {@link SharingCatalog}, and of catalogs a test builds. */
enum ShareKey implements CatalogKey {
  SHARED("note.shared"),
  /** A draft saved under the same key text as {@link NoteKey#SAVED}, which Stratum refuses. */
  DRAFT_SAVED("note.saved");

  private final String key;

  ShareKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
