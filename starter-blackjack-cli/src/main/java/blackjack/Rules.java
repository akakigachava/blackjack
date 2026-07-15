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
