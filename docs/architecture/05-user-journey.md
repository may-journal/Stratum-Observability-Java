# User Journey

## Legend

```mermaid
flowchart TB
    subgraph types["Node types"]
        persona((1 Persona)):::persona
        screen[2 Screen]:::screen
        action(3 Action):::action
        system[(4 System)]:::system
    end

    classDef persona fill:#eef2ff,stroke:#6366f1,color:#312e81,stroke-width:2px
    classDef screen fill:#f8fafc,stroke:#64748b,color:#1e293b,stroke-width:2px
    classDef action fill:#fff7ed,stroke:#f97316,color:#9a3412,stroke-width:2px
    classDef system fill:#ecfdf5,stroke:#10b981,color:#064e3b,stroke-width:2px
```

Numbers match the callout table.

| #   | Description | Why |
| --- | ----------- | --- |
| 1   | Persona     | A person the system context declares. |
| 2   | Screen      | What the person sees. |
| 3   | Action      | What the person does. |
| 4   | System      | What the product does in reply. |

## Journey

```mermaid
flowchart TB
    dev((1 Developer)):::persona
    catalog(2 Write a catalog file<br/>Req 0001, 0005, 0008):::action
    plugins(3 Choose plugins<br/>Req 0003, 0005):::action
    publish(4 The app publishes<br/>Req 0001, 0002):::action
    snapshot[(5 Event frozen with context<br/>Req 0002, 0005)]:::system
    plugin[6 Event at the plugin<br/>Req 0003, 0004, 0007]:::screen

    dev --> catalog
    catalog --> plugins
    plugins --> publish
    publish --> snapshot
    snapshot --> plugin

    classDef persona fill:#eef2ff,stroke:#6366f1,color:#312e81,stroke-width:2px
    classDef screen fill:#f8fafc,stroke:#64748b,color:#1e293b,stroke-width:2px
    classDef action fill:#fff7ed,stroke:#f97316,color:#9a3412,stroke-width:2px
    classDef system fill:#ecfdf5,stroke:#10b981,color:#064e3b,stroke-width:2px
```

Numbers match the callout table.

| #   | Description | Type | Why |
| --- | ----------- | ---- | --- |
| 1   | The developer. | Persona | Same person as the system context. |
| 2   | Writes the catalog as a constant in its own file: each key, its type, description, id, fields and settings. | Action | The app publishes by key constant, so an unknown key does not compile. |
| 3   | Adds the plugins the app sends events to. | Action | Each plugin takes only the event types it accepts. |
| 4   | A person uses the app, and its code publishes a key with an option for each field it fills. | Action | Events start from what a person does. |
| 5   | Stratum freezes the event with its context and product. | System | Every plugin reads the same value. |
| 6   | The developer reads the event where the plugin sent it, such as an OpenTelemetry log record or a text log line. | Screen | Private fields stay in the fields the plugin must keep private. |
