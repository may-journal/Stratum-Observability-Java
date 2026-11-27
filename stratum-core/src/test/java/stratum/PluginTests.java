package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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

  @Test
  void test0003_4() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(new Broken(), recorder);
    assertTrue(stratum.publish(NoteKey.SAVED));
    assertEquals(List.of("note.saved"), recorder.keys());
  }

  @Test
  void test0003_5() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(recorder);
    stratum.addListener(
        snapshot -> {
          throw new IllegalStateException("the listener is closed");
        });
    assertTrue(stratum.publish(NoteKey.SAVED));
    assertEquals(List.of("note.saved"), recorder.keys());
  }

  @Test
  void test0003_6() {
    Stratum stratum = NotesEngine.start(new Broken());
    List<LogRecord> records = Warnings.during(() -> stratum.publish(NoteKey.SAVED));
    assertEquals(1, records.size());
    assertEquals(Level.WARNING, records.get(0).getLevel());
    assertEquals("plugin crashes failed on note.saved", records.get(0).getMessage());
    assertTrue(records.get(0).getMessage().contains("crashes"));
    assertTrue(records.get(0).getMessage().contains("note.saved"));
    assertInstanceOf(IllegalStateException.class, records.get(0).getThrown());
  }

  @Test
  void test0003_7() {
    Recorder recorder = new Recorder();
    Stratum stratum = NotesEngine.start(recorder);
    stratum.publish(NoteKey.SAVED);
    stratum.removePlugin("recorder");
    stratum.publish(NoteKey.FAILED);
    assertEquals(List.of("note.saved"), recorder.keys());
  }

  @Test
  void test0003_8() {
    List<String> heard = new ArrayList<>();
    Stratum stratum = NotesEngine.start();
    Listening listening = stratum.addListener(snapshot -> heard.add(snapshot.key()));
    stratum.publish(NoteKey.SAVED);
    listening.close();
    stratum.publish(NoteKey.FAILED);
    assertEquals(List.of("note.saved"), heard);
  }
}
