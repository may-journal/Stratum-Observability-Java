# 0002 What each event carries

## Why

Each `stratum.Snapshot` tells me where the app was and which product it came from, and carries only the fields I allowed.

I pass each dynamic value as a JDK key-value pair of my field constant and its value, such as `entry(NoteField.WORDS, "120")`.

## Measurement

published events that carry the context and only allowed fields

-

published events

## Requirements

1. Carries a context value
   1. GIVEN Developer has set a context value
      1. WHEN I publish after setting it
         1. THEN the event carries that value
2. Leaves out an unnamed field
   1. GIVEN Developer passes a field the event does not name
      1. WHEN I publish with that field
         1. THEN the event leaves that field out
3. Names the product
   1. GIVEN Developer started the service for a product and version
      1. WHEN I publish through it
         1. THEN the event names both
4. Reaches a listener
   1. GIVEN Developer has added a listener
      1. WHEN I publish through the service
         1. THEN the listener receives it
5. Forgets a cleared value
   1. GIVEN Developer has cleared a context value
      1. WHEN I publish after clearing it
         1. THEN the event leaves that value out
6. Carries an id and level
   1. GIVEN Developer gave an event an id and a level
      1. WHEN I publish the event
         1. THEN it carries that id and level
7. Names its catalog
   1. GIVEN Developer named the catalog and gave it a version
      1. WHEN I publish an event that catalog holds
         1. THEN the event names that catalog and its version
