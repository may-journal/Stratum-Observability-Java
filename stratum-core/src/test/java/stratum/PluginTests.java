package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Requirement 0003: choosing where events go. */
class PluginTests {

  @Test
  void test0003_1() throws CatalogException {
    Stratum stratum = Notes.stratum();
    Recorder recorder = new Recorder();
    stratum.add(recorder);
    stratum.publish(Notes.SAVED);
    assertEquals(List.of(Notes.SAVED), recorder.keys());
  }

  @Test
  void test0003_2() throws CatalogException {
    ErrorsOnly errors = new ErrorsOnly();
    Stratum stratum = Notes.stratum(errors);
    stratum.publish(Notes.SAVED);
    assertEquals(List.of(), errors.keys());
  }

  @Test
  void test0003_3() throws CatalogException {
    Recorder first = new Recorder("crashes");
    Recorder second = new Recorder("crashes");
    Stratum stratum = Notes.stratum(first);
    stratum.add(second);
    stratum.publish(Notes.SAVED);
    assertEquals(List.of(), first.keys());
    assertEquals(List.of(Notes.SAVED), second.keys());
  }
}
