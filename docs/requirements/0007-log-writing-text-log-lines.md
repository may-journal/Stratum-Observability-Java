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

- 0007.8
    - Given Developer passes a value that holds a space
        - When a person saves a note at the front desk
            - Then the line shows the value in double quotes
- 0007.9
    - Given Developer passes a value that holds a line break
        - When a person saves a note with a pasted place
            - Then the log holds one line with the break escaped
- 0007.10
    - Given Developer passes a value that holds quotes, signs and control characters
        - When a person saves a note with a pasted place
            - Then the line escapes each one inside the quotes
- 0007.11
    - Given Developer passes an empty value
        - When a person saves a note with no place
            - Then the line shows the field with empty quotes
- 0007.12
    - Given Developer passes a no-break space
        - When a person saves a note
            - Then the line quotes the value
