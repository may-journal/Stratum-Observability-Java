package stratum.log;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import stratum.Stratum;

/** Requirement 0007: writing text log lines. */
class LogTests {

  /**
   * The lines java.util.logging, the JDK's default backend for {@link System.Logger}, receives
   * under one logger name.
   */
  static final class Lines extends Handler {

    private final List<LogRecord> records = new ArrayList<>();

    @Override
    public synchronized void publish(LogRecord record) {
      records.add(record);
    }

    @Override
    public void flush() {}

    @Override
    public void close() {}

    synchronized List<LogRecord> records() {
      return List.copyOf(records);
    }
  }

  /** An engine for the notes product, version 2.1, with the log plugin. */
  static Stratum start() {
    return new Stratum("Notes", "2.1", JournalCatalog.CATALOG, new LogPlugin());
  }

  /**
   * A person acts in the notes app, whose code {@code publishes} through the log plugin; the lines
   * the logger named {@code category} receives while it does.
   */
  static List<LogRecord> lines(String category, Consumer<Stratum> publishes) {
    return lines(start(), category, publishes);
  }

  /**
   * The lines the logger named {@code category} receives while {@code stratum} runs {@code
   * publishes}, from the Today screen.
   */
  static List<LogRecord> lines(Stratum stratum, String category, Consumer<Stratum> publishes) {
    stratum.setContext(entry(JournalField.SCREEN, "Today"));
    Logger logger = Logger.getLogger(category);
    Lines lines = new Lines();
    logger.setLevel(Level.ALL);
    logger.addHandler(lines);
    try {
      publishes.accept(stratum);
    } finally {
      logger.removeHandler(lines);
    }
    return lines.records();
  }

  /** The one line the logger named {@code category} receives. */
  static LogRecord line(String category, Consumer<Stratum> publishes) {
    List<LogRecord> records = lines(category, publishes);
    assertEquals(1, records.size());
    return records.get(0);
  }

  @Test
  void test0007_1() {
    LogRecord record = line("Notes", stratum -> stratum.publish(JournalKey.SAVED));
    assertEquals("Notes", record.getLoggerName());
    assertTrue(record.getMessage().startsWith("note.saved "));
    assertEquals(Level.INFO, record.getLevel());
  }

  @Test
  void test0007_2() {
    LogRecord record = line("Notes", stratum -> stratum.publish(JournalKey.FAILED));
    assertEquals(Level.SEVERE, record.getLevel());
  }

  @Test
  void test0007_3() {
    LogRecord record =
        line(
            "Notes",
            stratum -> stratum.publish(JournalKey.SAVED, entry(JournalField.WORDS, "120")));
    assertTrue(record.getMessage().contains(" words=120"));
  }

  @Test
  void test0007_4() {
    LogRecord record =
        line(
            "Notes",
            stratum ->
                stratum.publish(JournalKey.SAVED, entry(JournalField.TITLE, "Dentist at noon")));
    assertEquals("note.saved screen=Today title=" + LogPlugin.PRIVATE, record.getMessage());
  }

  @Test
  void test0007_5() {
    LogRecord record =
        line(
            "Notes",
            stratum -> stratum.publish(JournalKey.SAVED, entry(JournalField.WORDS, "120")));
    assertEquals("note.saved screen=Today words=120", record.getMessage());
  }

  @Test
  void test0007_6() {
    List<LogRecord> records =
        lines(
            "Notes",
            stratum -> stratum.publish(JournalKey.OPENED, entry(JournalField.SOURCE, "search")));
    assertEquals(List.of(), records);
  }

  @Test
  void test0007_7() {
    Stratum stratum = start();
    stratum.add(new LogPlugin());
    assertEquals(1, lines(stratum, "Notes", each -> each.publish(JournalKey.SAVED)).size());
  }
}
