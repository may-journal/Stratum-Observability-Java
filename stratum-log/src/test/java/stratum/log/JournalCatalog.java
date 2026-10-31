package stratum.log;

import java.util.Map;
import stratum.BaseType;
import stratum.Catalog;
import stratum.Event;
import stratum.Level;

/** The notes product's catalog as a log plugin sees it: two log events and one base event. */
final class JournalCatalog {

  static final Catalog CATALOG =
      new Catalog(
          "Notes",
          "1.0",
          Map.of(
              JournalKey.SAVED,
              Event.of(LogType.LOG, "A person saved a note", 1)
                  .publicFields(JournalField.WORDS)
                  .privateFields(JournalField.TITLE),
              JournalKey.FAILED,
              Event.of(LogType.LOG, "The notes failed to sync", 2).level(Level.ERROR),
              JournalKey.OPENED,
              Event.of(BaseType.BASE, "A person opened a note", 3)
                  .publicFields(JournalField.SOURCE)));

  private JournalCatalog() {}
}
