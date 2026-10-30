# Components

This splits the library modules that run inside the host app of [02-containers.md](02-containers.md) into the files that build them.

```mermaid
C4Component
    title Stratum

    Person(dev, "1 Developer")

    Container_Boundary(build, "Build") {
        Component(poms, "2 Maven build", "XML", "pom.xml, stratum-core/pom.xml, stratum-otel/pom.xml, stratum-log/pom.xml, config/checkstyle/checkstyle.xml")
    }

    Container_Boundary(core, "3 stratum-core (stratum-core/src/main/java/stratum)") {
        Component(service, "4 Service", "Java", "stratum-core/src/main/java/stratum/Stratum.java")
        Component(catalog, "5 Catalog", "Java", "stratum-core/src/main/java/stratum/Catalog.java, stratum-core/src/main/java/stratum/CatalogException.java, stratum-core/src/main/java/stratum/Event.java, stratum-core/src/main/java/stratum/Level.java, stratum-core/src/main/java/stratum/EventType.java, stratum-core/src/main/java/stratum/EventSettings.java, stratum-core/src/main/java/stratum/CatalogKey.java, stratum-core/src/main/java/stratum/CatalogField.java, stratum-core/src/main/java/stratum/BaseType.java")
        Component(snapshot, "6 Snapshot and Plugin", "Java", "stratum-core/src/main/java/stratum/Snapshot.java, stratum-core/src/main/java/stratum/Plugin.java")
    }

    Container_Boundary(otel, "7 stratum-otel (stratum-otel/src/main/java/stratum/otel)") {
        Component(emitter, "8 OpenTelemetry plugin", "Java", "stratum-otel/src/main/java/stratum/otel/OpenTelemetryPlugin.java")
    }

    System_Ext(provider, "9 App's LoggerProvider", "The OpenTelemetry SDK the app already runs, with its own exporter.")

    Container_Boundary(log, "10 stratum-log (stratum-log/src/main/java/stratum/log)") {
        Component(writer, "11 Text log plugin", "Java", "stratum-log/src/main/java/stratum/log/LogPlugin.java, stratum-log/src/main/java/stratum/log/LogType.java")
    }

    System_Ext(backend, "12 App's logging", "java.util.logging, or the SLF4J or Log4j 2 bridge the app adds behind System.Logger.")

    Rel(dev, poms, "A")
    Rel(dev, service, "B")
    Rel(service, catalog, "C")
    Rel(service, snapshot, "D")
    Rel(snapshot, catalog, "E")
    Rel(emitter, snapshot, "F")
    Rel(emitter, provider, "G")
    Rel(writer, snapshot, "H")
    Rel(writer, backend, "I")

    UpdateElementStyle(dev, $bgColor="#334155", $borderColor="#1e293b", $fontColor="#ffffff")
    UpdateElementStyle(poms, $bgColor="#b45309", $borderColor="#92400e", $fontColor="#ffffff")
    UpdateElementStyle(service, $bgColor="#b45309", $borderColor="#92400e", $fontColor="#ffffff")
    UpdateElementStyle(catalog, $bgColor="#b45309", $borderColor="#92400e", $fontColor="#ffffff")
    UpdateElementStyle(snapshot, $bgColor="#b45309", $borderColor="#92400e", $fontColor="#ffffff")
    UpdateElementStyle(emitter, $bgColor="#b45309", $borderColor="#92400e", $fontColor="#ffffff")
    UpdateElementStyle(provider, $bgColor="#64748b", $borderColor="#475569", $fontColor="#ffffff")
    UpdateElementStyle(writer, $bgColor="#b45309", $borderColor="#92400e", $fontColor="#ffffff")
    UpdateElementStyle(backend, $bgColor="#64748b", $borderColor="#475569", $fontColor="#ffffff")
```

```mermaid
C4Component
    title Legend

    Person(p, "Person", "Human or agent actor")
    Container_Boundary(cb, "Container boundary") {
        Component(c, "Component", "Tech", "Logical unit")
    }
    System_Ext(e, "External", "Outside our boundary")

    UpdateElementStyle(p, $bgColor="#334155", $borderColor="#1e293b", $fontColor="#ffffff")
    UpdateElementStyle(c, $bgColor="#b45309", $borderColor="#92400e", $fontColor="#ffffff")
    UpdateElementStyle(e, $bgColor="#64748b", $borderColor="#475569", $fontColor="#ffffff")
```

Numbers on nodes and letters on arrows match the callout table.

| #   | Description | Why |
| --- | --- | --- |
| 1   | The developer, whose app depends on the modules and calls the service. | Every run starts with this actor. |
| 2   | The root `pom.xml` lists the modules and pins Java 21, each module pom builds one module, and the Checkstyle config holds the thresholds. | An app depends only on the modules it uses. |
| 3   | The core module, from `stratum-core/src/main/java`. | Catalogs, context and publishing, with no plugin of its own. |
| 4   | `Stratum` holds catalogs, plugins, context and listeners behind one lock, and publishes. | Safe to call from any thread. |
| 5   | `Catalog`, `CatalogException`, `Event`, `Level`, `EventType`, `EventSettings`, `CatalogKey`, `CatalogField` and `BaseType`. | Each catalog entry names its type, description, id, level, fields and settings. |
| 6   | `Snapshot` and the `Plugin` interface, with the types each plugin accepts. | Every plugin and listener reads one frozen value. |
| 7   | The OpenTelemetry module, from `stratum-otel/src/main/java`. | Only apps that run OpenTelemetry pull in its API. |
| 8   | `OpenTelemetryPlugin` emits each snapshot as a log record, with private fields hidden. | Events join the pipeline the app already runs. |
| 9   | The `LoggerProvider` the app passes in, with the exporter it chose. | The plugin owns no endpoint of its own. |
| 10  | The text log module, from `stratum-log/src/main/java`. | It needs nothing beyond the JDK. |
| 11  | `LogPlugin` accepts the `LogType.LOG` event type and writes each such snapshot as one text line, with private fields hidden. | A team sees its events with no server. |
| 12  | The backend behind `System.Logger` that the app runs. | The app decides where lines go. |
| A   | The app adds the modules to its build. | A Maven or Gradle dependency. |
| B   | The app adds catalogs and plugins, and publishes. | One service per product. |
| C   | The service and each plugin check every catalog it adds. | A bad key or setting fails when it is added, not when it is used. |
| D   | The service freezes each event into a snapshot for its plugins. | A later context change never reaches it. |
| E   | A snapshot carries the level, types and settings of the event it froze. | Plugins read what to send from it. |
| F   | The plugin reads each snapshot it accepts. | Its record holds the same frozen value. |
| G   | The plugin emits the record through the app's provider. | The app decides where records go. |
| H   | The text log plugin reads each snapshot it accepts. | Its line holds the same frozen value. |
| I   | The plugin writes the line to the logger named for the product. | Lines sit beside the app's own. |
