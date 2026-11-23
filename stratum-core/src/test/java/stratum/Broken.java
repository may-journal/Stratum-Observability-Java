package stratum;

/** A plugin whose destination is down, so every publish throws. */
final class Broken implements Plugin {

  @Override
  public String name() {
    return "crashes";
  }

  @Override
  public void publish(Snapshot snapshot) {
    throw new IllegalStateException("the crash reporter is down");
  }
}
