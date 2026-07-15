package blackjack;

import java.util.List;

public class ConsoleView {

    public void showWelcome() {
        System.out.println("Blackjack CLI");
        System.out.println("Round commands: hit, stand, double, surrender, q");
        System.out.println("Blackjack pays 3:2. Dealer stands on all 17s. No split or insurance.");
    }

    public void showBankroll(int chips) {
        System.out.println("Chips: " + chips);
    }

    public void showBetPrompt(int chips, int defaultBet) {
        System.out.print("Bet (chips: " + chips + ", enter=" + defaultBet + ", q quits)> ");
    }

    public void showBetError(int chips) {
        System.out.println("Enter a whole number between 1 and " + chips + ".");
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

    public void showActionNotAllowed(String action) {
        System.out.println("You can only " + action + " as your first action.");
    }

    public void showCannotAffordDouble() {
        System.out.println("Not enough chips to double down.");
    }

    public void showOutOfChips() {
        System.out.println("You are out of chips. Session over.");
    }

    public void showOutcome(Outcome outcome) {
        System.out.println(displayText(outcome));
    }

    /** The chips won or lost this round and the new balance. */
    public void showRoundResult(int delta, int chips) {
        if (delta > 0) {
            System.out.println("You win " + delta + ". Chips: " + chips);
        } else if (delta < 0) {
            System.out.println("You lose " + (-delta) + ". Chips: " + chips);
        } else {
            System.out.println("Bet returned. Chips: " + chips);
        }
        System.out.println();
    }

    /**
     * The exact player-facing text for each outcome. Kept in the view so
     * {@link Rules} stays free of presentation strings.
     */
    public static String displayText(Outcome outcome) {
        return switch (outcome) {
            case PLAYER_WINS -> "Player wins";
            case PLAYER_BLACKJACK -> "Blackjack! Player wins";
            case DEALER_WINS -> "Dealer wins";
            case PUSH -> "Push";
            case SURRENDER -> "Player surrenders";
        };
    }
}
