package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Requirement 0012: reporting a failing plugin. */
class FailureTests {

  @Test
  void test0012_1() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(new Broken(), recorder);
    Warnings.failingDuring(() -> assertTrue(stratum.publish(NoteKey.SAVED)));
    assertEquals(List.of("note.saved"), recorder.keys());
  }
}
