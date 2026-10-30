package io.github.mayjournal.stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Requirement 0002: what each published event carries. */
class SnapshotTests {

  @Test
  void test0002_1() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    stratum.setContext("screen", "Today");
    stratum.publish(Notes.SAVED);
    assertEquals(Map.of("screen", "Today"), recorder.snapshots().get(0).context());
  }

  @Test
  void test0002_2() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    stratum.publish(
        Notes.SAVED, Map.of("words", "120", "title", "Dentist", "body", "Call the dentist"));
    Snapshot snapshot = recorder.snapshots().get(0);
    assertEquals(Map.of("words", "120"), snapshot.publicFields());
    assertEquals(Map.of("title", "Dentist"), snapshot.privateFields());
  }

  @Test
  void test0002_3() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    stratum.publish(Notes.SAVED);
    Snapshot snapshot = recorder.snapshots().get(0);
    assertEquals(List.of("Notes", "2.1"), List.of(snapshot.product(), snapshot.version()));
  }

  @Test
  void test0002_4() throws CatalogException {
    Stratum stratum = Notes.stratum();
    List<String> heard = new ArrayList<>();
    stratum.addListener(snapshot -> heard.add(snapshot.key()));
    stratum.publish(Notes.SAVED);
    assertEquals(List.of(Notes.SAVED), heard);
  }

  @Test
  void test0002_5() throws CatalogException {
    Recorder recorder = new Recorder();
    Stratum stratum = Notes.stratum(recorder);
    stratum.setContext("screen", "Today");
    stratum.setContext("screen", null);
    stratum.publish(Notes.SAVED);
    assertEquals(Map.of(), recorder.snapshots().get(0).context());
  }
}
