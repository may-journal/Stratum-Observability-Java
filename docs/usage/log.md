# Using the text log plugin

`LogPlugin` in `stratum-log` writes each event as one plain text line through `java.lang.System.Logger`. It needs nothing beyond the JDK, so a team sees its events with no server.

## Adding the plugin

Depend on `stratum:stratum-log`, give each catalog entry to log the `LogType.LOG` type, and add the plugin to the engine.

### NoteKey.java

```java
public enum NoteKey implements CatalogKey {
  SAVED("note.saved");

  private final String key;

  NoteKey(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
```

### NoteField.java

```java
public enum NoteField implements CatalogField {
  WORDS("words"),
  TITLE("title"),
  SCREEN("screen");

  private final String key;

  NoteField(String key) {
    this.key = key;
  }

  @Override
  public String key() {
    return key;
  }
}
```

### NotesCatalog.java

```java
public final class NotesCatalog {
  public static final Catalog CATALOG = new Catalog("Notes", "1.0", Map.of(
      NoteKey.SAVED, Event.of(LogType.LOG, "A person saved a note", 1)
          .publicFields(NoteField.WORDS)
          .privateFields(NoteField.TITLE)));

  private NotesCatalog() {}
}
```

### NotesStratum.java

```java
public final class NotesStratum {
  public static final Stratum STRATUM =
      new Stratum("Notes", "2.1", NotesCatalog.CATALOG, new LogPlugin());

  private NotesStratum() {}
}
```

### App code

```java
import static java.util.Map.entry;

NotesStratum.STRATUM.setContext(entry(NoteField.SCREEN, "Today"));
NotesStratum.STRATUM.publish(NoteKey.SAVED, entry(NoteField.WORDS, "120"), entry(NoteField.TITLE, "Dentist at noon"));
```

That call writes this line to the logger named `Notes`, at the info level.

```text
note.saved screen=Today title=<private> words=120
```

## How a snapshot becomes a line

- The logger is named for the product, so all its lines sit together.
- The line starts with the event's key text, such as `note.saved`.
- Context and public fields follow as `name=value` pairs, in name order.
- Private fields appear by name only, with the value `<private>`.
- The plugin accepts only events whose catalog gives them the `LogType.LOG` type.

| Level | System.Logger level |
| --- | --- |
| `DEBUG` | `DEBUG` |
| `INFO` | `INFO` |
| `NOTICE` | `INFO` |
| `WARNING` | `WARNING` |
| `ERROR` | `ERROR` |
| `FAULT` | `ERROR` |

## Logging libraries

`System.Logger` is the JDK's logging facade. The common libraries plug in behind it through a bridge the app adds, so `stratum-log` stays dependency free.

| Library | What the app adds |
| --- | --- |
| java.util.logging | Nothing, since it is the JDK's default backend. |
| SLF4J, with Logback or another binding | `org.slf4j:slf4j-jdk-platform-logging` |
| Log4j 2 | `org.apache.logging.log4j:log4j-jpl` |

A `System.Logger` line carries a message and a level. It has no structured fields or MDC, so public fields reach the library only as text in the line. A team that needs native key and value pairs would add a separate plugin for that library.
