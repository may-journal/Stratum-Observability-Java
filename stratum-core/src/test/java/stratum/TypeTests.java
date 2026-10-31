package stratum;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Requirement 0005: event types and their settings. */
class TypeTests {

  @Test
  void test0005_1() {
    Recorder recorder = new Recorder();
    NotesEngine.start(recorder).publish(NoteKey.SYNCED, entry(NoteField.MILLIS, "420"));
    Snapshot snapshot = recorder.snapshots().get(0);
    assertEquals(CountType.COUNT, snapshot.eventType());
    assertEquals(
        Optional.of(new CountSettings(NoteField.MILLIS)), snapshot.settings(CountSettings.class));
  }

  @Test
  void test0005_2() {
    Recorder audit = new TypedRecorder("audit", AuditType.AUDIT);
    Stratum stratum = NotesEngine.start(audit);
    stratum.publish(NoteKey.DELETED);
    stratum.publish(NoteKey.COUNTED, entry(NoteField.NOTES, "12"));
    assertEquals(List.of("note.deleted"), audit.keys());
  }

  @Test
  void test0005_3() {
    Recorder tally = new TypedRecorder("tally", () -> "COUNT");
    NotesEngine.start(tally).publish(NoteKey.COUNTED, entry(NoteField.NOTES, "12"));
    assertEquals(List.of("notes.counted"), tally.keys());
  }
}
