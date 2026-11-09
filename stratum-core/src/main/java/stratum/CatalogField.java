package stratum;

/**
 * A field an event or the context can carry, which an app declares as an enum implementing this.
 * Each constant carries its field text, such as {@code "words"}, so renaming it never changes what
 * plugins report.
 */
public interface CatalogField {

  /** The field text plugins report, such as {@code "words"}. */
  String key();
}
