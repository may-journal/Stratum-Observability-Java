package stratum;

import java.util.Map;

/** The notes product's catalog, which most requirements start from. */
public final class NotesCatalog {

  public static final Catalog CATALOG =
      new Catalog(
          "Notes",
          "1.0",
          Map.of(
              NoteKey.SAVED,
              Event.of(BaseType.BASE, "A person saved a note", 1)
                  .publicFields(NoteField.WORDS)
                  .privateFields(NoteField.TITLE),
              NoteKey.FAILED,
              Event.of(BaseType.BASE, "The notes failed to sync", 2).level(Level.ERROR),
              NoteKey.DELETED,
              Event.of(AuditType.AUDIT, "A person deleted a note", 3),
              NoteKey.SYNCED,
              Event.of(CountType.COUNT, "The notes finished syncing", 4)
                  .publicFields(NoteField.MILLIS, NoteField.KIND)
                  .settings(new CountSettings(NoteField.MILLIS)),
              NoteKey.COUNTED,
              Event.of(CountType.COUNT, "The app counted the notes", 5)
                  .publicFields(NoteField.NOTES)
                  .settings(new CountSettings(NoteField.NOTES))));

  private NotesCatalog() {}
}
