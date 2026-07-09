# Final Project: Complete Blackjack Product

## Context

The final project continues your Blackjack work from the midterm.

This final project combines two kinds of work:

- persistence and statistics
- fuller Blackjack rules and product quality

Your goal is to turn the refactored partial Blackjack CLI into a more complete, maintainable application.

## Point Value

The final project is worth:

- `50` points

The first `40` points count as Final Project points. Any points above `40` count as Homework points.

There are no soft-deadline or early-submission bonus points in this final project rubric.

## Main Goal

Deliver a working Blackjack project with:

- fuller Blackjack rules
- tested game logic
- playable CLI flow
- persistent session and round history
- useful statistics or reports
- clear rule organization
- usable documentation

## Rule Feature Menu

Implement the game as close to normal Blackjack rules as reasonable for this course project.

A local rule reference is provided in `Final_Project_Blackjack_rules_reference.md`.

You earn points for each rule feature you implement well. No single rule feature is a prerequisite for submitting the project.

- correct deck composition and shuffle/deal flow
- hand-value calculation with aces
- player actions: hit and stand
- dealer drawing rules
- bust, win, loss, push, and natural blackjack outcomes
- betting or score/bankroll tracking
- blackjack payout behavior
- double down
- split
- surrender
- shoe or reshuffle behavior
- session history or replay-style reporting

## Persistence And Statistics

Use a Java ORM or structured persistence framework to persist Blackjack session and round history.

Allowed tools include:

- MyBatis
- Hibernate or JPA
- Spring Data JPA
- jOOQ
- another Java ORM-like persistence mapper, if approved before submission

Your persistence model should support:

- players
- sessions or games
- rounds
- hands or cards dealt
- player actions
- bets, scores, or bankroll changes if your game supports them
- round outcomes
- timestamps

Use a database suitable for local development and testing.

Acceptable examples:

- H2
- SQLite
- PostgreSQL
- MySQL or MariaDB

Implement at least three query or report features, such as:

- list recent sessions or rounds
- show player win count
- show player push/loss count
- show highest final bankroll or score
- show blackjack count by player
- show average rounds per session

These may be exposed through CLI commands, menu options, or a documented report mode.

## Product Quality

### 1. Game Architecture

Game rules and state should be testable without console input.

The CLI should not be the only place where rules exist.

### 2. CLI Playability

The game should be playable from the command line without needing to understand the source code.

### 3. Tests

Include tests for:

- deck composition and dealing
- hand values and ace handling
- hit and stand flow
- dealer drawing behavior
- bust, win, loss, and push outcomes
- natural blackjack behavior
- advanced actions you implement
- persistence behavior
- statistics or report queries

## Deliverables

Submit:

- source code
- tests
- ORM or persistence configuration
- schema or migration/setup script
- query/report implementation
- `README.md`
- `docs/database.md`
- `docs/rules-supported.md`
- `docs/final-report.md`

## Final Report

`docs/final-report.md` should explain:

- what Blackjack rules are implemented
- how the game is played from the CLI
- how the architecture separates game logic from CLI interaction
- what persistence approach and database are used
- what statistics or reports are available
- what tests were added
- what limitations remain

`docs/rules-supported.md` should list which rules from `Final_Project_Blackjack_rules_reference.md` are implemented and which variants or simplifications are used.

`docs/database.md` should explain:

- selected database
- selected ORM or persistence framework
- schema setup
- how to run persistence tests
- how to view session history or statistics

## Constraints

- Do not replace the project with an unrelated game.
- Do not require undocumented machine-specific setup.
- Do not hide failing tests.
- Do not store only plain text logs and call that persistence.
- Do not put database credentials directly in source code.
- Do not require a manually preconfigured private database without documentation.
- Do not remove existing build, logging, Docker, or test functionality from the midterm.

