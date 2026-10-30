# 0009 Checking metric settings

## Why

A metric event with bad `stratum.otel.metrics.MetricSettings` fails when its catalog is added, not when the event is published. A private field never becomes a metric value or attribute.

## Measurement

catalogs with bad metric settings that fail when added beside the metrics plugin

-

catalogs with bad metric settings

## Requirements

- 0009.1
    - Given Developer has an event with the metric type and no metric settings
        - When I add its catalog beside the metrics plugin
            - Then adding fails and names the event
- 0009.2
    - Given Developer has a histogram with no value field
        - When I add its catalog beside the metrics plugin
            - Then adding fails and names the metric
- 0009.3
    - Given Developer has a metric whose value is a private field
        - When I add its catalog beside the metrics plugin
            - Then adding fails and names the field
- 0009.4
    - Given Developer has a metric attribute that is a private field
        - When I add its catalog beside the metrics plugin
            - Then adding fails and names the field
- 0009.5
    - Given Developer has a metric attribute the event does not name
        - When I add its catalog beside the metrics plugin
            - Then adding fails and names the field
