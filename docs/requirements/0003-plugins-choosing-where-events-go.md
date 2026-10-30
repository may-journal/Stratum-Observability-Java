# 0003 Choosing where events go

## Why

I choose which `stratum.Plugin` classes receive my events, and each plugin takes only the events it wants.

## Measurement

plugins that receive exactly the events they accept

-

plugins

## Requirements

- 0003.1
    - Given Developer adds a plugin after start
        - When I publish an event
            - Then that plugin receives it
- 0003.2
    - Given Developer has a plugin that takes only errors
        - When I publish an info event
            - Then that plugin receives nothing
- 0003.3
    - Given Developer adds a second plugin with the same name
        - When I publish once
            - Then only the second plugin receives it
