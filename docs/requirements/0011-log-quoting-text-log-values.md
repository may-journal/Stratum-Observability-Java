# 0011 Quoting text log values

## Why

Each event stays one line in the log, and each value parses back as I wrote it.

## Measurement

log values that parse back as written from lines the log plugin writes

-

log values written through the log plugin

## Requirements

1. Quotes a space
   1. GIVEN Developer passes a value that holds a space
      1. WHEN a person saves a note at the front desk
         1. THEN the line shows the value in double quotes
2. Escapes a line break
   1. GIVEN Developer passes a value that holds a line break
      1. WHEN a person saves a note with a pasted place
         1. THEN the log holds one line with the break escaped
3. Escapes special characters
   1. GIVEN Developer passes a value that holds quotes
   2. GIVEN Developer passes a value that holds signs
   3. GIVEN Developer passes a value that holds control characters
      1. WHEN a person saves a note with a pasted place
         1. THEN the line escapes each one inside the quotes
4. Quotes an empty value
   1. GIVEN Developer passes an empty value
      1. WHEN a person saves a note with no place
         1. THEN the line shows the field with empty quotes
5. Quotes a no break space
   1. GIVEN Developer passes a no break space
      1. WHEN a person saves a note
         1. THEN the line quotes the value
