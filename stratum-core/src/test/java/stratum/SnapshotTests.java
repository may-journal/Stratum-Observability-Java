package stratum;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Requirement 0002: what each published event carries. */
class SnapshotTests {

  @Test
  void test0002_1() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(recorder);
    stratum.setContext(entry(NoteField.SCREEN, "Today"));
    stratum.publish(NoteKey.SAVED);
    assertEquals(Map.of("screen", "Today"), recorder.snapshots().get(0).context());
  }

  @Test
  void test0002_2() {
    Recorder recorder = new Recorder();
    NotesEngine.start(recorder)
        .publish(
            NoteKey.SAVED,
            entry(NoteField.WORDS, "120"),
            entry(NoteField.TITLE, "Dentist"),
            entry(NoteField.BODY, "Call the dentist"));
    Snapshot snapshot = recorder.snapshots().get(0);
    assertEquals(Map.of("words", "120"), snapshot.publicFields());
    assertEquals(Map.of("title", "Dentist"), snapshot.privateFields());
  }

  @Test
  void test0002_3() {
    Recorder recorder = new Recorder();
    NotesEngine.start(recorder).publish(NoteKey.SAVED);
    Snapshot snapshot = recorder.snapshots().get(0);
    assertEquals(List.of("Notes", "2.1"), List.of(snapshot.product(), snapshot.version()));
  }

  @Test
  void test0002_4() {
    Stratum stratum = NotesEngine.start();
    List<String> heard = new ArrayList<>();
    stratum.addListener(snapshot -> heard.add(snapshot.key()));
    stratum.publish(NoteKey.SAVED);
    assertEquals(List.of("note.saved"), heard);
  }

  @Test
  void test0002_5() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(recorder);
    stratum.setContext(entry(NoteField.SCREEN, "Today"));
    stratum.clearContext(NoteField.SCREEN);
    stratum.publish(NoteKey.SAVED);
    assertEquals(Map.of(), recorder.snapshots().get(0).context());
  }

  @Test
  void test0002_6() {
    Recorder recorder = new Recorder();
    NotesEngine.start(recorder).publish(NoteKey.FAILED);
    Snapshot snapshot = recorder.snapshots().get(0);
    assertEquals(2, snapshot.id());
    assertEquals(Level.ERROR, snapshot.level());
  }
}
