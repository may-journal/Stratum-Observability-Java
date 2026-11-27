# 0009 Checking metric settings

## Why

A metric event with bad `stratum.otel.metrics.MetricSettings` fails when its catalog is added, not when the event is published. A private field never becomes a metric value or attribute.

## Measurement

catalogs with bad metric settings that fail when added beside the metrics plugin

-

catalogs with bad metric settings

## Requirements

1. Refuses missing metric settings
   1. GIVEN Developer has an event with the metric type
   2. GIVEN Developer gave that event no metric settings
      1. WHEN I add its catalog beside the metrics plugin
         1. THEN adding fails
         2. THEN it names the event
2. Refuses a histogram without a value
   1. GIVEN Developer has a histogram with no value field
      1. WHEN I add its catalog beside the metrics plugin
         1. THEN adding fails
         2. THEN it names the metric
3. Refuses a private value
   1. GIVEN Developer has a metric whose value is a private field
      1. WHEN I add its catalog beside the metrics plugin
         1. THEN adding fails
         2. THEN it names the field
4. Refuses a private attribute
   1. GIVEN Developer has a metric attribute that is a private field
      1. WHEN I add its catalog beside the metrics plugin
         1. THEN adding fails
         2. THEN it names the field
5. Refuses an unnamed attribute
   1. GIVEN Developer has a metric attribute the event does not name
      1. WHEN I add its catalog beside the metrics plugin
         1. THEN adding fails
         2. THEN it names the field
