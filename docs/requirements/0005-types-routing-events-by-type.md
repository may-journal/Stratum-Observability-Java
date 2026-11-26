# 0005 Routing events by type

## Why

My catalog gives each event the type that decides which plugins receive it. One catalog entry then controls what every backend receives.

A plugin lists the `stratum.EventType` values it accepts, and receives an event of one of them, matched by name. A plugin that lists none accepts every event.

## Measurement

published events that reach exactly the plugins that accept their type

-

published events

## Requirements

1. Passes the type and settings
   1. GIVEN Developer has an event with a type and settings
      1. WHEN I publish it to a plugin that accepts every event
         1. THEN that plugin receives the type and the settings
2. Filters by type
   1. GIVEN Developer has a plugin that accepts one type
      1. WHEN I publish an event with that type and an event with another
         1. THEN that plugin receives only the event with its type
3. Matches a type by name
   1. GIVEN Developer has a plugin accepting another type of the same name
      1. WHEN I publish an event of that type
         1. THEN that plugin receives it
