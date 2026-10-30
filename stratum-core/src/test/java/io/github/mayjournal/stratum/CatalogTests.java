package io.github.mayjournal.stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Requirement 0001: publishing what a catalog names. */
class CatalogTests {

  @Test
  void test0001_1() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    stratum.publish(Notes.SAVED);
    assertEquals(List.of(Notes.SAVED), recorder.keys());
  }

  @Test
  void test0001_2() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    assertFalse(stratum.publish(Notes.SHARED));
    assertEquals(List.of(), recorder.keys());
  }

  @Test
  void test0001_3() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    stratum.add(Notes.catalog("Sharing", Notes.SHARED, "share"));
    stratum.publish(Notes.SHARED);
    assertEquals(
        List.of("A person shared a note"),
        recorder.snapshots().stream().map(Snapshot::meaning).toList());
  }

  @Test
  void test0001_4() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    stratum.removeCatalog("Notes");
    stratum.publish(Notes.SAVED);
    assertEquals(List.of(), recorder.keys());
  }

  @Test
  void test0001_5() throws CatalogException {
    Stratum stratum = Notes.stratum();
    Catalog spaced = Notes.catalog("Sharing", "note shared", "share");
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(spaced));
    assertEquals("event note shared is invalid: its key must be one word", refused.getMessage());
  }

  @Test
  void test0001_6() throws CatalogException {
    Stratum stratum = Notes.stratum();
    Catalog clash = Notes.catalog("Drafts", Notes.SAVED, "drafts");
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(clash));
    assertEquals("event note.saved is already in catalog Notes", refused.getMessage());
  }

  @Test
  void test0001_7() throws CatalogException {
    Stratum stratum = Notes.stratum();
    Catalog spaced = Notes.catalog("Sharing", Notes.SHARED, "share sheet");
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(spaced));
    assertEquals(Notes.SHARED, refused.key());
    assertEquals(
        "event note.shared is invalid: its category must be one word", refused.getMessage());
  }

  @Test
  void test0001_8() throws CatalogException {
    Stratum stratum = Notes.stratum();
    Event both =
        new Event("A person shared a note", "share", Level.INFO, Set.of("to"), Set.of("to"));
    Catalog overlap = new Catalog("Sharing", "1.0", Map.of(Notes.SHARED, both));
    CatalogException refused = assertThrows(CatalogException.class, () -> stratum.add(overlap));
    assertEquals(
        "event note.shared is invalid: field to is both public and private", refused.getMessage());
  }
}
