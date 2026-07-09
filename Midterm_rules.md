# Midterm Blackjack Rules

This document describes the baseline behavior for the partial Blackjack CLI used in the midterm.

The baseline is not full Blackjack. It is a working, simplified game used for refactoring practice.

## Cards And Deck

- The game uses one standard 52-card deck.
- Cards have a rank and suit.
- Number cards count as their number.
- Face cards count as `10`.
- Aces may count as `11` or `1`, whichever keeps the hand from busting when possible.
- The baseline may use a deterministic deck order in tests.

## Round Flow

- One human player plays against one dealer.
- The player and dealer each receive two cards at the start of a round.
- The player chooses `hit` or `stand`.
- A `hit` deals one card to the player.
- A `stand` ends the player phase and starts the dealer phase.
- Invalid-input behavior is part of the baseline. Characterize it with tests before refactoring code that may affect it.

## Dealer Behavior

- The dealer draws until the dealer hand value is at least `17`.
- The baseline treats all dealer `17` values as stand values.
- No advanced soft-17 variant is required in the baseline.

## Outcomes

- A hand value over `21` is a bust.
- If the player busts, the dealer wins.
- If the dealer busts and the player does not, the player wins.
- If neither busts, the higher hand value wins.
- Equal hand values are a push.

If the starter code uses a simpler or awkward outcome rule, preserve that behavior during the midterm unless you document and test an intentional behavior change.

## Intentionally Missing Rules

These rules are intentionally left out of the midterm baseline:

- blackjack payout rules
- betting or bankroll tracking
- split
- double down
- surrender
- insurance
- multiple human players
- multiple-deck shoe rules
- shuffle penetration or reshuffle rules
- side bets
- tournament scoring

These missing rules are suitable final-project extension scope.

## Known Simplifications

The baseline may contain simplified or awkward behavior, such as:

- command parsing tied directly to console input
- hand display mixed with game rules
- hand-value calculation repeated in several branches
- no distinction between invalid commands and unavailable actions
- invalid input causing an immediate stand or round loss
- no persistent round history

Do not silently fix these behaviors during the midterm. First characterize them with tests, then refactor while preserving behavior.
