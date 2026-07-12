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
  the seedable `Deck` constructor to force wins, losses, and pushes.

## What still makes change difficult

- `Rules.determineOutcome` returns display strings (`"Player wins"`), so
  rule results and presentation are still coupled. Betting payouts would
  have to switch on those strings; an `Outcome` enum with the view mapping
  enum values to text is the natural next refactoring, but it was left out
  of the midterm because it changes behavior-adjacent code the tests pin
  down as exact strings.
- `Main` plays exactly one round per run. Bankroll tracking is only
  meaningful across rounds, so the loop would need a play-again step —
  a small but real change to the characterized CLI behavior.
- The deterministic deck means "betting strategy" is trivially exploitable
  until shuffling is added; shuffling in turn requires the order-dependent
  tests to move to seeded decks.
