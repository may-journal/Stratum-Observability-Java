package stratum;

/**
 * A field an event or the context can carry. An app declares its fields as an enum that implements
 * this, beside its keys, so a misspelled or undeclared field does not compile. Each constant
 * carries its field text, such as {@code "words"}, so renaming the constant never changes what
 * plugins report.
 */
public interface CatalogField {

  /** The field text plugins report, such as {@code "words"}. */
  String key();
}
