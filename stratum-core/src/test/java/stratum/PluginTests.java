package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Requirement 0003: choosing where events go. */
class PluginTests {

  @Test
  void test0003_1() {
    Stratum stratum = NotesEngine.start();
    Recorder recorder = new Recorder();
    stratum.add(recorder);
    stratum.publish(NoteKey.SAVED);
    assertEquals(List.of("note.saved"), recorder.keys());
  }

  @Test
  void test0003_2() {
    ErrorsOnly errors = new ErrorsOnly();
    NotesEngine.start(errors).publish(NoteKey.SAVED);
    assertEquals(List.of(), errors.keys());
  }

  @Test
  void test0003_3() {
    Recorder first = new Recorder("crashes");
    Recorder second = new Recorder("crashes");
    Stratum stratum = NotesEngine.start(first);
    stratum.add(second);
    stratum.publish(NoteKey.SAVED);
    assertEquals(List.of(), first.keys());
    assertEquals(List.of("note.saved"), second.keys());
  }
}
