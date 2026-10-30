# Stratum Observability for Java

A JVM app writes its events down in a catalog. Stratum publishes each one to the plugins the app chose. Every event has one key, one id, one description, and a list of the fields it may carry.

This is the Java edition. The Swift edition lives in [may-journal/Stratum-Observability-Swift](https://github.com/may-journal/Stratum-Observability-Swift), and the TypeScript edition in [may-journal/Stratum-Observability](https://github.com/may-journal/Stratum-Observability), forked from Capital One's [Stratum Observability](https://github.com/capitalone/Stratum-Observability).

## Modules

| Module | What it does |
| --- | --- |
| `stratum-core` | Catalogs, context, listeners and publishing. It needs no plugin and no runtime dependency. |
| `stratum-log` | `LogPlugin` writes each event with the `LogType.LOG` type as one text line through `System.Logger`. It works with java.util.logging, SLF4J and Log4j 2 through their bridges, with no dependency of its own. |
| `stratum-otel-logs` | `OpenTelemetryLogsPlugin` emits each event with the `OpenTelemetryLogType.LOG` type as a log record through the app's `LoggerProvider`. |
| `stratum-otel-metrics` | `OpenTelemetryMetricsPlugin` records each event with the `MetricType.METRIC` type through the app's `MeterProvider`, as its `MetricSettings` describe. |

Plugins are built in the order [Plan #1](https://github.com/may-journal/Stratum-Observability-Java/issues/1) set. Each plugin is its own module, and only an app that chooses a plugin sends data anywhere. See [Using OpenTelemetry](docs/usage/opentelemetry.md) to wire either OpenTelemetry plugin or both.

## Install

Build the modules with Maven and depend on `stratum:stratum-core` plus the plugin modules the app uses. It needs Java 21.

```sh
mvn -q verify
```

## Getting started

A typical Stratum setup has these parts, each in its own file:

1. Catalog keys: an enum of every event the app can publish
1. Catalog fields: an enum of every field its events and context carry
1. Catalog: the single source of truth for the voice of the app
1. A shared `Stratum` engine that loads the plugins and publishes
1. The app's own code, which publishes by key

### NoteKey.java

```java
import stratum.CatalogKey;

/** Enumerated keys replace hard-coded strings, so an unknown key does not compile. */
public enum NoteKey implements CatalogKey {
  LOADED("app.loaded"),
  SAVED("note.saved");

  private final String key;

  NoteKey(String key) {
    this.key = key;
  }

  /** The stable text plugins report, which renaming the constant never changes. */
  @Override
  public String key() {
    return key;
  }
}
```

### NoteField.java

```java
import stratum.CatalogField;

/** Enumerated fields, so a misspelled or undeclared field does not compile. */
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
import java.util.Map;
import stratum.BaseType;
import stratum.Catalog;
import stratum.Event;

/** The source of truth for every event the app publishes. */
public final class NotesCatalog {
  public static final Catalog CATALOG = new Catalog("Notes", "1.0", Map.of(
      // eventType, description, id (the reference id that queries find the event by)
      NoteKey.LOADED, Event.of(BaseType.BASE, "The app loaded for the first time", 1),
      NoteKey.SAVED, Event.of(BaseType.BASE, "A person saved a note", 2)
          .publicFields(NoteField.WORDS)
          .privateFields(NoteField.TITLE)));

  private NotesCatalog() {}
}
```

### NotesStratum.java

```java
import stratum.Stratum;

/** The shared engine: product name, product version, catalog, and 1+ plugins. */
public final class NotesStratum {
  public static final Stratum STRATUM =
      new Stratum("Notes", "2.1", NotesCatalog.CATALOG, new ConsolePlugin());

  private NotesStratum() {}
}
```

### App code

```java
import static java.util.Map.entry;

NotesStratum.STRATUM.publish(NoteKey.LOADED);
NotesStratum.STRATUM.publish(NoteKey.SAVED, entry(NoteField.WORDS, "120"), entry(NoteField.TITLE, "Dentist at noon"));
```

- Plugins report each key and field by its text, such as `note.saved` and `words`.
- A key no catalog holds publishes nothing, and `publish` returns false.
- Each value is a JDK `Map.Entry` from `Map.entry`. A field the event does not declare is left out.
- Public fields may be shown anywhere; a plugin keeps private fields out of what it sends.
- `setContext(entry(NoteField.SCREEN, "Today"))` adds context to every later event, and `clearContext(NoteField.SCREEN)` removes it. Context is public.
- `Event.of` gives an info event; `.level(Level.ERROR)` changes it.
- Starting the engine or adding a catalog throws `CatalogException` for a field both public and private.
- It also throws for a key text or id another event holds, even under another constant.

## Creating a custom plugin

A plugin is any class that implements `Plugin`. Adding a second plugin with the same name replaces the first.

### ConsolePlugin.java

```java
import java.util.Set;
import stratum.BaseType;
import stratum.EventType;
import stratum.Plugin;
import stratum.Snapshot;

/** A custom plugin: takes BASE events and prints each one, hiding private values. */
public final class ConsolePlugin implements Plugin {
  @Override
  public String name() {
    return "console";
  }

  @Override
  public Set<EventType> acceptedTypes() {
    return Set.of(BaseType.BASE);
  }

  @Override
  public void publish(Snapshot snapshot) {
    System.out.println(snapshot.id() + " " + snapshot.key() + " " + snapshot.publicFields());
  }
}
```

- `acceptedTypes()` lists the event types the plugin takes. An empty set, the default, takes every event.
- Types match by name, so two modules may each define a type with the same name and both receive its events.
- `shouldPublish(snapshot)` filters further, such as on level, or while the destination is unavailable.
- `check(key, event)` can refuse a bad catalog entry with `CatalogException` when its catalog or the plugin is added.

### Event types and settings

A plugin module defines its event types as an enum that implements `EventType`. Extra attributes for a type are a record that implements `EventSettings`, which the catalog entry carries. Stratum never reads settings.

```java
enum AuditType implements EventType { AUDIT }

record AuditSettings(String trail) implements EventSettings {
  public EventType type() { return AuditType.AUDIT; }
}

NoteKey.DELETED, Event.of(AuditType.AUDIT, "A person deleted a note", 3)
    .settings(new AuditSettings("notes")),
```

- The plugin reads them through `snapshot.settings(AuditSettings.class)`.
- Settings that name a field take the app's `CatalogField` constant, not text.
- Adding a catalog throws for settings whose type is not the event's type.

## Docs

- [Requirements](docs/requirements): what the library promises, one test for each acceptance.
- [Architecture](docs/architecture): the five views, from system context to user journey.
- [Text log plugin](docs/usage/log.md): how lines look, and which logging libraries the plugin works with.
- [Using OpenTelemetry](docs/usage/opentelemetry.md): how to wire the OpenTelemetry logs and metrics plugins.
- [Contributing](CONTRIBUTING.md): how to build, test and check a change.

## License

Apache 2.0, as the upstream project. See [LICENSE](LICENSE).
