# 0002 What each event carries

## Why

Each `stratum.Snapshot` tells me where the app was and which product it came from, and carries only the fields I allowed.

I pass each dynamic value as a JDK key-value pair of my field constant and its value, such as `entry(NoteField.WORDS, "120")`.

## Measurement

published events that carry the context and only allowed fields

-

published events

## Requirements

- 0002.1
    - Given Developer has set a context value
        - When I publish after setting it
            - Then the event carries that value
- 0002.2
    - Given Developer passes a field the event does not name
        - When I publish with that field
            - Then the event leaves that field out
- 0002.3
    - Given Developer started the service for a product and version
        - When I publish through it
            - Then the event names both
- 0002.4
    - Given Developer has added a listener
        - When I publish through the service
            - Then the listener receives it
- 0002.5
    - Given Developer has cleared a context value
        - When I publish after clearing it
            - Then the event leaves that value out
- 0002.6
    - Given Developer gave an event an id and a level
        - When I publish the event
            - Then it carries that id and level
