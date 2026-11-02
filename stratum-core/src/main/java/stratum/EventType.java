package stratum;

/**
 * A type an event can have, which decides the plugins it reaches. Two types with the same name are
 * the same type, so modules need not depend on each other.
 */
public interface EventType {

  /** The type's name, such as an enum constant's name. */
  String name();
}
