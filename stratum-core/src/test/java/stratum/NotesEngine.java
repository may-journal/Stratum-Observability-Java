package stratum;

/** Starts a fresh engine for the notes product, as each test needs its own. */
public final class NotesEngine {

  private NotesEngine() {}

  /** An engine for the notes product, version 2.1, with {@code plugins}. */
  public static Stratum start(Plugin... plugins) {
    return new Stratum("Notes", "2.1", NotesCatalog.CATALOG, plugins);
  }
}
