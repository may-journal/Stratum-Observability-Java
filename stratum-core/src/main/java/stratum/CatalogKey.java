package stratum;

/**
 * The key an app publishes an event under. An app declares its keys as an enum that implements
 * this, so app code names events by constant and an unknown key does not compile. Each constant
 * carries its key text, such as {@code "note.saved"}, so renaming the constant never changes what
 * plugins report.
 */
public interface CatalogKey {

  /** The key text plugins report, such as {@code "note.saved"}, unique across a service. */
  String key();
}
