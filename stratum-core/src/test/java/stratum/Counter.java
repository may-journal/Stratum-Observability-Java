package stratum;

/**
 * A recorder that accepts only {@link CountType#COUNT} events, and refuses one whose {@link
 * CountSettings} are missing or name a field that is not public.
 */
final class Counter extends TypedRecorder {

  Counter() {
    super("counter", CountType.COUNT);
  }

  @Override
  public void check(CatalogKey key, Event event) {
    CountSettings settings =
        event
            .settings(CountSettings.class)
            .orElseThrow(() -> CatalogException.invalidEvent(key, "it has no count settings"));
    if (!event.publicFields().contains(settings.field())) {
      throw CatalogException.invalidEvent(
          key, "count field " + settings.field().key() + " is not public");
    }
  }
}
