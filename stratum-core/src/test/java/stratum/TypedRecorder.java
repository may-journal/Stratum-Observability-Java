package stratum;

import java.util.Set;

/** A recorder that accepts only events with one of its types. */
class TypedRecorder extends Recorder {

  private final Set<EventType> accepted;

  TypedRecorder(String name, EventType... accepted) {
    super(name);
    this.accepted = Set.of(accepted);
  }

  @Override
  public Set<EventType> acceptedTypes() {
    return accepted;
  }
}
