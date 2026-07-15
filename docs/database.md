# Database And Persistence

## Selected database

H2 (embedded, file-based). It is pure Java, requires no installation or
server process, works identically on every development machine and inside
the Docker image, and supports an in-memory mode that the tests use for
isolation.

By default the game writes to `./data/blackjack.mv.db` (created on first
run, relative to the directory the game is started from). The `data/`
directory is gitignored.

## Selected persistence framework

MyBatis (3.5.x), configured programmatically in
`src/main/java/blackjack/persistence/Database.java` — no XML configuration.

- All SQL lives in annotated mapper interfaces:
  `PlayerMapper`, `SessionMapper`, `RoundMapper` (writes) and
  `StatsMapper` (report queries).
- Game code never sees SQL. `Game`, `Rules`, and `Main` talk only to
  `HistoryRepository` (the repository facade) and `SessionRecorder`
  (records one CLI session; degrades to a no-op if the database cannot be
  opened, so the game always stays playable).

## Connection configuration

Settings are read from system properties first, then environment
variables, then defaults — no credentials live in source code:

| System property         | Environment variable    | Default                        |
|-------------------------|-------------------------|--------------------------------|
| `blackjack.db.url`      | `BLACKJACK_DB_URL`      | `jdbc:h2:file:./data/blackjack` |
| `blackjack.db.user`     | `BLACKJACK_DB_USER`     | `sa` (H2 embedded default)     |
| `blackjack.db.password` | `BLACKJACK_DB_PASSWORD` | empty                          |

## Schema setup

The schema is applied automatically at startup from
`src/main/resources/db/schema.sql` (every statement is
`CREATE TABLE IF NOT EXISTS`, so reruns are harmless). No manual setup
step is needed.

Tables:

- `players` — id, unique name, created_at
- `sessions` — id, player_id, started_at, ended_at (one row per program run)
- `rounds` — id, session_id, round_number, player_cards, dealer_cards,
  player_value, dealer_value, outcome (the `Outcome` enum name), bet,
  bankroll_change, bankroll_after, played_at
- `round_actions` — id, round_id, seq, action (the player's
  HIT/STAND/DOUBLE/SURRENDER decisions in order)

Databases created before betting existed are upgraded in place by
`ALTER TABLE ... ADD COLUMN IF NOT EXISTS` statements in the same script
(old rounds show a bet of 0).

## How to run the persistence tests

```bash
cd starter-blackjack-cli
./mvnw test
```

Persistence coverage lives in
`src/test/java/BlackjackPersistenceTest.java`. Every test opens its own
throwaway in-memory H2 database (`jdbc:h2:mem:...`), so the tests never
read or write the real game history and do not depend on any
machine-specific state. The CLI round tests in the characterization suite
also redirect the game to an in-memory database for the same reason.

## How to view session history and statistics

Play rounds first (they are recorded automatically):

```bash
java -jar target/blackjack-cli-1.0.0.jar --player Akaki
```

Then run the report mode:

```bash
java -jar target/blackjack-cli-1.0.0.jar --stats
```

It prints five reports from the persisted history:

1. recent sessions (player, start/end time, rounds played)
2. player results (win/loss/push counts, blackjacks, and surrenders)
3. highest bankroll each player has reached
4. rounds per session (totals and average per player)
5. recent rounds (hands, values, outcome, bet, and bankroll change,
   newest first)

For ad-hoc SQL, the H2 shell works against the same file (close the game
first; the embedded database allows one process at a time):

```bash
java -cp ~/.m2/repository/com/h2database/h2/2.2.224/h2-2.2.224.jar \
  org.h2.tools.Shell -url jdbc:h2:file:./data/blackjack -user sa -password ""
```

## Limitations

- The embedded file database supports a single process at a time; running
  the game and the H2 shell simultaneously will fail with a lock error.
- Quitting with `q` records the session but not an unfinished round (an
  aborted round has no outcome to store, and the bet is returned).
