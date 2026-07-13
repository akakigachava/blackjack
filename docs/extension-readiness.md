# Extension Readiness

## Which extension the design supports best

Betting / bankroll tracking. The design now has a single place where a
round is played (`Game`) and a single place where the result of a round
becomes known (`game.outcome()` in `Main`'s loop). A bankroll feature slots
in without touching card, hand, or dealer logic:

- a new `Bankroll` class in the `blackjack` package holding the balance and
  applying a round result to a bet;
- a bet prompt before `game.startRound()` and a balance line after the
  outcome, both added to `ConsoleView`;
- one call in `Main`'s loop connecting the round outcome to the bankroll.

Replacing or improving the CLI view is nearly as easy: every print goes
through `ConsoleView`, so a different view (colors, another language, a
test double that records output) is one class swap.

## Where the change would be implemented

- `starter-blackjack-cli/src/main/java/blackjack/` — new `Bankroll` class,
  small additions to `ConsoleView`.
- `starter-blackjack-cli/src/main/java/Main.java` — prompt for a bet and
  settle it after the round.
- Tests can drive `Bankroll` and `Game` directly, with no console, using
  the fixed-order `Deck(String...)` constructor to force wins, losses, and
  pushes.

## What still makes change difficult

- `Main` plays exactly one round per run. Bankroll tracking is only
  meaningful across rounds, so the loop would need a play-again step —
  a small but real change to the characterized CLI behavior.
- The deterministic deck means "betting strategy" is trivially exploitable
  until shuffling is added; shuffling in turn requires the order-dependent
  tests to inject fixed-order decks (or a seeded shuffle, once a
  constructor that takes a random seed actually exists).

One earlier obstacle is gone: since the rework, `Rules.determineOutcome`
returns an `Outcome` enum (`PLAYER_WINS`, `DEALER_WINS`, `PUSH`) and
`ConsoleView` owns the exact display strings, so betting payouts can switch
on the enum instead of on presentation text.
