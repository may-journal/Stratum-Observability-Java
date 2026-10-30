# 0008 Checking type settings

## Why

My catalog gives an event's types the `stratum.EventSettings` their plugins read. A bad setting fails when its catalog or plugin is added, not when the event is published.

Stratum never reads settings. Each plugin checks the events whose types it accepts and refuses a bad one with `stratum.CatalogException`.

## Measurement

catalogs with a bad setting that fail when they are added

-

catalogs with a bad setting

## Requirements

- 0008.1
    - Given Developer has a plugin that needs settings an event of its type lacks
        - When I start the service with that plugin and catalog
            - Then starting fails and names the event
- 0008.2
    - Given Developer has settings naming a private field the plugin needs public
        - When I start the service with that plugin and catalog
            - Then starting fails and names the field
- 0008.3
    - Given Developer has an event the plugin refuses
        - When I add that plugin after start
            - Then adding fails and names the event
- 0008.4
    - Given Developer has a plugin that needs settings
        - When I add an event of another type without them
            - Then adding succeeds and the plugin never receives it
- 0008.5
    - Given Developer has settings for a type the event does not have
        - When I add its catalog
            - Then adding fails and names the type
- 0008.6
    - Given Developer gives one type settings twice
        - When I publish the event
            - Then the plugin receives only the later settings
