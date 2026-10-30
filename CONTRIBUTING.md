# Contributing

Every change starts from an issue. A change in behavior needs an approved Plan issue, and its pull request closes that Plan.

Contributions are licensed under the Apache License 2.0, as the rest of the project.

## Build and test

You need a JDK 21 or later and Maven 3.9 or later.

```sh
mvn -q verify
```

`mvn verify` compiles every module and runs one JUnit class per [requirement doc](docs/requirements). It then holds JaCoCo coverage at 100%, Checkstyle at its thresholds and Spotless at google-java-format. Each test starts a real `Stratum`, publishes as an app does, and reads what the plugin or listener received.

## Checks

The org's Fitness suite runs on every pull request. Run it locally through the shared hooks, as the [Fitness CI guide](https://github.com/may-journal/fitness-runner/blob/main/docs/ci.md) describes:

```sh
git config --local core.hooksPath .fitness-hooks
"$HOME/.local/share/may-journal/fitness/fitness-install" --version latest
```

Checkstyle reads `config/checkstyle/checkstyle.xml`. Format the code with Spotless:

```sh
mvn -q spotless:apply
```

## Pull requests

- Add one `changelog/<yyyy-mm-dd-HHMM>-<change>.md` entry and link it from the description.
- Name the agent that wrote the change in the description, such as Vanderbot Claude. `cspell.json` knows each agent name.
- Change a requirement doc and its test together.
- Keep the architecture views in step with the sources they name.
