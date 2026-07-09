# Midterm Exam: Refactor A Working Partial Blackjack CLI

## Context

You are given a working CLI Blackjack-like game.

The game is playable, but it is intentionally incomplete and poorly structured. It implements only a partial version of Blackjack so that later assignments and the final project have meaningful extension scope.

The code is written in a procedural, feature-grown style:

- one large game class or controller
- card and hand state stored in primitive arrays or strings
- duplicated hand-value and outcome checks
- console input/output mixed with game rules
- round flow, parsing, dealer logic, and outcome detection mixed together
- special cases handled through nested conditionals

Your job is not to rewrite the game. Your job is to make it safer and easier to change through characterization tests and incremental refactoring.

## Main Goal

Refactor the existing partial Blackjack game toward a clearer design while preserving its current behavior.

Strong solutions move toward an MVC-like separation:

- deck, hand, scoring, and round rules can be tested without the console
- console rendering and prompts are separated from rule execution
- command parsing and round orchestration are not tangled with scoring rules

Do not create classes named `Model`, `View`, or `Controller` just to satisfy the phrase MVC. Naming is not evidence of design. Responsibilities and tests are.

## Baseline Rule Scope

The starter game supports only a partial Blackjack rule set:

- one human player against one dealer
- one standard 52-card deck
- initial deal of two cards to the player and two cards to the dealer
- player actions limited to `hit` and `stand`
- dealer draws until reaching at least 17
- basic ace handling as `1` or `11`
- round outcome as player win, dealer win, push, player bust, or dealer bust

The starter game does not implement the full game.

These features are intentionally left for later work, especially the final project:

- blackjack payout rules
- betting or bankroll tracking
- split
- double down
- surrender
- insurance
- multiple human players
- multiple-deck shoe rules
- shuffle penetration or reshuffle rules
- round history or replay features

## Required Work

### 1. Understand And Run The Game

From the repository root, enter the starter project:

```bash
cd starter-blackjack-cli
```

Run the current game:

```bash
./scripts/run.sh
```

Run the current checks:

```bash
./scripts/test.sh
```

Read the implemented rules:

- `Midterm_rules.md`

Do not assume official Blackjack rules are implemented. The midterm is about preserving and improving this implementation.

### 2. Add Characterization Tests

Before refactoring risky behavior, add tests that describe what the current system does.

Characterization tests are tests for existing behavior. Their purpose is to document how the current code behaves before you change its design. They are not tests for an ideal version of Blackjack.

If the current implementation has a quirk, your tests should record that quirk. For example:

- if ace handling is simplified, test that current behavior
- if the dealer stands on all 17s, test that current behavior
- if an invalid command ends the round or is ignored, test that current behavior
- if card order is deterministic in tests, test the behavior that currently exists

These tests protect the game while you extract methods, clarify responsibilities, and rename code. Refactoring is successful only if the characterized behavior still works afterward, unless a behavior change is explicitly documented in your report and covered by tests.

Your tests should cover at least these behaviors:

- deck creation
- initial deal
- hand-value calculation
- ace handling
- player hit
- player bust
- dealer draw behavior
- round outcome detection
- at least one input or edge-case behavior that surprised you

Tests do not need to describe perfect Blackjack. They must describe this implementation.

### 3. Refactor Incrementally

Refactor in small steps. After each meaningful step, the checks should still pass.

Required refactoring outcomes:

- Extract deck, hand, and scoring behavior out of the main round loop.
- Reduce duplicated hand-value and outcome logic.
- Separate at least part of console input/output from game rule logic.
- Make at least one rule behavior testable without running the full CLI game.
- Improve names so responsibilities are easier to understand.

### 4. Preserve Existing Behavior

Unless explicitly documented in your report and covered by tests, the playable behavior should remain the same.

This includes quirks documented in `Midterm_rules.md`, such as:

- simplified scoring rules
- dealer behavior at 17
- no betting or bankroll
- no split or double down
- any existing invalid-input behavior

### 5. Prepare The Design For One Extension

Your design should make at least one of these possible extensions easier:

- add betting or bankroll tracking
- add split
- add double down
- add blackjack payout rules
- add multiple players
- add a round-history or replay log
- add a smarter dealer or strategy simulation
- replace or improve the CLI view

You do not need to implement the extension during the midterm. You must leave a design that makes such a change plausible.

### 6. Add Build Tool Support

Convert the project to use Maven or Gradle.

Your repository must include one of:

- `pom.xml`
- `build.gradle`
- `build.gradle.kts`

The project must support standard commands for:

- compiling/building the project
- running tests
- packaging the application
- running the application

### 7. Integrate Tests With The Build Tool

Your characterization tests must run through the build tool.

Examples:

- `mvn test`
- `gradle test`
- `./gradlew test`

Tests should not require manual classpath setup.

### 8. Add Logging

Add logging using a normal Java logging approach.

Acceptable examples:

- `java.util.logging`
- Logback
- Log4j
- SLF4J with a backend

Log at least these events:

- game start
- round start
- card dealt
- player action
- dealer action
- invalid input
- round end

Do not replace normal user-facing CLI output with logs. The CLI should still be readable for players.

### 9. Add Docker Support

Add a `Dockerfile` that builds and runs the application.

The Docker setup must allow the game to start from a documented command.

If your project uses a wrapper such as `mvnw` or `gradlew`, include the files needed for Docker to use it.

Docker must not depend on files outside the repository.

### 10. Update The README

Add or update `README.md`.

It must include exact commands for:

- local build
- local test
- local run
- package creation
- Docker build
- Docker run

## Constraints

Do not:

- rewrite the whole project from scratch
- replace the CLI game with a different game
- implement the full final-project rule set during the midterm
- introduce a large framework
- hide behavior changes inside refactoring commits
- hide build failures behind scripts that ignore errors
- apply design patterns mechanically
- delete behavior simply because it is awkward
- remove existing tests
- require IDE-specific steps
- log sensitive local paths or machine-specific data
- make Docker depend on files outside the repository

You may:

- add small classes
- add tests or test helpers
- rename methods and variables
- extract methods and classes
- introduce value objects
- introduce MVC-like boundaries
- document known limitations

## Deliverables

Submit:

- refactored source code
- characterization tests
- Maven or Gradle build configuration
- tests runnable through the build tool
- logging implementation
- `Dockerfile`
- `README.md` with exact local and Docker commands
- a short refactoring report in `docs/refactoring-report.md`
- a short extension-readiness note in `docs/extension-readiness.md`

The refactoring report should answer:

- What behavior did you characterize before refactoring?
- What were the worst design problems you found?
- Which refactorings did you perform?
- What behavior did you intentionally preserve?
- What risks remain?

The extension-readiness note should answer:

- Which extension would your design support best?
- Where would that change be implemented?
- What part of your design still makes change difficult?

## Submission Workflow

Submit your work through a pull request.

Use this workflow:

1. Fork the original repository from the instructor's GitHub account.
2. Clone your fork locally.
3. Create and switch to a branch named `midterm`.
4. Make your changes on the `midterm` branch.
5. Commit your work in meaningful steps.
6. Push the `midterm` branch to your GitHub fork.
7. Open a pull request from your `midterm` branch to the original repository.

Your pull request should include all required deliverables listed above.

## Suggested Workflow

1. Run the game manually.
2. Read the implemented rules.
3. Run existing checks.
4. Add characterization tests around one behavior.
5. Refactor one small area.
6. Rerun checks.
7. Repeat.
8. Write the report.

## Evaluation

See `Midterm_rubric.md`.
