package blackjack;

import java.util.List;

public class ConsoleView {

    public void showWelcome() {
        System.out.println("Partial Blackjack CLI");
        System.out.println("Commands: hit, stand, q");
        System.out.println("No betting, split, double down, insurance, or blackjack payout.");
    }

    public void showTable(Game game, boolean showDealer) {
        System.out.println();
        System.out.println("Dealer:");
        if (showDealer) {
            showHand(game.dealerCards());
            System.out.println("Dealer value: " + game.dealerValue());
        } else {
            System.out.println(game.dealerCards().get(0) + " [hidden]");
        }

        System.out.println("Player:");
        showHand(game.playerCards());
        System.out.println("Player value: " + game.playerValue());
        System.out.println();
    }

    public void showHand(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.print(cards.get(i));
            if (i < cards.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public void showActionPrompt() {
        System.out.print("Player action> ");
    }

    public void showPlayerBust() {
        System.out.println("Player busts. Dealer wins.");
    }

    public void showInvalidCommand() {
        System.out.println("Invalid command. You stand.");
    }

    public void showGameStopped() {
        System.out.println("Game stopped.");
    }

    public void showOutcome(Outcome outcome) {
        System.out.println(displayText(outcome));
    }

    /**
     * The exact player-facing text for each outcome. Kept in the view so
     * {@link Rules} stays free of presentation strings.
     */
    public static String displayText(Outcome outcome) {
        return switch (outcome) {
            case PLAYER_WINS -> "Player wins";
            case DEALER_WINS -> "Dealer wins";
            case PUSH -> "Push";
        };
    }
}
