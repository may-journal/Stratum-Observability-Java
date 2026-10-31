package stratum;

/** The keys of the notes product's catalog, {@link NotesCatalog}. */
public enum NoteKey implements CatalogKey {
  SAVED("note.saved"),
  FAILED("sync.failed"),
  DELETED("note.deleted"),
  SYNCED("sync.finished"),
  COUNTED("notes.counted");

  private final String key;

  NoteKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
