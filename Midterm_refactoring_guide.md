# Blackjack Refactoring Guide

Start with behavior preservation.

## Suggested Order

1. Enter `starter-blackjack-cli/`.
2. Compile and run the game with `./scripts/run.sh`.
3. Run the existing checks with `./scripts/test.sh`.
4. Read `Midterm_rules.md`.
5. Add characterization checks around one hand-value behavior.
6. Extract command parsing from the round loop.
7. Extract deck access helpers.
8. Centralize hand-value calculation.
9. Centralize outcome detection.
10. Separate hand rendering from rule decisions.
11. Separate player/dealer phases from card dealing.
12. Add checks around bust and push detection.
13. Document one future extension point.

## Useful Refactorings

- Extract Method
- Extract Class
- Move Method
- Split Phase
- Replace Conditional with Polymorphism
- Introduce Parameter Object
- Replace Primitive With Object

## Good First Tests

Useful characterization tests include:

- a new deck has 52 cards
- face cards count as 10
- an ace counts as 11 when it does not bust the hand
- an ace counts as 1 when 11 would bust the hand
- a player hit adds one card
- a hand over 21 busts
- the dealer draws below 17
- equal player and dealer values produce a push

## Design Targets

Your refactored design should make these responsibilities easier to find:

- deck representation
- hand representation
- command parsing
- hand-value calculation
- bust detection
- dealer drawing rules
- round outcome detection
- round state
- CLI rendering and prompts

Do not introduce abstractions only to use pattern names. Use smaller classes or methods only when they make the current behavior easier to test and change.
