# 0012 Reporting a failing plugin

## Why

I publish events, and my other plugins still receive them when a plugin and the logger both fail.

## Measurement

plugins that receive the event while a plugin and the logger fail

-

plugins

## Requirements

1. Survives a failing plugin and a failing logger
   1. GIVEN Developer has a plugin that fails
   2. GIVEN Developer has a logger that fails too
      1. WHEN I publish an event
         1. THEN my other plugins still receive it
