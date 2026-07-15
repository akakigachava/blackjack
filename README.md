# Blackjack Assignment Track

This folder contains the Blackjack version of the midterm-to-final project track.

The track uses this structure:

- midterm: refactor a working but messy partial CLI game and add basic project infrastructure
- final project: add persistence/statistics and extend the game toward fuller rules and a more complete product

The midterm baseline is intentionally incomplete. It is playable enough to refactor and test, but it leaves meaningful rule and persistence work for the final project.

Current files:

- `Midterm_assignment.md`
- `Midterm_rules.md`
- `Midterm_rubric.md`
- `Midterm_refactoring_guide.md`
- `Final_Project.md`
- `Final_Project_Blackjack_rules_reference.md`
- `Final_rubric.md`
- `starter-blackjack-cli/` — the game (now a Maven project)
- `docs/refactoring-report.md` — midterm refactoring report
- `docs/extension-readiness.md` — midterm extension-readiness note
- `docs/database.md` — final project persistence setup and usage
- `docs/rules-supported.md` — which Blackjack rules are implemented
- `docs/final-report.md` — final project report

## Requirements

- JDK 17 or newer (a full JDK — `javac` must be available). Maven itself is not
  required: the project ships the Maven Wrapper (`mvnw`), which downloads Maven
  automatically on first use.
- Docker (only for the Docker commands).

All commands below are run from the project directory:

```bash
cd starter-blackjack-cli
```

## Build

```bash
./mvnw compile
```

## Test

```bash
./mvnw test
```

Or run everything (Maven tests plus the legacy starter checks) with:

```bash
./scripts/test.sh
```

## Run

```bash
./mvnw compile exec:java
```

Or:

```bash
./scripts/run.sh
```

Each round asks for a bet first (`enter` bets 10, `q` quits). Round
commands: `hit`, `stand`, `double`, `surrender`, `q`. The session starts
with 100 chips and runs until you quit or go broke. See
`docs/rules-supported.md` for the exact rules.

## Package

```bash
./mvnw package
```

This produces `target/blackjack-cli-1.0.0.jar`, runnable with:

```bash
java -jar target/blackjack-cli-1.0.0.jar
```

The JAR is self-contained (dependencies bundled). Optional flags:

```bash
java -jar target/blackjack-cli-1.0.0.jar --player Akaki   # play under a name
java -jar target/blackjack-cli-1.0.0.jar --stats          # show history reports
```

## Docker build

```bash
docker build -t blackjack-cli .
```

## Docker run

```bash
docker run -it --rm blackjack-cli
```

`-it` is required because the game reads player commands from stdin.

## Logging

Game events (game start, round start, cards dealt, player and dealer actions,
invalid input, round end) are logged to `blackjack.log` in the working
directory. Logs do not appear in the player-facing CLI output.

## Persistence and statistics

Completed rounds are recorded automatically to an embedded H2 database
(`./data/blackjack.mv.db`, created on first run) through MyBatis. Play under
a name with `--player <name>` (default: `Player`), then view the recorded
history with:

```bash
java -jar target/blackjack-cli-1.0.0.jar --stats
```

This prints recent sessions, win/loss/push counts (with blackjacks and
surrenders), highest bankrolls, rounds per session, and a replay-style
list of recent rounds with bets. Connection settings, schema, and
persistence-test details are documented in `docs/database.md`.

## Notes

- If `JAVA_HOME` points to a JRE without a compiler, `./mvnw` fails with
  "release version 17 not supported". Export `JAVA_HOME` to a full JDK, or use
  the `scripts/*.sh` wrappers: they validate an existing `JAVA_HOME` (failing
  fast if it lacks `bin/javac`), ask `/usr/libexec/java_home` on macOS, or
  derive a JDK home from the `javac` on `PATH` when it resolves to a real JDK.
  If no JDK can be found they print a setup error instead of guessing.
- The baseline game intentionally uses an unshuffled, deterministic deck and a
  partial rule set. See `Midterm_rules.md` for the documented quirks and
  `docs/refactoring-report.md` for how they were preserved.
