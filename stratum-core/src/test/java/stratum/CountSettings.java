package stratum;

/**
 * Made-up settings for the {@link CountType#COUNT} type: the public field whose value a tally adds.
 *
 * @param field the field the tally adds
 */
record CountSettings(String field) implements EventSettings {

  @Override
  public EventType type() {
    return CountType.COUNT;
  }
}
