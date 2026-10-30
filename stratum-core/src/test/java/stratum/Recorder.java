package stratum;

import java.util.ArrayList;
import java.util.List;

/** A plugin an app writes for itself, which accepts every event and keeps each snapshot. */
class Recorder implements Plugin {

  private final String name;
  private final List<Snapshot> received = new ArrayList<>();

  Recorder() {
    this("recorder");
  }

  Recorder(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public synchronized void publish(Snapshot snapshot) {
    received.add(snapshot);
  }

  synchronized List<Snapshot> snapshots() {
    return List.copyOf(received);
  }

  List<String> keys() {
    return snapshots().stream().map(Snapshot::key).toList();
  }
}
