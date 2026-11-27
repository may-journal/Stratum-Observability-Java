# 0010 Recording flight events

## Why

My events sit on the JDK Flight Recorder timeline beside the JVM's own. What a person wrote stays private there.

## Measurement

published flight events a running recording holds with private fields hidden

-

events with the flight type published through the Flight Recorder plugin

## Requirements

1. Commits one flight event
   1. GIVEN Developer uses the Flight Recorder plugin while a recording runs
      1. WHEN a person saves a note during a recording
         1. THEN the recording holds one event
         2. THEN the event names the key
         3. THEN the event names the id
2. Names the error level
   1. GIVEN Developer has an error event
      1. WHEN the sync fails during a recording
         1. THEN the flight event names the error level
3. Orders context and fields
   1. GIVEN Developer passes a value for a public field
      1. WHEN a person saves 120 words from the Today screen
         1. THEN the flight event shows the context in name order
         2. THEN the flight event shows the field in name order
4. Hides a private value
   1. GIVEN Developer passes a value for a private field
      1. WHEN a person saves a titled note during a recording
         1. THEN the flight event names the field
         2. THEN the flight event hides its value
5. Skips another type
   1. GIVEN Developer has an event without the flight type
      1. WHEN a person opens a note by search during a recording
         1. THEN the recording holds no event
6. Survives no recording
   1. GIVEN Developer runs the app with no recording
      1. WHEN a person saves a note while nothing records
         1. THEN the plugin skips the event
         2. THEN publishing still succeeds
7. Ignores a repeated plugin
   1. GIVEN Developer adds the Flight Recorder plugin a second time
      1. WHEN a person saves a note after the second plugin is added
         1. THEN the recording holds one event
8. Names the event text
   1. GIVEN Developer uses the Flight Recorder plugin while a recording runs
      1. WHEN a person saves a note during a recording
         1. THEN the event names the description
         2. THEN the event names the product
         3. THEN the event names the version
