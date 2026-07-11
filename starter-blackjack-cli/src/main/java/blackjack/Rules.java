package blackjack;

public final class Rules {
    public static final int DEALER_STAND_VALUE = 17;

    private Rules() {
    }

    public static boolean dealerShouldDraw(int dealerValue) {
        return dealerValue < DEALER_STAND_VALUE;
    }

    public static String determineOutcome(int playerValue, int dealerValue) {
        if (playerValue > 21) {
            return "Dealer wins";
        }
        if (dealerValue > 21) {
            return "Player wins";
        }
        if (playerValue > dealerValue) {
            return "Player wins";
        }
        if (dealerValue > playerValue) {
            return "Dealer wins";
        }
        return "Push";
    }
}
