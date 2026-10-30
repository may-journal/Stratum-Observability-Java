package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Requirement 0008: checking type settings. */
class SettingsTests {

  /** A Sharing catalog whose one event is {@code shared}. */
  static Catalog sharing(Event shared) {
    return new Catalog("Sharing", "1.0", Map.of(ShareKey.SHARED, shared));
  }

  /** A shared note of the {@link CountType#COUNT} type, with public {@code to}. */
  static Event counted() {
    return Event.of(CountType.COUNT, "A person shared a note", 10)
        .publicFields(ShareField.TO)
        .privateFields(ShareField.MESSAGE);
  }

  @Test
  void test0008_1() {
    Stratum stratum = NotesEngine.start();
    stratum.add(sharing(counted()));
    CatalogException refused =
        assertThrows(CatalogException.class, () -> stratum.add(new Counter()));
    assertEquals("event note.shared is invalid: it has no count settings", refused.getMessage());
  }

  @Test
  void test0008_2() {
    Stratum stratum = NotesEngine.start(new Counter());
    Catalog bad = sharing(counted().settings(new CountSettings(ShareField.MESSAGE)));
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(bad));
    assertEquals(
        "event note.shared is invalid: count field message is not public", refused.getMessage());
  }

  @Test
  void test0008_3() {
    Counter counter = new Counter();
    Stratum stratum = NotesEngine.start(counter);
    stratum.add(SharingCatalog.CATALOG);
    stratum.publish(ShareKey.SHARED);
    assertEquals(List.of(), counter.keys());
  }

  @Test
  void test0008_4() {
    Stratum stratum = NotesEngine.start();
    Catalog stray =
        sharing(
            SharingCatalog.CATALOG
                .items()
                .get(ShareKey.SHARED)
                .settings(new CountSettings(ShareField.TO)));
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(stray));
    assertEquals(
        "event note.shared is invalid: it has settings for type COUNT but is of type BASE",
        refused.getMessage());
  }
}
