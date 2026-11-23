### 2026.11.23.1455

- Fix: a logging backend that throws while a failure is reported no longer stops delivery
- Fix: a plugin whose `name()` throws no longer stops delivery to the other plugins
- Docs: requirement 0003.9 covers a plugin and a logger that fail together
