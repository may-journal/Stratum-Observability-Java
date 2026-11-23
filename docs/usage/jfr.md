# Using the Flight Recorder plugin

`JfrPlugin` in `stratum-jfr` commits each event to JDK Flight Recorder, the recorder built into every JDK. It needs nothing beyond the JDK, and it does nothing while no recording runs.

## Adding the plugin

Depend on `stratum:stratum-jfr`, give each catalog entry to record the `JfrType.JFR` type, and add the plugin to the engine.

```java
public final class NotesCatalog {
  public static final Catalog CATALOG = new Catalog("Notes", "1.0", Map.of(
      NoteKey.SAVED, Event.of(JfrType.JFR, "A person saved a note", 1)
          .publicFields(NoteField.WORDS)
          .privateFields(NoteField.TITLE)));

  private NotesCatalog() {}
}

public final class NotesStratum {
  public static final Stratum STRATUM =
      new Stratum("Notes", "2.1", NotesCatalog.CATALOG, new JfrPlugin());

  private NotesStratum() {}
}
```

## Reading the events

Start a recording as usual, such as with `java -XX:StartFlightRecording:filename=notes.jfr`. Then open the file in JDK Mission Control, or print the events:

```sh
jfr print --events stratum.Event notes.jfr
```

```text
stratum.Event {
  startTime = 14:02:11.482
  key = "note.saved"
  id = 1
  description = "A person saved a note"
  level = "INFO"
  product = "Notes"
  version = "2.1"
  fields = "screen=Today title=<private> words=120"
}
```

## How a snapshot becomes a flight event

- Every catalogued event is one flight event named `stratum.Event`, in the `Stratum` category.
- Flight Recorder needs fixed fields, so the context and field values share the `fields` text.
- Context and public fields read as `name=value` pairs, in name order.
- Private fields appear by name only, with the value `<private>`.
- The plugin accepts only events whose catalog gives them the `JfrType.JFR` type.
- While no recording has `stratum.Event` enabled, the plugin builds nothing.
