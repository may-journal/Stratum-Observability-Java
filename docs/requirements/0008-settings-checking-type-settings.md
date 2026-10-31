# 0008 Checking type settings

## Why

A catalog entry can carry `stratum.EventSettings`, the extra attributes its type's plugins read. A bad setting fails when its catalog or plugin is added, not when the event is published.

Stratum never reads settings. Each plugin checks the events of the types it accepts and refuses a bad one with `stratum.CatalogException`.

## Measurement

catalogs with a bad setting that fail when they are added

-

catalogs with a bad setting

## Requirements

- 0008.1
    - Given Developer has an event the plugin refuses for missing settings
        - When I add that plugin after start
            - Then adding fails and names the event
- 0008.2
    - Given Developer has settings naming a private field the plugin needs public
        - When I add that catalog to a service with the plugin
            - Then adding fails and names the field
- 0008.3
    - Given Developer has a plugin that needs settings
        - When I add an event of another type without them
            - Then adding succeeds and the plugin never receives it
- 0008.4
    - Given Developer has settings for a type the event does not have
        - When I add its catalog
            - Then adding fails and names both types
