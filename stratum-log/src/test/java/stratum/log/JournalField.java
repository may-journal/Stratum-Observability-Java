package stratum.log;

import stratum.CatalogField;

/** The fields of {@link JournalCatalog}'s events and context. */
enum JournalField implements CatalogField {
  WORDS("words"),
  TITLE("title"),
  SOURCE("source"),
  SCREEN("screen");

  private final String key;

  JournalField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
