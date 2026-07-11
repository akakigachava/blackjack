package blackjack;

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
            showHand(game.dealerHand());
            System.out.println("Dealer value: " + game.dealerHand().value());
        } else {
            System.out.println(game.dealerHand().cardAt(0) + " [hidden]");
        }

        System.out.println("Player:");
        showHand(game.playerHand());
        System.out.println("Player value: " + game.playerHand().value());
        System.out.println();
    }

    public void showHand(Hand hand) {
        for (int i = 0; i < hand.cardCount(); i++) {
            System.out.print(hand.cardAt(i));
            if (i < hand.cardCount() - 1) {
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

    public void showOutcome(String outcome) {
        System.out.println(outcome);
    }
}
