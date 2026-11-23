package stratum.jfr;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

/**
 * The one flight event every catalogued event is committed as. Flight Recorder needs fixed fields,
 * so the context and field values share one text field.
 */
@Name(JfrPlugin.EVENT)
@Label("Stratum Event")
@Category("Stratum")
@Description("One catalogued event an app published through Stratum")
@StackTrace(false)
final class FlightEvent extends Event {

  @Label("Key")
  private final String key;

  @Label("Id")
  private final int id;

  @Label("Description")
  private final String description;

  @Label("Level")
  private final String level;

  @Label("Product")
  private final String product;

  @Label("Version")
  private final String version;

  @Label("Fields")
  private final String fields;

  /** A flight event for one snapshot, with its fields already written as text. */
  FlightEvent(stratum.Snapshot snapshot, String fields) {
    this.key = snapshot.key();
    this.id = snapshot.id();
    this.description = snapshot.description();
    this.level = snapshot.level().name();
    this.product = snapshot.product();
    this.version = snapshot.version();
    this.fields = fields;
  }
}
