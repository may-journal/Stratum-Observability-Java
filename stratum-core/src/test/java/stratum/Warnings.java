package stratum;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** The records java.util.logging, the JDK's default backend, receives from the engine's logger. */
final class Warnings extends Handler {

  private final List<LogRecord> records = new ArrayList<>();

  /** The records the engine's logger receives while {@code publishes} runs. */
  static List<LogRecord> during(Runnable publishes) {
    Logger logger = Logger.getLogger(Delivery.LOGGER);
    Warnings warnings = new Warnings();
    logger.addHandler(warnings);
    try {
      publishes.run();
    } finally {
      logger.removeHandler(warnings);
    }
    return List.copyOf(warnings.records);
  }

  @Override
  public synchronized void publish(LogRecord record) {
    records.add(record);
  }

  @Override
  public void flush() {}

  @Override
  public void close() {}
}
