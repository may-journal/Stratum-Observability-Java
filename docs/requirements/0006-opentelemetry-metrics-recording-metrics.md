# 0006 Recording metrics

## Why

The events my catalog gives the metric type reach my OpenTelemetry meters as counters, histograms and gauges, and private fields never become attributes.

## Measurement

published metric events the app's reader holds with the catalog's instrument, value and public attributes

-

events with the metric type published through the OpenTelemetry metrics plugin

## Requirements

1. Counts each event
   1. GIVEN Developer uses the OpenTelemetry metrics plugin with my meter provider
      1. WHEN a person opens two notes
         1. THEN my reader holds the counter at two with the source attribute
2. Counts a value field
   1. GIVEN Developer has a counter with a value field
      1. WHEN a person types 120 words
         1. THEN my reader holds the counter at 120 in the catalog's unit
3. Records a histogram
   1. GIVEN Developer has a histogram event
      1. WHEN a sync takes 420 ms
         1. THEN my reader holds one histogram value with the catalog's unit and description and attribute
4. Keeps the latest gauge
   1. GIVEN Developer has a gauge event
      1. WHEN the app counts the notes twice
         1. THEN my reader holds the latest count
5. Skips a bad value
   1. GIVEN Developer passes a value that is missing or not a number
      1. WHEN the app publishes it
         1. THEN publishing succeeds and my reader holds nothing
6. Skips a log only event
   1. GIVEN Developer has an event with only the log type
      1. WHEN the app saves a note
         1. THEN my reader holds nothing
7. Hides a private attribute
   1. GIVEN Developer passes a private field beside the attribute field
      1. WHEN a person opens a note with a title
         1. THEN the counter's attributes leave the private field out
8. Uses the latest provider
   1. GIVEN Developer adds a second OpenTelemetry metrics plugin with another provider
      1. WHEN the app counts the notes once
         1. THEN only the second provider's reader holds the gauge
