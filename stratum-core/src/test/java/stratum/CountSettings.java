package stratum;

/**
 * Made-up settings for the {@link CountType#COUNT} type: the field whose value a tally adds.
 *
 * @param field the field the tally adds, which must be public
 */
record CountSettings(CatalogField field) implements EventSettings {

  @Override
  public EventType type() {
    return CountType.COUNT;
  }
}
