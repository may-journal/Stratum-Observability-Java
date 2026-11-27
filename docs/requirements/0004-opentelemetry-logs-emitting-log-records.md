# 0004 Emitting log records

## Why

My events reach the OpenTelemetry pipeline my app already runs, and what a person wrote stays private there.

## Measurement

published events the app's exporter holds as log records with private fields hidden

-

events with the log type published through the OpenTelemetry logs plugin

## Requirements

1. Emits one record per event
   1. GIVEN Developer uses the OpenTelemetry logs plugin with my logger provider
      1. WHEN I publish through it
         1. THEN my exporter holds one record
         2. THEN the record names the event's key
         3. THEN the record names the event's id
2. Maps an error to its severity
   1. GIVEN Developer has an error event
      1. WHEN I publish the error
         1. THEN the record has the error severity
3. Shows a public value
   1. GIVEN Developer passes a value for a public field
      1. WHEN I emit the event
         1. THEN the record's attributes show its value
4. Hides a private value
   1. GIVEN Developer passes a value for a private field
      1. WHEN I emit that private field
         1. THEN the record names the field
         2. THEN the record hides its value
5. Names the product
   1. GIVEN Developer made the service for my product
      1. WHEN I emit an event
         1. THEN the record's body is the event's description
         2. THEN the record's attributes name my product
         3. THEN the record's attributes name my version
6. Uses the latest provider
   1. GIVEN Developer adds a second OpenTelemetry logs plugin with another provider
      1. WHEN I save a note
         1. THEN only the second provider's exporter holds the record
7. Skips a metric event
   1. GIVEN Developer has an event of the metric type
      1. WHEN I open a note
         1. THEN my exporter holds no record
8. Names the catalog
   1. GIVEN Developer named the catalog
   2. GIVEN Developer gave that catalog a version
      1. WHEN I emit an event that catalog holds
         1. THEN the record's attributes name that catalog
         2. THEN the record's attributes name that catalog's version
