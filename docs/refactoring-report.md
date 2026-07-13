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
   quirk; an explicit fixed-order constructor, `Deck(String... cardCodes)`,
   replaced the old trick of swapping in a raw array from tests — it
   injects a deterministic card sequence and involves no random seed).
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

## Intentional behavior change (documented and tested)

One behavior was removed rather than preserved: the old `handValue` skipped
`null` entries inside the counted range of the hand array. That situation
was unreachable through gameplay — it could only be produced by externally
poking `Main`'s public arrays — and the array representation itself was
removed.

The new contract is explicit: `Hand.add` rejects `null` with a
`NullPointerException` and leaves the hand unchanged, so a hand can never
hold a `null` card. This is enforced by the characterization test
`addRejectsNullCards`, which replaces the deleted null-tolerance test.

## Rework changes (after the midterm review)

- `Hand.add` now rejects `null` (see the intentional-change section above),
  with a test pinning the contract.
- `Rules.determineOutcome` returns an `Outcome` enum (`PLAYER_WINS`,
  `DEALER_WINS`, `PUSH`) instead of display strings. The exact baseline
  strings live in `ConsoleView.displayText`, and both the CLI round tests
  and a dedicated mapping test keep them unchanged.
- `Game` no longer exposes mutable `Hand` objects. Callers use read-only
  query methods (`playerCards()`, `dealerCards()`, `playerValue()`,
  `dealerValue()`); tests seed mid-round scenarios through a fixture
  constructor `Game(Deck, Hand, Hand)`.
- `scripts/run.sh` and `scripts/test.sh` share `scripts/jdk-env.sh`, which
  validates an existing `JAVA_HOME`, consults `/usr/libexec/java_home` on
  macOS, only accepts a derived home that is a real JDK (never `/usr` from
  Apple's `javac` shim), and otherwise falls back to `javac` on `PATH` or
  fails with a clear setup error.

## Remaining risks

- The round loop in `Main` is covered only by output-capture tests, not
  unit tests; a subtle reordering of prints would be caught, but only as a
  string comparison.
- Most tests rely on the deterministic deck order. When shuffling is added
  in the final project, those tests must inject fixed-order decks (the
  `Deck(String...)` constructor already exists for this) or use a future
  seeded-shuffle constructor once one is written.
- The `Game(Deck, Hand, Hand)` fixture constructor is public so tests can
  reach it; production code is expected to use `startRound()` instead.
