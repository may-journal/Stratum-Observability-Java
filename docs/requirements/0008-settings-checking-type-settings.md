# 0008 Checking type settings

## Why

A catalog entry can carry `stratum.EventSettings`, the extra attributes its type's plugins read. A bad setting fails when its catalog or plugin is added, not when the event is published.

Stratum never reads settings. Each plugin checks the events of the types it accepts and refuses a bad one with `stratum.CatalogException`.

## Measurement

catalogs with a bad setting that fail when they are added

-

catalogs with a bad setting

## Requirements

1. Refuses missing settings
   1. GIVEN Developer has an event the plugin refuses for missing settings
      1. WHEN I add that plugin after start
         1. THEN adding fails and names the event
2. Refuses a private settings field
   1. GIVEN Developer has settings naming a private field the plugin needs public
      1. WHEN I add that catalog to a service with the plugin
         1. THEN adding fails and names the field
3. Ignores another type
   1. GIVEN Developer has a plugin that needs settings
      1. WHEN I add an event of another type without them
         1. THEN adding succeeds and the plugin never receives it
4. Refuses settings for another type
   1. GIVEN Developer has settings for a type the event does not have
      1. WHEN I add its catalog
         1. THEN adding fails and names both types
