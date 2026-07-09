# Final Project Rubric

## Point Value

The final project is worth:

- `50` points

The first `40` points count as Final Project points. Any points above `40` count as Homework points.

There are no soft-deadline or early-submission bonus points in this rubric.

## 1. Fuller Blackjack Rules Implementation: 22 points

### 1.1 Deck, Dealing, And Hand Values: 4 points

- standard deck composition is correct
- shuffle/deal flow works
- face-card values are correct
- ace handling works with one or more aces
- behavior is tested

### 1.2 Player And Dealer Flow: 4 points

- player can hit and stand
- dealer reveals hidden card at the correct time
- dealer draws until the documented threshold
- invalid commands are handled without crashing
- behavior is tested

### 1.3 Outcomes And Natural Blackjack: 4 points

- player bust is handled
- dealer bust is handled
- win, loss, and push are handled
- natural blackjack is distinguished from later 21
- outcome behavior is tested

### 1.4 Betting, Score, Or Bankroll Tracking: 3 points

- the game tracks a score, stake, or bankroll across rounds
- outcomes update that value consistently
- blackjack payout behavior is implemented or clearly documented as simplified
- score/bankroll behavior is tested

### 1.5 Advanced Player Actions: 5 points

Credit may come from implemented and tested support for:

- split
- double down
- surrender
- insurance
- multiple hands after split

Partial implementation should be documented in `docs/rules-supported.md`.

### 1.6 Shoe, Session, And Round-End Rules: 2 points

- reshuffle or shoe behavior is documented
- sessions can include multiple rounds
- round-end behavior is clear
- long-session limitations are documented

## 2. Persistence And Statistics: 12 points

### 2.1 Database Schema: 3 points

- schema supports players
- schema supports sessions or games
- schema supports rounds and outcomes
- schema supports hands, actions, score/bankroll changes, and timestamps where applicable

### 2.2 ORM Or Persistence Mapping: 3 points

- a Java ORM or structured persistence mapper is configured
- persistence code uses repository, DAO, mapper, or equivalent classes
- game logic does not contain raw SQL directly
- configuration is documented

### 2.3 Persist Session And Round Results: 3 points

- player names are persisted
- session or round timestamp is persisted
- player/dealer hands or enough round detail are persisted
- outcome and score/bankroll changes are persisted where applicable

### 2.4 Query Features And Persistence Tests: 3 points

- at least three useful query/report features are implemented
- queries are available through CLI commands, menu options, or documented report mode
- persistence tests use an isolated local or test database
- tests do not depend on private machine state

## 3. Architecture And Product Quality: 8 points

### 3.1 Game Design And Rule Organization: 4 points

- game logic is testable without console input
- CLI is separated from rule execution
- rule behavior has a clear home
- packages/classes have clear responsibilities

### 3.2 CLI Playability: 2 points

- players can complete a normal session from the CLI
- prompts and output are understandable
- invalid input is handled without crashing

### 3.3 Build, Logging, And Docker Preservation: 2 points

- build and test commands still work
- logging still records important game events
- Docker still builds and starts the game
- final work does not break midterm infrastructure

## 4. Tests: 4 points

- tests cover deck, deal, and hand-value behavior
- tests cover player and dealer flow
- tests cover outcomes and advanced actions you implement
- tests cover persistence and query/report behavior

## 5. Documentation And Final Report: 4 points

- `README.md` includes exact build, test, run, package, Docker, and report commands
- `docs/rules-supported.md` lists implemented and missing rules
- `docs/database.md` explains persistence setup and usage
- `docs/final-report.md` is clear, evidence-based, and explains limitations

## Point Calculation

```text
Fuller Blackjack Rules Implementation /22
+ Persistence And Statistics /12
+ Architecture And Product Quality /8
+ Tests /4
+ Documentation And Final Report /4
= Final project grade /50
```

## Reporting Split

```text
Final Project points: min(final project grade, 40) /40
Homework points from final project: max(final project grade - 40, 0) /10
```

## Important Scope Note

The final project is not only a rule-extension task and not only a persistence task. Strong submissions combine fuller Blackjack rules, persistence/statistics, tests, CLI playability, and clear documentation into one coherent project.

