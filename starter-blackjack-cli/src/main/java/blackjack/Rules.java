package blackjack;

public final class Rules {
    public static final int DEALER_STAND_VALUE = 17;

    /** Reshuffle before a round when fewer cards than this remain. */
    public static final int RESHUFFLE_THRESHOLD = 15;

    private Rules() {
    }

    public static boolean dealerShouldDraw(int dealerValue) {
        return dealerValue < DEALER_STAND_VALUE;
    }

    /**
     * Resolves naturals right after the deal: a two-card 21 ends the round
     * before any actions. Returns null when no one has a natural and play
     * continues.
     */
    public static Outcome naturalOutcome(boolean playerNatural, boolean dealerNatural) {
        if (playerNatural && dealerNatural) {
            return Outcome.PUSH;
        }
        if (playerNatural) {
            return Outcome.PLAYER_BLACKJACK;
        }
        if (dealerNatural) {
            return Outcome.DEALER_WINS;
        }
        return null;
    }

    /**
     * The bankroll change for a finished round. Blackjack pays 3:2 and
     * surrender returns half the bet, both rounded in the player's favor
     * (integer chips).
     */
    public static int payout(Outcome outcome, int bet) {
        return switch (outcome) {
            case PLAYER_WINS -> bet;
            case PLAYER_BLACKJACK -> bet * 3 / 2;
            case DEALER_WINS -> -bet;
            case PUSH -> 0;
            case SURRENDER -> -(bet / 2);
        };
    }

    public static Outcome determineOutcome(int playerValue, int dealerValue) {
        if (playerValue > 21) {
            return Outcome.DEALER_WINS;
        }
        if (dealerValue > 21) {
            return Outcome.PLAYER_WINS;
        }
        if (playerValue > dealerValue) {
            return Outcome.PLAYER_WINS;
        }
        if (dealerValue > playerValue) {
            return Outcome.DEALER_WINS;
        }
        return Outcome.PUSH;
    }
}
