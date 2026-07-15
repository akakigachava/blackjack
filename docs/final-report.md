# Final Project Report

## What Blackjack rules are implemented

The game now plays a recognizable single-deck Blackjack session: a
shuffled deck with reshuffle-when-low shoe behavior, bets placed before
each deal, hit/stand/double down/surrender as player actions, dealer
standing on all 17s, natural blackjack resolved immediately after the
deal and paying 3:2, and a 100-chip bankroll that carries across rounds
until the player quits or goes broke. The full list with variants and
simplifications (no split, no insurance) is in `docs/rules-supported.md`.

## How the game is played from the CLI

```bash
cd starter-blackjack-cli
./mvnw package
java -jar target/blackjack-cli-1.0.0.jar --player Akaki
```

Each round asks for a bet (`enter` bets 10, `q` quits), deals the hands
with the dealer's first card hidden, and accepts `hit`, `stand`,
`double`, `surrender`, or `q`. After the round the result and the new
chip balance are shown and the next bet prompt appears. `--stats` prints
the history reports instead of playing. `scripts/run.sh` and Docker
(`docker run -it --rm blackjack-cli`) run the same game.

## How the architecture separates game logic from CLI interaction

- `Card`, `Deck`, `Hand`, `Rules`, `Outcome`, `Bankroll`, and `Game` hold
  all game state and rule behavior; none of them reads input or prints.
  All are exercised directly by unit tests without any console.
- `ConsoleView` owns every player-facing string (including the mapping
  from the `Outcome` enum to display text); `ReportView` renders the
  statistics reports; `Command.parse` owns input parsing.
- `Main` is the application loop: it wires views, scanner, bankroll, and
  the session recorder together and contains no rule arithmetic.
- The persistence package (`blackjack.persistence`) is reached only
  through `HistoryRepository` and `SessionRecorder`; game classes have no
  knowledge of the database, and if the database is unavailable the
  recorder becomes a no-op and the game still plays.

## Persistence approach and database

MyBatis 3.5 (annotated mappers, programmatic configuration, no XML) over
embedded H2. The schema (`src/main/resources/db/schema.sql`, applied
automatically at startup) has four tables: `players`, `sessions`,
`rounds` (hands, values, outcome, bet, bankroll change and balance,
timestamp), and `round_actions` (the ordered player decisions). Settings
come from system properties or environment variables with local-file
defaults, so no credentials live in source. Details in
`docs/database.md`.

## Statistics and reports

`java -jar target/blackjack-cli-1.0.0.jar --stats` prints five reports:
recent sessions with round counts, per-player win/loss/push totals with
blackjack and surrender counts, the highest bankroll each player has
reached, average rounds per session, and a replay-style list of recent
rounds with hands, bets, and outcomes.

## Tests

61 JUnit tests in two suites, plus the legacy baseline checks from the
starter:

- deck composition, deterministic fixed-order decks, shuffle uniqueness
  and seed reproducibility, reshuffle-when-low and reshuffle-on-empty
- hand values with aces, natural detection, the null-card contract
- player flow (hit/stand/double/surrender parsing and first-action
  restrictions), dealer draw thresholds including soft 17
- outcomes including naturals, payout rates, bankroll arithmetic
- full CLI rounds through the real `Main` with a seeded deck
  (`-Dblackjack.deck.seed`) covering betting, naturals both ways,
  surrender, double down, bust, invalid input, multi-round sessions, and
  quitting — with output captured and asserted
- persistence round-trips and all five report queries on isolated
  in-memory databases, plus recorder no-op degradation

`./mvnw test` runs everything; `scripts/test.sh` adds the legacy checks.

## Limitations

- No split or insurance (documented in `docs/rules-supported.md`).
- One human player per session; the dealer is the only opponent.
- The embedded H2 file allows a single process at a time, so `--stats`
  cannot run while a game is open.
- CLI round tests pin specific shuffle seeds; if the shuffle algorithm
  ever changes (e.g. a JDK change to `Collections.shuffle`), the seeds
  would need to be rediscovered.
- The bankroll resets to 100 each session; there is no cross-session
  carry-over (history keeps every session's results, though).
