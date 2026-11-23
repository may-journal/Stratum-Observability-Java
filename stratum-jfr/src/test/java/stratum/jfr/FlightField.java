package stratum.jfr;

import stratum.CatalogField;

/** The fields of {@link FlightCatalog}'s events and context. */
enum FlightField implements CatalogField {
  WORDS("words"),
  TITLE("title"),
  SOURCE("source"),
  SCREEN("screen");

  private final String key;

  FlightField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
