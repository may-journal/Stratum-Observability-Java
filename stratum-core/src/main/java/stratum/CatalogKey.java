package stratum;

/**
 * The key an app publishes an event under, which an app declares as an enum implementing this. Each
 * constant carries its key text, such as {@code "note.saved"}, so renaming it never changes what
 * plugins report.
 */
public interface CatalogKey {

  /** The key text plugins report, such as {@code "note.saved"}, unique across a service. */
  String key();
}
