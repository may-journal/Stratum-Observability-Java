package io.github.mayjournal.stratum;

/** A recorder that takes only error events, as a crash reporter would. */
final class ErrorsOnly extends Recorder {

  ErrorsOnly() {
    super("errors");
  }

  @Override
  public boolean accepts(Snapshot snapshot) {
    return snapshot.level() == Level.ERROR;
  }
}
