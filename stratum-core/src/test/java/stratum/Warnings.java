package stratum;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** The records java.util.logging, the JDK's default backend, receives from the engine's logger. */
final class Warnings extends Handler {

  private final List<LogRecord> records = new ArrayList<>();

  private final boolean broken;

  private Warnings(boolean broken) {
    this.broken = broken;
  }

  /** The records the engine's logger receives while {@code publishes} runs. */
  static List<LogRecord> during(Runnable publishes) {
    return new Warnings(false).hear(publishes);
  }

  /** Runs {@code publishes} while the engine's logger fails on every record. */
  static void failingDuring(Runnable publishes) {
    new Warnings(true).hear(publishes);
  }

  private List<LogRecord> hear(Runnable publishes) {
    Logger logger = Logger.getLogger(Delivery.LOGGER);
    logger.addHandler(this);
    try {
      publishes.run();
    } finally {
      logger.removeHandler(this);
    }
    return List.copyOf(records);
  }

  @Override
  public synchronized void publish(LogRecord record) {
    if (broken) {
      throw new IllegalStateException("the log disk is full");
    }
    records.add(record);
  }

  @Override
  public void flush() {}

  @Override
  public void close() {}
}
