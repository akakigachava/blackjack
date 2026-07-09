# Midterm Technical Rubric

This document explains how your Blackjack midterm project will be graded.

The midterm is worth:

- `40` grading points

There are no soft-deadline or early-submission bonus points in this rubric.

## Point Categories

### 1. Behavior Preservation And Characterization Tests: 8 points

To score strongly here:

- Your tests cover important existing behavior before risky refactoring.
- Your tests describe this implementation, including quirks.
- Your tests cover deck setup, hand values, ace handling, player actions, dealer behavior, and round outcomes.
- Your tests are focused and readable.
- Your tests can be run with a simple command.

You lose points here when:

- Some tests exist, but major rule behavior is untested.
- Tests are too broad, fragile, or hard to understand.
- Tests are added only after refactoring.
- Tests assert desired full Blackjack behavior instead of current baseline behavior.
- Refactoring changes behavior without test evidence.

### 2. Incremental Refactoring Discipline: 7 points

To score strongly here:

- Your work is split into small behavior-preserving steps.
- Each step has a clear purpose.
- Refactoring and behavior changes are not mixed.
- The final design is easier to reason about than the original.

You lose points here when:

- Refactoring steps are too large or poorly explained.
- Unrelated cleanup is mixed into the assignment.
- Behavior preservation is mostly assumed rather than demonstrated.
- The project is rewritten instead of refactored.
- The refactoring is cosmetic only.

### 3. Design Improvement: 8 points

To score strongly here:

- Deck, hand, scoring, and round rules are separated from console input/output.
- Duplicated hand-value and outcome logic is reduced.
- Card, deck, hand, or round behavior has a clearer home.
- At least one rule can be tested without running the full CLI game.
- The design moves toward MVC-like separation without superficial naming.

You lose points here when:

- Some responsibilities are extracted, but important coupling remains.
- MVC names appear, but responsibilities are only partially separated.
- Duplication is reduced in one area but remains central elsewhere.
- Classes are created without meaningful responsibility.
- Patterns are applied mechanically.
- Console, rules, parsing, and state remain essentially tangled.

### 4. Build Tool And Test Integration: 5 points

To score strongly here:

- The project uses Maven or Gradle.
- The build tool can compile/build the project.
- The build tool can run the test suite.
- The build tool can package the application.
- The README documents exact build, test, package, and run commands.

You lose points here when:

- There is no Maven or Gradle project file.
- Tests require manual classpath setup.
- Build commands are undocumented or do not work.
- Packaging is missing.
- Scripts hide build failures instead of failing clearly.

### 5. Logging And Docker: 5 points

To score strongly here:

- The game uses a normal Java logging approach.
- Logs include game start, round start, card dealt, player action, dealer action, invalid input, and round end.
- Logging does not replace normal player-facing CLI output.
- A `Dockerfile` builds and starts the game.
- Docker build and run commands are documented.
- Docker does not depend on files outside the repository.

You lose points here when:

- Important events are not logged.
- Logs contain sensitive local paths or machine-specific data.
- Docker setup is missing or undocumented.
- Docker depends on external files.
- Docker build or run commands fail.

### 6. Code Quality: 4 points

To score strongly here:

- Names are clear.
- Methods and classes have understandable responsibilities.
- The code avoids unnecessary cleverness.
- The solution fits the scale of the project.

You lose points here when:

- Some methods remain too large or unclear.
- The code works but still requires too much mental tracking.
- Code is brittle, obscure, or overengineered.
- The design adds more complexity than it removes.
- Important behavior is hidden behind unclear abstractions.

### 7. Report And Extension Readiness: 3 points

To score strongly here:

- Your refactoring report clearly explains the refactoring path, preserved behavior, and remaining risks.
- Your extension-readiness note identifies a realistic extension point and explains where the design still resists change.

You lose points here when:

- The refactoring report summarizes changes but does not explain tradeoffs.
- The extension-readiness note is vague.
- Remaining risks are incomplete.
- Claims are not supported by code or tests.
- Extension readiness is not addressed.

## Submission Packaging

Packaging problems are considered separately from refactoring quality.

Examples of packaging problems:

- wrong folder structure
- top-level scripts wired to the wrong source tree
- required docs present under nonstandard filenames or nested paths
- refactored code present in the repository but not connected to the default compile/run path

These problems can reduce your score when they make your work hard to run, verify, or understand. However, a small packaging problem is not the same as missing refactoring work.

If your refactored code is present but the default scripts point elsewhere, make the correct source location clear in your documentation. If your tests can be run directly, document the exact command.

## Point Calculation

```text
Behavior Preservation And Characterization Tests /8
+ Incremental Refactoring Discipline /7
+ Design Improvement /8
+ Build Tool And Test Integration /5
+ Logging And Docker /5
+ Code Quality /4
+ Report And Extension Readiness /3
= Midterm grade /40
```

## Important Scope Note

The goal is to preserve the partial baseline behavior, improve the design, make the project buildable and runnable in a standard way, and make later rule expansion plausible.

Do not treat the midterm as a full Blackjack implementation task. Extra rules help only if the required refactoring, characterization, infrastructure, and documentation work is already strong and the new behavior is clearly tested and explained.
