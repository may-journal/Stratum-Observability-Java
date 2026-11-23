### 2026.11.23.1420

- Fix: a plugin or listener that throws no longer stops the other plugins from receiving the event
- Fix: `publish` no longer throws a plugin's or listener's failure into the app's code
- Feat: each failure is logged as a warning on the `stratum` logger, with the plugin's name and the event key
