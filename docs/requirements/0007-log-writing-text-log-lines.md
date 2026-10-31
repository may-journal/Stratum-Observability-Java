# 0007 Writing text log lines

## Why

My events land as plain text lines in the logging my app already runs. What a person wrote stays private there.

## Measurement

published log events the app's logging holds as one line under the product's logger with private fields hidden

-

events with the log type published through the log plugin

## Requirements

- 0007.1
    - Given Developer uses the log plugin
        - When the app writes the event as text
            - Then the logger named for my product holds one line that starts with its key
- 0007.2
    - Given Developer has an error event
        - When the sync fails
            - Then the line has the error level
- 0007.3
    - Given Developer passes a value for a public field
        - When a person saves 120 words
            - Then the line shows the field and its value
- 0007.4
    - Given Developer passes a value for a private field
        - When a person saves a titled note
            - Then the line names the field but hides its value
- 0007.5
    - Given Developer has set a value for a context field
        - When a person saves from the Today screen
            - Then the line shows the context and fields in name order
- 0007.6
    - Given Developer has an event without the log type
        - When a person opens a note by search
            - Then the log holds no line
- 0007.7
    - Given Developer adds the log plugin a second time
        - When a person saves a note
            - Then the log holds one line
