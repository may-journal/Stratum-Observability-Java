# 0003 Choosing where events go

## Why

I choose which `stratum.Plugin` classes receive the events my catalog holds, and each plugin takes only the events it wants.

## Measurement

plugins that receive exactly the events they accept

-

plugins

## Requirements

1. Adds a plugin after start
   1. GIVEN Developer adds a plugin after start
      1. WHEN I publish an event
         1. THEN that plugin receives it
2. Skips a refused event
   1. GIVEN Developer has a plugin that takes only errors
      1. WHEN I publish an info event
         1. THEN that plugin receives nothing
3. Replaces a plugin of the same name
   1. GIVEN Developer adds a second plugin with the same name
      1. WHEN I publish once
         1. THEN only the second plugin receives it
4. Survives a failing destination
   1. GIVEN Developer has a plugin whose destination fails
      1. WHEN I publish an event
         1. THEN my other plugins still receive it
5. Survives a failing listener
   1. GIVEN Developer has a listener that fails
      1. WHEN I publish an event
         1. THEN my plugins still receive it
6. Warns about a failing plugin
   1. GIVEN Developer has a plugin that fails
      1. WHEN I publish an event
         1. THEN the stratum logger holds one warning that names the plugin and the key
7. Removes a plugin by name
   1. GIVEN Developer removes a plugin by its name
      1. WHEN I publish an event
         1. THEN that plugin receives nothing more
8. Closes a listener
   1. GIVEN Developer closes the handle a listener was added with
      1. WHEN I publish an event
         1. THEN that listener hears nothing more
9. Survives a failing plugin and a failing logger
   1. GIVEN Developer has a plugin that fails
   2. GIVEN a logger that fails too
      1. WHEN I publish an event
         1. THEN my other plugins still receive it
