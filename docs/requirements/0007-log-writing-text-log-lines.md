# 0007 Writing text log lines

## Why

My events land as plain text lines in the logging my app already runs. What a person wrote stays private there.

## Measurement

published log events the app's logging holds as one line under the product's logger with private fields hidden

-

events with the log type published through the log plugin

## Requirements

1. Writes one line per event
   1. GIVEN Developer uses the log plugin
      1. WHEN the app writes the event as text
         1. THEN the logger named for my product holds one line that starts with its key
2. Marks an error
   1. GIVEN Developer has an error event
      1. WHEN the sync fails
         1. THEN the line has the error level
3. Shows a public value
   1. GIVEN Developer passes a value for a public field
      1. WHEN a person saves 120 words
         1. THEN the line shows the field and its value
4. Hides a private value
   1. GIVEN Developer passes a value for a private field
      1. WHEN a person saves a titled note
         1. THEN the line names the field but hides its value
5. Orders context and fields
   1. GIVEN Developer has set a value for a context field
      1. WHEN a person saves from the Today screen
         1. THEN the line shows the context and fields in name order
6. Skips another type
   1. GIVEN Developer has an event without the log type
      1. WHEN a person opens a note by search
         1. THEN the log holds no line
7. Ignores a repeated plugin
   1. GIVEN Developer adds the log plugin a second time
      1. WHEN a person saves a note
         1. THEN the log holds one line
8. Removed in 2026.11.26.1140: moved to 0011.1 because 0007 held more acceptances than the cap allows
9. Removed in 2026.11.26.1140: moved to 0011.2 because 0007 held more acceptances than the cap allows
10. Removed in 2026.11.26.1140: moved to 0011.3 because 0007 held more acceptances than the cap allows
11. Removed in 2026.11.26.1140: moved to 0011.4 because 0007 held more acceptances than the cap allows
12. Removed in 2026.11.26.1140: moved to 0011.5 because 0007 held more acceptances than the cap allows
