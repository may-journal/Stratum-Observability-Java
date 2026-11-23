package stratum.jfr;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import org.junit.jupiter.api.Test;
import stratum.Stratum;

/** Requirement 0010: recording events with JDK Flight Recorder. */
class JfrTests {

  /** An engine for the notes product, version 2.1, with the Flight Recorder plugin. */
  static Stratum start() {
    Stratum stratum = new Stratum("Notes", "2.1", FlightCatalog.CATALOG, new JfrPlugin());
    stratum.setContext(entry(FlightField.SCREEN, "Today"));
    return stratum;
  }

  /** The Stratum events a recording file holds. */
  static List<RecordedEvent> read(Path file) throws IOException {
    try {
      return RecordingFile.readAllEvents(file).stream()
          .filter(event -> event.getEventType().getName().equals(JfrPlugin.EVENT))
          .toList();
    } finally {
      Files.delete(file);
    }
  }

  /**
   * A person acts in the notes app while a recording runs, and the app {@code publishes}. It
   * returns the Stratum events the recording holds.
   */
  static List<RecordedEvent> recorded(Consumer<Stratum> publishes) throws IOException {
    Path file = Files.createTempFile("stratum", ".jfr");
    try (Recording recording = new Recording()) {
      recording.start();
      publishes.accept(start());
      recording.stop();
      recording.dump(file);
    }
    return read(file);
  }

  /** The one Stratum event the recording holds. */
  static RecordedEvent event(Consumer<Stratum> publishes) throws IOException {
    List<RecordedEvent> events = recorded(publishes);
    assertEquals(1, events.size());
    return events.get(0);
  }

  @Test
  void test0010_1() throws IOException {
    RecordedEvent event = event(stratum -> stratum.publish(FlightKey.SAVED));
    assertEquals("note.saved", event.getString("key"));
    assertEquals(1, event.getInt("id"));
    assertEquals("A person saved a note", event.getString("description"));
    assertEquals(
        List.of("Notes", "2.1"), List.of(event.getString("product"), event.getString("version")));
  }

  @Test
  void test0010_2() throws IOException {
    RecordedEvent event = event(stratum -> stratum.publish(FlightKey.FAILED));
    assertEquals("ERROR", event.getString("level"));
  }

  @Test
  void test0010_3() throws IOException {
    RecordedEvent event =
        event(stratum -> stratum.publish(FlightKey.SAVED, entry(FlightField.WORDS, "120")));
    assertEquals("screen=Today words=120", event.getString("fields"));
  }

  @Test
  void test0010_4() throws IOException {
    RecordedEvent event =
        event(stratum -> stratum.publish(FlightKey.SAVED, entry(FlightField.TITLE, "Dentist")));
    assertEquals("screen=Today title=" + JfrPlugin.PRIVATE, event.getString("fields"));
  }

  @Test
  void test0010_5() throws IOException {
    List<RecordedEvent> events =
        recorded(stratum -> stratum.publish(FlightKey.OPENED, entry(FlightField.SOURCE, "search")));
    assertEquals(List.of(), events);
  }

  @Test
  void test0010_6() throws IOException {
    JfrPlugin plugin = new JfrPlugin();
    List<Boolean> asked = new ArrayList<>();
    Stratum stratum = start();
    stratum.addListener(snapshot -> asked.add(plugin.shouldPublish(snapshot)));
    assertTrue(stratum.publish(FlightKey.SAVED));
    assertEquals(List.of(false), asked);
    assertEquals(List.of(), recorded(each -> {}));
  }

  @Test
  void test0010_7() throws IOException {
    List<RecordedEvent> events =
        recorded(
            stratum -> {
              stratum.add(new JfrPlugin());
              stratum.publish(FlightKey.SAVED);
            });
    assertEquals(1, events.size());
  }
}
