# Stratum Observability for Java

A JVM app writes its events down in a catalog. Stratum publishes each one to the plugins the app chose. Every event has one key, one meaning, and a list of the fields it may carry.

This is the Java edition. The Swift edition lives in [may-journal/Stratum-Observability-Swift](https://github.com/may-journal/Stratum-Observability-Swift), and the TypeScript edition in [may-journal/Stratum-Observability](https://github.com/may-journal/Stratum-Observability), forked from Capital One's [Stratum Observability](https://github.com/capitalone/Stratum-Observability).

## Modules

| Module | What it does |
| --- | --- |
| `stratum-core` | Catalogs, context, listeners and publishing. It needs no plugin and no runtime dependency. |

Plugins are built in the order [Plan #1](https://github.com/may-journal/Stratum-Observability-Java/issues/1) set. An OpenTelemetry plugin comes first, as its own module, and only an app that chooses a plugin sends data anywhere.

## Install

Build the modules with Maven and depend on `stratum:stratum-core` plus the plugin modules the app uses. It needs Java 21.

```sh
mvn -q verify
```

## Use

Write a catalog, start one service with the app's plugins, and publish by key. An event's category can match a logger name the app already uses, so its entries sit beside the app's own lines.

```java
import stratum.Catalog;
import stratum.Event;
import stratum.Level;
import stratum.Stratum;

Catalog catalog = new Catalog("Notes", "1.0", Map.of(
    "note.saved", new Event("A person saved a note", "editor", Level.INFO,
        Set.of("words"), Set.of("title")),
    "sync.failed", new Event("The notes failed to sync", "sync", Level.ERROR)));

Stratum stratum = new Stratum("Notes", "2.1", List.of(catalog), List.of(plugin));

stratum.setContext("screen", "Today");
stratum.publish("note.saved", Map.of("words", "120", "title", "Dentist at noon"));
```

- A key no catalog holds publishes nothing, and `publish` returns false.
- A field the event does not name is dropped.
- Public fields may be shown anywhere; a plugin keeps private fields out of what it sends.
- Context is public, so keep what a person wrote out of it.
- A plugin is any class that implements `Plugin`. Adding a second plugin with the same name replaces the first.
- Adding a catalog throws `CatalogException` for a key or category of more than one word.
- It also throws for a field that is both public and private, or a key another catalog holds.

### Types

A plugin module defines its event types as an enum that implements `EventType`. Its settings are a record that implements `EventSettings`. Stratum defines no types and never reads settings.

```java
enum AuditType implements EventType { AUDIT }

record AuditSettings(String trail) implements EventSettings {
  public EventType type() { return AuditType.AUDIT; }
}

Event deleted = new Event("A person deleted a note", "editor")
    .types(AuditType.AUDIT)
    .with(new AuditSettings("notes"));
```

- A plugin returns the types it accepts from `acceptedTypes()`. It receives an event that has one of them.
- A plugin that accepts no types, the default, receives every event. An event with no types reaches only those plugins.
- Types match by name, so two modules may each define a type with the same name and both receive its events.
- Each plugin receives only its accepted types and their settings, through `snapshot.settings(AuditSettings.class)`.
- A plugin can override `shouldPublish` to filter on what an event carries.
- Its `check` can refuse a bad event with `CatalogException` when its catalog or the plugin is added.
- Adding a catalog also throws for settings whose type the event does not have.

## Docs

- [Requirements](docs/requirements): what the library promises, one test for each acceptance.
- [Architecture](docs/architecture): the five views, from system context to user journey.
- [Contributing](CONTRIBUTING.md): how to build, test and check a change.

## License

Apache 2.0, as the upstream project. See [LICENSE](LICENSE).
