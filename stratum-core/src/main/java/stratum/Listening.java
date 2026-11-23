package stratum;

/**
 * A listener's place in an engine, which {@link Stratum#addListener} returns. Closing it removes
 * the listener, so a screen can listen in a try-with-resources block or close it when it goes.
 */
@FunctionalInterface
public interface Listening extends AutoCloseable {

  /** Removes the listener, so it hears no later event. */
  @Override
  void close();
}
