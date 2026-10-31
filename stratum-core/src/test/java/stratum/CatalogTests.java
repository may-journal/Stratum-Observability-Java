package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Requirement 0001: publishing what a catalog names. */
class CatalogTests {

  @Test
  void test0001_1() {
    Recorder recorder = new Recorder();
    assertTrue(NotesEngine.start(recorder).publish(NoteKey.SAVED));
    assertEquals(List.of("note.saved"), recorder.keys());
  }

  @Test
  void test0001_2() {
    Recorder recorder = new Recorder();
    assertFalse(NotesEngine.start(recorder).publish(ShareKey.SHARED));
    assertEquals(List.of(), recorder.keys());
  }

  @Test
  void test0001_3() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(recorder);
    stratum.add(SharingCatalog.CATALOG);
    stratum.publish(ShareKey.SHARED);
    assertEquals(
        List.of("A person shared a note"),
        recorder.snapshots().stream().map(Snapshot::description).toList());
  }

  @Test
  void test0001_4() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(recorder);
    stratum.removeCatalog("Notes");
    stratum.publish(NoteKey.SAVED);
    assertEquals(List.of(), recorder.keys());
  }

  @Test
  void test0001_5() {
    Stratum stratum = NotesEngine.start();
    Catalog drafts =
        new Catalog(
            "Drafts", "1.0", Map.of(ShareKey.DRAFT_SAVED, Event.of(BaseType.BASE, "Saved", 20)));
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(drafts));
    assertEquals("event note.saved is already in Notes", refused.getMessage());
  }

  @Test
  void test0001_6() {
    Stratum stratum = NotesEngine.start();
    Catalog sharing =
        new Catalog(
            "Sharing", "1.0", Map.of(ShareKey.SHARED, Event.of(BaseType.BASE, "Shared", 1)));
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(sharing));
    assertEquals("note.shared", refused.key());
    assertEquals("event note.shared has id 1, which note.saved has", refused.getMessage());
  }

  @Test
  void test0001_7() {
    Stratum stratum = NotesEngine.start();
    Event both =
        Event.of(BaseType.BASE, "Shared", 10)
            .publicFields(ShareField.TO)
            .privateFields(ShareField.TO);
    Catalog sharing = new Catalog("Sharing", "1.0", Map.of(ShareKey.SHARED, both));
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(sharing));
    assertEquals(
        "event note.shared is invalid: field to is both public and private", refused.getMessage());
  }

  @Test
  void test0001_8(@TempDir Path out) throws URISyntaxException {
    assertEquals(List.of(), AppBuild.errors(out, "stratum.publish(NoteKey.SAVED);"));
    assertFalse(AppBuild.errors(out, "stratum.publish(\"note.saved\");").isEmpty());
    String field = "stratum.publish(NoteKey.SAVED, java.util.Map.entry(\"words\", \"120\"));";
    assertFalse(AppBuild.errors(out, field).isEmpty());
  }
}
