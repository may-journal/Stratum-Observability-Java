# Code

The types an app uses, from [03-components.md](03-components.md). The root `pom.xml` builds every module for Java 21, with no runtime dependency.

```mermaid
classDiagram
    class Stratum["1 Stratum"] {
        +add(Catalog)
        +removeCatalog(String)
        +add(Plugin)
        +setContext(String, String)
        +addListener(Consumer~Snapshot~)
        +publish(String, Map~String, String~) boolean
    }
    class Catalog["2 Catalog"] {
        +name
        +version
        +events
    }
    class CatalogException["3 CatalogException"] {
        +key() String
    }
    class Event["4 Event"] {
        +meaning
        +category
        +level
        +publicFields
        +privateFields
        +eventTypes
        +settings
        +types(EventType...) Event
        +with(EventSettings) Event
    }
    class Level["5 Level"]
    class Snapshot["6 Snapshot"] {
        +key
        +level
        +eventTypes
        +settings(Class~T~) Optional~T~
        +context
        +time
    }
    class Plugin["7 Plugin"] {
        +name() String
        +acceptedTypes() Set~EventType~
        +shouldPublish(Snapshot) boolean
        +check(String, Event)
        +publish(Snapshot)
    }
    class EventType["8 EventType"] {
        +name() String
    }
    class EventSettings["9 EventSettings"] {
        +type() EventType
    }

    Stratum --> Catalog : A
    Stratum --> CatalogException : B
    Stratum --> Event : C
    Event --> Level : D
    Stratum --> Snapshot : E
    Stratum --> Plugin : F
    Plugin --> Snapshot : G
    Event --> EventType : H
    Event --> EventSettings : I
    EventSettings --> EventType : J
    Plugin --> EventType : K
    Plugin --> Event : L
    Plugin --> CatalogException : M
    Snapshot --> Event : N
    Snapshot --> EventType : O
    Snapshot --> EventSettings : P

    style Stratum fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style Catalog fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style CatalogException fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style Event fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style Level fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style Snapshot fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style Plugin fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style EventType fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style EventSettings fill:#6d28d9,stroke:#4c1d95,color:#ffffff
```

```mermaid
classDiagram
    class Example {
        +property
        +method()
    }
    class Parent
    class Child
    Parent <|-- Child : inherits
    Parent --> Child : uses

    style Example fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style Parent fill:#6d28d9,stroke:#4c1d95,color:#ffffff
    style Child fill:#6d28d9,stroke:#4c1d95,color:#ffffff
```

Numbers on classes and letters on relationships match the callout table.

| #   | Description | Why |
| --- | --- | --- |
| 1   | `Stratum` in [Stratum.java](../../stratum-core/src/main/java/stratum/Stratum.java), the service an app publishes through. It also adds plugins and listeners. | One final class, safe from any thread. |
| 2   | `Catalog` in [Catalog.java](../../stratum-core/src/main/java/stratum/Catalog.java), a named and versioned set of events by key. | A key belongs to one catalog. |
| 3   | `CatalogException` in [CatalogException.java](../../stratum-core/src/main/java/stratum/CatalogException.java), an invalid event or a duplicate key. | Adding a catalog fails at once, naming the event. |
| 4   | `Event` in [Event.java](../../stratum-core/src/main/java/stratum/Event.java), with its category, level, allowed fields, types and settings. | The category matches a logger name the app already uses. |
| 5   | `Level` in [Level.java](../../stratum-core/src/main/java/stratum/Level.java), from debug to fault. | Each plugin maps it onto its own scale. |
| 6   | `Snapshot` in [Snapshot.java](../../stratum-core/src/main/java/stratum/Snapshot.java), one published event with its context, product and time. | Plugins never read live state. |
| 7   | `Plugin` in [Plugin.java](../../stratum-core/src/main/java/stratum/Plugin.java), a destination with a name and the types it accepts. | A second plugin with the same name replaces the first. |
| 8   | `EventType` in [EventType.java](../../stratum-core/src/main/java/stratum/EventType.java), a type an event can have. | Plugin modules define the types, and two types with the same name match. |
| 9   | `EventSettings` in [EventSettings.java](../../stratum-core/src/main/java/stratum/EventSettings.java), settings for one type. | Only the plugin module that defines them reads them. |
| A   | The service holds catalogs and checks each one it adds. Each plugin checks the events whose types it accepts. | Every key is checked once. |
| B   | A bad or duplicate key throws a `CatalogException`. | The developer reads which event to fix. |
| C   | The service reads each key's event from its catalog. | Fields a key's event does not name are dropped. |
| D   | An event has one level. | Plugins read it to filter and to map. |
| E   | Publishing builds one snapshot. | Listeners and plugins share it. |
| F   | The service hands the snapshot to each plugin whose `shouldPublish` returns true. | A plugin can take only some events. |
| G   | A plugin receives snapshots. | It never sees the service's state. |
| H   | An event has the types that decide the plugins it reaches. | An event with no types reaches only plugins that accept every event. |
| I   | An event may give settings to some of its types. | Settings for a type it lacks fail when the catalog is added. |
| J   | Each settings value names its type. | A plugin receives only the settings of the types it accepts. |
| K   | A plugin lists the types it accepts. | An empty list accepts every event. |
| L   | A plugin checks each event whose types it accepts. | A bad setting fails when it is added. |
| M   | A plugin refuses a bad event with a `CatalogException`. | The developer reads which event to fix. |
| N   | A snapshot freezes one event. | It matches types the way its event does. |
| O   | A snapshot carries the event types its plugin accepts. | A typed plugin never sees other types. |
| P   | A snapshot carries the settings of those types. | A plugin reads its own settings by class. |

## Tests

`mvn verify` runs `stratum-core/src/test/java` and `stratum-otel/src/test/java`, one JUnit class per requirement doc. Each test starts a real `Stratum` for the notes product, publishes as an app does, and reads what the plugin or listener received. The OpenTelemetry tests read records from an in-memory exporter. JaCoCo holds line and branch coverage of each module at 100%.
