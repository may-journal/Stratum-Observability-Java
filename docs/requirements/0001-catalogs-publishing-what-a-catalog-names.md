# 0001 Publishing what a catalog names

## Why

My app publishes only the events I wrote down in a `stratum.Catalog`. Every event my team reads has one known meaning and one id.

I name each event and field by a constant of my own enums, which implement `stratum.CatalogKey` and `stratum.CatalogField`. The compiler refuses a key or field typed as text.

## Measurement

published keys that reach my plugins from a catalog that holds them

-

published keys

## Requirements

1. Publishes a named event
   1. GIVEN Developer has a catalog holding an event
      1. WHEN the app publishes it
         1. THEN my plugin receives that event
2. Ignores an unnamed key
   1. GIVEN Developer has no event under a key
      1. WHEN I publish that key
         1. THEN my plugin receives nothing
3. Adds a catalog after start
   1. GIVEN Developer adds a catalog after start
      1. WHEN I publish one of its events
         1. THEN my plugin receives it with that catalog's description
4. Forgets a removed catalog
   1. GIVEN Developer removes a catalog
      1. WHEN I publish one of its events
         1. THEN my plugin receives nothing
5. Refuses a repeated key
   1. GIVEN Developer has a key whose text another catalog holds
      1. WHEN I add the catalog
         1. THEN adding fails
         2. THEN it names the other catalog
6. Refuses a repeated id
   1. GIVEN Developer has an id another event holds
      1. WHEN I add its catalog
         1. THEN adding fails
         2. THEN it names the other event
7. Refuses a shared field
   1. GIVEN Developer has a field that is public
   2. GIVEN Developer made that same field private
      1. WHEN I add the catalog with it
         1. THEN adding fails
         2. THEN it names the field
8. Refuses text for a constant
   1. GIVEN Developer writes app code that publishes a key or a field as text
      1. WHEN I compile the app
         1. THEN the compiler refuses it
