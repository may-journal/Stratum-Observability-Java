package stratum.otel.metrics;

import stratum.CatalogField;

/** The fields of {@link NotebookCatalog}. */
enum NotebookField implements CatalogField {
  WORDS("words"),
  TITLE("title"),
  MILLIS("millis"),
  KIND("kind"),
  SOURCE("source"),
  NOTES("notes");

  private final String key;

  NotebookField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
