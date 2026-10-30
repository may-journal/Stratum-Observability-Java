# System Context

```mermaid
C4Context
    title Stratum Observability for Java

    Person(dev, "1 Developer")
    System(stratum, "2 Stratum", "Java library that publishes catalogued events to the plugins an app chose.")
    System_Ext(app, "3 Host app", "The JVM app or service that depends on the library.")

    Rel(dev, app, "A")
    Rel(app, stratum, "B")

    UpdateElementStyle(dev, $bgColor="#334155", $borderColor="#1e293b", $fontColor="#ffffff")
    UpdateElementStyle(stratum, $bgColor="#1d4ed8", $borderColor="#1e3a8a", $fontColor="#ffffff")
    UpdateElementStyle(app, $bgColor="#64748b", $borderColor="#475569", $fontColor="#ffffff")
```

```mermaid
C4Context
    title Legend

    Person(p, "Person", "Human or agent actor")
    System(s, "System", "Software we own")
    System_Ext(e, "External", "Outside our boundary")

    UpdateElementStyle(p, $bgColor="#334155", $borderColor="#1e293b", $fontColor="#ffffff")
    UpdateElementStyle(s, $bgColor="#1d4ed8", $borderColor="#1e3a8a", $fontColor="#ffffff")
    UpdateElementStyle(e, $bgColor="#64748b", $borderColor="#475569", $fontColor="#ffffff")
```

Numbers on nodes and letters on arrows match the callout table.

| #   | Description | Why |
| --- | --- | --- |
| 1   | Writes an app, its catalog of events, and the plugins it publishes to. | Requirements name this actor in a Given. |
| 2   | The `stratum-core` library and the plugin modules built on it. | One way to publish, whatever the destination. |
| 3   | The app a person uses, which depends on the library. | Events start from what a person does there. |
| A   | The developer builds the app with the library. | The app chooses its catalogs and plugins. |
| B   | The app publishes events by key. | One call per event, checked against the catalog. |
