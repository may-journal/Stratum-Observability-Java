package io.github.mayjournal.stratum;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** The notes product most requirements start from: a note a person saves, and a failed sync. */
public final class Notes {

  public static final String SAVED = "note.saved";
  static final String SHARED = "note.shared";

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
              new Event("The notes failed to sync", "sync", Level.ERROR)));

  private Notes() {}

  /** A service for the notes product with {@code plugins}. */
  public static Stratum stratum(Plugin... plugins) throws CatalogException {
    return new Stratum("Notes", "2.1", List.of(CATALOG), List.of(plugins));
  }

  /** A catalog named {@code name} holding one event, {@code key}, in {@code category}. */
  static Catalog catalog(String name, String key, String category) {
    return new Catalog(name, "1.0", Map.of(key, new Event("A person shared a note", category)));
  }
}
