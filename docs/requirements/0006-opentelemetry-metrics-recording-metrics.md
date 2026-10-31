# 0006 Recording metrics

## Why

The events my catalog gives the metric type reach my OpenTelemetry meters as counters, histograms and gauges, and private fields never become attributes.

## Measurement

published metric events the app's reader holds with the catalog's instrument, value and public attributes

-

events with the metric type published through the OpenTelemetry metrics plugin

## Requirements

- 0006.1
    - Given Developer uses the OpenTelemetry metrics plugin with my meter provider
        - When a person opens two notes
            - Then my reader holds the counter at two with the source attribute
- 0006.2
    - Given Developer has a counter with a value field
        - When a person types 120 words
            - Then my reader holds the counter at 120 in the catalog's unit
- 0006.3
    - Given Developer has a histogram event
        - When a sync takes 420 ms
            - Then my reader holds one histogram value with the catalog's unit, description and attribute
- 0006.4
    - Given Developer has a gauge event
        - When the app counts the notes twice
            - Then my reader holds the latest count
- 0006.5
    - Given Developer passes a value that is missing or not a number
        - When the app publishes it
            - Then publishing succeeds and my reader holds nothing
- 0006.6
    - Given Developer has an event with only the log type
        - When the app saves a note
            - Then my reader holds nothing
- 0006.7
    - Given Developer passes a private field beside the attribute field
        - When a person opens a note with a title
            - Then the counter's attributes leave the private field out
- 0006.8
    - Given Developer adds a second OpenTelemetry metrics plugin with another provider
        - When the app counts the notes once
            - Then only the second provider's reader holds the gauge
