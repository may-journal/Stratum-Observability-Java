# 0005 Routing events by type

## Why

My catalog gives each event the type that decides which plugins receive it. One catalog entry then controls what every backend receives.

A plugin lists the `stratum.EventType` values it accepts, and receives an event of one of them, matched by name. A plugin that lists none accepts every event.

## Measurement

published events that reach exactly the plugins that accept their type

-

published events

## Requirements

- 0005.1
    - Given Developer has an event with a type and settings
        - When I publish it to a plugin that accepts every event
            - Then that plugin receives the type and the settings
- 0005.2
    - Given Developer has a plugin that accepts one type
        - When I publish an event with that type and an event with another
            - Then that plugin receives only the event with its type
- 0005.3
    - Given Developer has a plugin accepting another type of the same name
        - When I publish an event of that type
            - Then that plugin receives it
