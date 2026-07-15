# Rules Supported

This lists which rules from `Final_Project_Blackjack_rules_reference.md`
are implemented, with the variants and simplifications used.

## Implemented

### Deck and cards
- Standard 52-card deck, all four suits, ranks A–K.
- The deck is shuffled (`java.util.Random`) before play and dealt in order.
- Single-deck shoe: the deck is reshuffled before a round when fewer than
  15 cards remain (`Rules.RESHUFFLE_THRESHOLD`), and immediately if it runs
  out mid-round.

### Round setup
- Bet placed before the deal (whole chips, 1 up to the current bankroll;
  empty input bets 10 or the whole bankroll if smaller).
- Deal order: player, dealer, player, dealer. The dealer's first card is
  hidden until the player finishes acting.

### Hand values
- Number cards count face value, J/Q/K count 10, aces count 11 falling
  back to 1 as needed (multiple aces handled).

### Player actions
- `hit` and `stand` at any time.
- `double` — double down: first action only, requires enough chips to
  double the bet, draws exactly one card, then the dealer plays.
- `surrender` — late surrender: first action only, forfeits half the bet
  (rounded in the player's favor), dealer does not play. Because naturals
  are resolved before any action, surrender is never offered against a
  dealer blackjack.
- `q`/`quit` — leaves the session; an unfinished round is not recorded.
- Any other input is treated as `stand` (kept from the baseline game).

### Dealer rules
- Dealer reveals the hole card after the player stands, doubles, or busts.
- Dealer draws while below 17 and stands on all 17s, including soft 17.

### Outcomes and payouts
- Bust, win, loss, and push are all handled; both-bust goes to the dealer
  because the player's bust ends the round first.
- Natural blackjack (21 with the first two cards) is distinguished from a
  later 21: it is resolved immediately after the deal and pays 3:2
  (rounded down to whole chips, e.g. a bet of 5 pays 7).
- Player and dealer naturals push; dealer natural wins the full bet.
- Even-money wins pay 1:1; surrender loses half the bet.

### Bankroll and session
- The session starts with 100 chips and tracks the bankroll across rounds.
- The session ends when the player quits or runs out of chips.
- Sessions contain any number of rounds; every completed round is
  persisted with bet, bankroll change, and resulting balance.

### Session history
- All players, sessions, rounds, hands, actions, bets, and outcomes are
  recorded to the database; `--stats` prints five reports including a
  replay-style recent-round list. See `docs/database.md`.

## Not implemented

- **Split** (and multiple hands after split) — the largest structural
  change (several concurrent hands per round); left out deliberately and
  documented here.
- **Insurance** — side bet against a dealer ace; not offered.
- **Multi-deck shoes** — the shoe is a single deck with the reshuffle rule
  above.
- **Betting variations** — no minimum/maximum table limits beyond
  1..bankroll, no side bets.

## Simplifications

- Blackjack pays 3:2 rounded down to whole chips (integer arithmetic).
- Surrender returns half the bet rounded in the player's favor.
- Naturals are checked for both hands immediately after the deal (works
  like a dealer hole-card peek: a dealer natural ends the round before
  any player action).
- Chips are abstract units with no currency; the starting bankroll is
  fixed at 100.
