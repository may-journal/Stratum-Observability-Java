# 0004 Emitting log records

## Why

My events reach the OpenTelemetry pipeline my app already runs, and what a person wrote stays private there.

## Measurement

published events the app's exporter holds as log records with private fields hidden

-

events published through the OpenTelemetry plugin

## Requirements

- 0004.1
    - Given Developer uses the OpenTelemetry plugin with my logger provider
        - When I publish through it
            - Then my exporter holds one record that names the event's key and id
- 0004.2
    - Given Developer has an error event
        - When I publish the error
            - Then the record has the error severity
- 0004.3
    - Given Developer passes a value for a public field
        - When I emit the event
            - Then the record's attributes show its value
- 0004.4
    - Given Developer passes a value for a private field
        - When I emit that private field
            - Then the record names the field but hides its value
- 0004.5
    - Given Developer made the service for my product
        - When I emit an event
            - Then the record's body is the event's description and its attributes name my product and version
- 0004.6
    - Given Developer adds a second OpenTelemetry plugin with another provider
        - When I save a note
            - Then only the second provider's exporter holds the record
