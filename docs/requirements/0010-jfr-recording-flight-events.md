# 0010 Recording flight events

## Why

My events sit on the JDK Flight Recorder timeline beside the JVM's own. What a person wrote stays private there.

## Measurement

published flight events a running recording holds with private fields hidden

-

events with the flight type published through the Flight Recorder plugin

## Requirements

- 0010.1
    - Given Developer uses the Flight Recorder plugin while a recording runs
        - When a person saves a note during a recording
            - Then the recording holds one event that names the key, id, description, product and version
- 0010.2
    - Given Developer has an error event
        - When the sync fails during a recording
            - Then the flight event names the error level
- 0010.3
    - Given Developer passes a value for a public field
        - When a person saves 120 words from the Today screen
            - Then the flight event shows the context and the field in name order
- 0010.4
    - Given Developer passes a value for a private field
        - When a person saves a titled note during a recording
            - Then the flight event names the field but hides its value
- 0010.5
    - Given Developer has an event without the flight type
        - When a person opens a note by search during a recording
            - Then the recording holds no event
- 0010.6
    - Given Developer runs the app with no recording
        - When a person saves a note while nothing records
            - Then the plugin skips the event and publishing still succeeds
- 0010.7
    - Given Developer adds the Flight Recorder plugin a second time
        - When a person saves a note after the second plugin is added
            - Then the recording holds one event
