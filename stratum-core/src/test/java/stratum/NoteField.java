package stratum;

/** The fields of the notes product's events and context, beside {@link NoteKey}. */
public enum NoteField implements CatalogField {
  WORDS("words"),
  TITLE("title"),
  BODY("body"),
  MILLIS("millis"),
  KIND("kind"),
  NOTES("notes"),
  SCREEN("screen");

  private final String key;

  NoteField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
