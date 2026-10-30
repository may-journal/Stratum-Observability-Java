package stratum.otel.metrics;

import stratum.CatalogKey;

/** The keys of {@link MetricSettingsTests}. */
enum ShareKey implements CatalogKey {
  SHARED("note.shared");

  private final String key;

  ShareKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
