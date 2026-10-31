package stratum.otel.metrics;

import stratum.CatalogField;

/** The fields of {@link MetricSettingsTests}. */
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
