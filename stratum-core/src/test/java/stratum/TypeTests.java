package stratum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Requirement 0005: event types and their settings. */
class TypeTests {

  @Test
  void test0005_1() throws CatalogException {
    Recorder recorder = new Recorder();
    Notes.stratum(recorder).publish(Notes.SYNCED, Map.of("millis", "420", "kind", "full"));
    Snapshot snapshot = recorder.snapshots().get(0);
    assertEquals(Set.of(AuditType.AUDIT, CountType.COUNT), snapshot.eventTypes());
    assertEquals(Optional.of(new CountSettings("millis")), snapshot.settings(CountSettings.class));
  }

  @Test
  void test0005_2() throws CatalogException {
    Recorder audit = new TypedRecorder("audit", AuditType.AUDIT);
    Stratum stratum = Notes.stratum(audit);
    stratum.publish(Notes.DELETED);
    stratum.publish(Notes.COUNTED, Map.of("notes", "12"));
    assertEquals(List.of(Notes.DELETED), audit.keys());
  }

  @Test
  void test0005_3() throws CatalogException {
    Recorder audit = new TypedRecorder("audit", AuditType.AUDIT);
    Counter counter = new Counter();
    Notes.stratum(audit, counter).publish(Notes.SYNCED, Map.of("millis", "420"));
    Snapshot audited = audit.snapshots().get(0);
    Snapshot counted = counter.snapshots().get(0);
    assertEquals(Set.of(AuditType.AUDIT), audited.eventTypes());
    assertEquals(List.of(), audited.settings());
    assertEquals(Set.of(CountType.COUNT), counted.eventTypes());
    assertEquals(List.of(new CountSettings("millis")), counted.settings());
  }

  @Test
  void test0005_4() throws CatalogException {
    Recorder audit = new TypedRecorder("audit", AuditType.AUDIT);
    Recorder recorder = new Recorder();
    Notes.stratum(audit, recorder).publish(Notes.SAVED);
    assertEquals(List.of(), audit.keys());
    assertEquals(List.of(Notes.SAVED), recorder.keys());
  }

  @Test
  void test0005_5() throws CatalogException {
    EventType count = () -> "COUNT";
    Recorder tally = new TypedRecorder("tally", count);
    Notes.stratum(tally).publish(Notes.COUNTED, Map.of("notes", "12"));
    assertEquals(List.of(Notes.COUNTED), tally.keys());
  }
}
