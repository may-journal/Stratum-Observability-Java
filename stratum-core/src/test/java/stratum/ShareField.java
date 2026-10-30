package stratum;

/** The fields of the sharing catalog's events, beside {@link ShareKey}. */
enum ShareField implements CatalogField {
  TO("to"),
  MESSAGE("message");

  private final String key;

  ShareField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
