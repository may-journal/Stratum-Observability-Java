package stratum;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The notes product most requirements start from: a note a person saves, a failed sync, and events
 * with made-up types.
 */
public final class Notes {

  public static final String SAVED = "note.saved";
  static final String SHARED = "note.shared";

  /** A deleted note, which only an audit trail keeps. */
  static final String DELETED = "note.deleted";

  /** A finished sync, which an audit trail keeps and a tally counts by how long it took. */
  static final String SYNCED = "sync.finished";

  /** How many notes a person keeps, which only a tally counts. */
  static final String COUNTED = "notes.counted";

  /** The catalog of the notes product. */
  static final Catalog CATALOG =
      new Catalog(
          "Notes",
          "1.0",
          Map.of(
              SAVED,
              new Event(
                  "A person saved a note", "editor", Level.INFO, Set.of("words"), Set.of("title")),
              "sync.failed",
              new Event("The notes failed to sync", "sync", Level.ERROR),
              DELETED,
              new Event("A person deleted a note", "editor").types(AuditType.AUDIT),
              SYNCED,
              new Event(
                      "The notes finished syncing",
                      "sync",
                      Level.INFO,
                      Set.of("millis", "kind"),
                      Set.of())
                  .types(AuditType.AUDIT, CountType.COUNT)
                  .with(new CountSettings("millis")),
              COUNTED,
              new Event(
                      "The app counted the notes", "library", Level.INFO, Set.of("notes"), Set.of())
                  .types(CountType.COUNT)
                  .with(new CountSettings("notes"))));

  private Notes() {}

  /** A service for the notes product with {@code plugins}. */
  public static Stratum stratum(Plugin... plugins) throws CatalogException {
    return new Stratum("Notes", "2.1", List.of(CATALOG), List.of(plugins));
  }

  /** A catalog named {@code name} holding one event, {@code key}, in {@code category}. */
  static Catalog catalog(String name, String key, String category) {
    return new Catalog(name, "1.0", Map.of(key, new Event("A person shared a note", category)));
  }

  /** A catalog named Sharing that holds {@code event} under {@link #SHARED}. */
  static Catalog sharing(Event event) {
    return new Catalog("Sharing", "1.0", Map.of(SHARED, event));
  }

  /**
   * The {@link #SHARED} event, with a public field {@code to} and a private field {@code message},
   * before it is given types or settings.
   */
  static Event shared() {
    return new Event(
        "A person shared a note", "share", Level.INFO, Set.of("to"), Set.of("message"));
  }
}
