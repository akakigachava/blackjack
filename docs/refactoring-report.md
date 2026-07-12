# Refactoring Report

## What behavior was characterized before refactoring

Before touching the design, I wrote JUnit characterization tests
(`starter-blackjack-cli/src/test/java/BlackjackCharacterizationTest.java`)
that describe what the starter code actually does, including its quirks:

- Deck: 52 cards, built in a fixed rank-within-suit order (H, D, C, S) and
  never shuffled, so every game starts identically.
- Quirk: drawing from an exhausted deck returns a phantom `AH` forever
  without advancing the deck position.
- Initial deal: player, dealer, player, dealer — so the opening hands are
  always player `AH 3H` (14) vs dealer `2H 4H` (6).
- Hand values: number cards, face cards as 10, ace as 11 falling back to 1,
  two aces counting as 12.
- Player actions: `hit` adds exactly one card; over 21 is a bust.
- Dealer: draws below 17, stands on hard 17 and on soft 17, and can bust.
- Outcomes: exact strings `Player wins` / `Dealer wins` / `Push`, including
  the quirk that when both hands bust the dealer wins because the player
  bust check runs first.
- Full CLI rounds through the real `main` loop with captured input/output:
  stand, hit-then-stand (a deterministic push), hit-to-bust, quit, and the
  invalid-input quirk where any unrecognized command (including an empty
  line) forces an immediate stand and ends the round.

The original starter checks (`tests/BlackjackBaselineTest.java`) were kept
and still run from `scripts/test.sh`.

## Worst design problems found

- All state was global and mutable: static arrays and counters on `Main`,
  shared by every method.
- Primitive obsession: cards were strings, hands were `String[12]` arrays
  paired with separate count fields that had to be kept in sync manually.
- The hand-value calculation was invoked with array-plus-count pairs from
  four different call sites, and outcome text, rules, parsing, rendering,
  and round flow all lived in one class.
- Nothing could be tested without running the full CLI game, because rules
  and console I/O were tangled together.

## Refactorings performed (in commit order)

1. Converted the project to Maven with the Maven Wrapper, moving
   `Main.java` unchanged into the standard layout.
2. Added the characterization tests described above.
3. Extract Class: `Card` (immutable rank + suit value object) and `Deck`
   (owns the deterministic order, the draw position, and the phantom-`AH`
   quirk; a seedable constructor replaced the old trick of swapping in a
   raw array from tests).
4. Extract Class: `Hand` — replaced array-plus-count pairs and centralized
   the hand-value/ace calculation in one method (`Hand.value()`).
5. Extract Class: `Rules` — round outcome and the dealer's stand threshold
   (17) as a named constant instead of a magic number.
6. Split Phase: `Command.parse` (input parsing), `ConsoleView` (all
   printing), and `Game` (deck + hands + round actions). `Main` became a
   thin loop that wires them together and holds no game state.
7. Added `java.util.logging` with a file handler (`LogSetup`), logging game
   start, round start, every card dealt, player and dealer actions, invalid
   input, and round end — without changing player-facing output.

After each step the full test suite was rerun, and the CLI round tests
(which assert on the real program output) stayed green throughout.

## Behavior intentionally preserved

- The unshuffled, deterministic deck.
- The phantom `AH` from an exhausted deck.
- Invalid input (including an empty line) forcing an immediate stand.
- Dealer standing on all 17s, including soft 17.
- The both-bust ordering quirk (dealer wins).
- Exact output strings and their order, including the double table print
  when the player busts.
- One round per program run; `q`/`quit` stopping without an outcome.

## Intentional behavior change (documented)

One behavior was removed rather than preserved: the old `handValue` skipped
`null` entries inside the counted range of the hand array. That situation
was unreachable through gameplay — it could only be produced by externally
poking `Main`'s public arrays — and the array representation itself was
removed. The corresponding characterization test was deleted in the same
commit that removed the arrays.

## Remaining risks

- The round loop in `Main` is covered only by output-capture tests, not
  unit tests; a subtle reordering of prints would be caught, but only as a
  string comparison.
- `Rules.determineOutcome` returns display strings, so rule results and
  presentation text are still coupled (see the extension-readiness note).
- Most tests rely on the deterministic deck order. When shuffling is added
  in the final project, those tests must switch to seeded `Deck` instances
  (the seedable constructor already exists for this).
- `Game` exposes its hands mutably so tests can seed scenarios; production
  code could misuse this.
