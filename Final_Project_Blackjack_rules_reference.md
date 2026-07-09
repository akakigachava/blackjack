# Final Project Blackjack Rules Reference

This document defines the Blackjack rules used for the final project.

If your implementation uses a variant or simplification, document it in `docs/rules-supported.md`.

## Deck And Cards

- The game uses at least one standard 52-card deck.
- Cards have rank and suit.
- Number cards count as their number.
- Face cards count as `10`.
- Aces count as `11` when possible and `1` when needed to avoid busting.
- If you use multiple decks, document the number of decks.
- If you use deterministic deck order for tests, keep that separate from normal gameplay shuffle behavior.

## Round Setup

- Each round has one dealer and at least one player.
- The player and dealer each receive two cards.
- The dealer has one visible card and one hidden card during the player decision phase.
- The player acts before the dealer unless the round ends immediately because of a natural blackjack rule.

## Player Actions

At minimum, the final project should support:

- hit
- stand

Additional actions may include:

- double down
- split
- surrender
- insurance

If you implement only some advanced actions, document which ones are supported.

## Dealer Rules

- The dealer draws until reaching at least `17`.
- The dealer then stands.
- Document whether the dealer hits or stands on soft 17.

## Hand Values

- A hand over `21` busts.
- A hand may contain multiple aces.
- Ace adjustment should continue until the hand is no longer busting or no ace can be adjusted.

## Outcomes

At minimum, the game should handle:

- player bust
- dealer bust
- player win
- dealer win
- push
- natural blackjack

If betting or score/bankroll tracking is implemented, outcomes should update the score or bankroll consistently.

## Blackjack And Payouts

- A natural blackjack is an ace plus a ten-value card as the first two cards.
- Natural blackjack should be distinguished from a later 21 made with more cards.
- If payout behavior is implemented, document the payout ratio.
- If payout behavior is simplified or omitted, document that limitation.

## Split

If split is implemented:

- splitting is available when the first two cards have the same rank or value, depending on your documented rule
- split hands are played separately
- each split hand receives an additional card
- outcomes are recorded per hand

## Double Down

If double down is implemented:

- the player's stake or score risk increases according to your documented rule
- the player receives exactly one additional card
- the player then stands automatically

## Surrender

If surrender is implemented:

- the player may end the round early
- the score or bankroll effect is documented
- availability timing is documented

## Session History

The final project should preserve enough round information to support persistence or replay-style reporting.

Useful history fields include:

- session id
- round number
- player
- initial hands
- actions taken
- final player hand
- final dealer hand
- outcome
- score or bankroll change, if applicable
- timestamp or persisted round order

