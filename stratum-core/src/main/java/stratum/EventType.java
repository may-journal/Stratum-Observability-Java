package stratum;

/**
 * A type an event can have, which decides the plugins it reaches. Stratum defines no types: each
 * plugin module defines its own, usually as an enum. Two types with the same name are the same
 * type, so modules that take the same kind of event need not depend on each other.
 */
public interface EventType {

  /** The type's name, such as an enum constant's name. */
  String name();
}
