package stratum.otel.logs;

import stratum.CatalogField;

/** The fields of {@link NotebookCatalog}'s events. */
enum NotebookField implements CatalogField {
  WORDS("words"),
  TITLE("title"),
  SOURCE("source");

  private final String key;

  NotebookField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
