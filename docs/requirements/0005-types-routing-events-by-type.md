# 0005 Routing events by type

## Why

My catalog gives each event the types that decide which plugins receive it. One event definition then controls what every backend receives.

A plugin lists the `stratum.EventType` values it accepts, and receives an event that has one of them, matched by name. A plugin that lists none accepts every event. An event with no types reaches only those plugins.

## Measurement

published events that reach exactly the plugins whose types they have

-

published events

## Requirements

- 0005.1
    - Given Developer has an event with two types and settings
        - When I publish it to a plugin that accepts every event
            - Then that plugin receives both types and the settings
- 0005.2
    - Given Developer has a plugin that accepts one type
        - When I publish an event with that type and an event with another
            - Then that plugin receives only the event with its type
- 0005.3
    - Given Developer has plugins that each accept one of an event's two types
        - When I publish it
            - Then each plugin receives only its own type and that type's settings
- 0005.4
    - Given Developer has an event with no types
        - When I publish it
            - Then only the plugin that accepts every event receives it
- 0005.5
    - Given Developer has a plugin accepting another type of the same name
        - When I publish an event of that type
            - Then that plugin receives it
