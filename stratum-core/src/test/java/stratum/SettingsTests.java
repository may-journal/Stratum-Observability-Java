package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Requirement 0008: checking type settings. */
class SettingsTests {

  /** The message starting the notes service with a counter and the Sharing catalog. */
  static String refusal(Event shared) {
    List<Catalog> catalogs = List.of(Notes.CATALOG, Notes.sharing(shared));
    List<Plugin> plugins = List.of(new Counter());
    return assertThrows(
            CatalogException.class, () -> new Stratum("Notes", "2.1", catalogs, plugins))
        .getMessage();
  }

  @Test
  void test0008_1() {
    Event shared = Notes.shared().types(CountType.COUNT);
    assertEquals("event note.shared is invalid: it has no count settings", refusal(shared));
  }

  @Test
  void test0008_2() {
    Event shared = Notes.shared().types(CountType.COUNT).with(new CountSettings("message"));
    assertEquals(
        "event note.shared is invalid: count field message is not public", refusal(shared));
  }

  @Test
  void test0008_3() throws CatalogException {
    Stratum stratum = Notes.stratum();
    stratum.add(Notes.sharing(Notes.shared().types(CountType.COUNT)));
    CatalogException refused =
        assertThrows(CatalogException.class, () -> stratum.add(new Counter()));
    assertEquals(Notes.SHARED, refused.key());
  }

  @Test
  void test0008_4() throws CatalogException {
    Counter counter = new Counter();
    Stratum stratum = Notes.stratum(counter);
    stratum.add(Notes.sharing(Notes.shared().types(AuditType.AUDIT)));
    stratum.publish(Notes.SHARED);
    assertEquals(List.of(), counter.keys());
  }

  @Test
  void test0008_5() throws CatalogException {
    Stratum stratum = Notes.stratum();
    Catalog stray = Notes.sharing(Notes.shared().with(new CountSettings("to")));
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(stray));
    assertEquals(
        "event note.shared is invalid: it has settings for type COUNT but not that type",
        refused.getMessage());
  }

  @Test
  void test0008_6() throws CatalogException {
    Recorder recorder = new Recorder();
    Event twice =
        Notes.shared()
            .types(CountType.COUNT)
            .with(new CountSettings("message"))
            .with(new CountSettings("to"));
    Stratum stratum = Notes.stratum(recorder);
    stratum.add(Notes.sharing(twice));
    stratum.publish(Notes.SHARED);
    assertEquals(List.of(new CountSettings("to")), recorder.snapshots().get(0).settings());
  }
}
