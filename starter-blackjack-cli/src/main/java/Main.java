import blackjack.Deck;
import blackjack.Hand;

import java.util.Scanner;

public class Main {
    public static Deck deck = new Deck();
    public static Hand playerHand = new Hand();
    public static Hand dealerHand = new Hand();
    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        resetGame();
        startRound();

        System.out.println("Partial Blackjack CLI");
        System.out.println("Commands: hit, stand, q");
        System.out.println("No betting, split, double down, insurance, or blackjack payout.");

        while (true) {
            printTable(false);

            if (playerHand.isBust()) {
                printTable(true);
                System.out.println("Player busts. Dealer wins.");
                break;
            }

            System.out.print("Player action> ");
            String command = input.nextLine().trim();

            if (command.equals("q") || command.equals("quit")) {
                System.out.println("Game stopped.");
                break;
            }

            if (command.equals("hit")) {
                playerHit();
                continue;
            }

            if (command.equals("stand")) {
                dealerPlay();
                printTable(true);
                System.out.println(determineOutcome(playerHand.value(), dealerHand.value()));
                break;
            }

            System.out.println("Invalid command. You stand.");
            dealerPlay();
            printTable(true);
            System.out.println(determineOutcome(playerHand.value(), dealerHand.value()));
            break;
        }
    }

    public static void resetGame() {
        deck = new Deck();
        clearHands();
    }

    public static void clearHands() {
        playerHand = new Hand();
        dealerHand = new Hand();
    }

    public static void startRound() {
        clearHands();
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());
    }

    public static void playerHit() {
        playerHand.add(deck.draw());
    }

    public static void dealerPlay() {
        while (dealerHand.value() < 17) {
            dealerHand.add(deck.draw());
        }
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

    public static void printTable(boolean showDealer) {
        System.out.println();
        System.out.println("Dealer:");
        if (showDealer) {
            printHand(dealerHand);
            System.out.println("Dealer value: " + dealerHand.value());
        } else {
            System.out.println(dealerHand.cardAt(0) + " [hidden]");
        }

        System.out.println("Player:");
        printHand(playerHand);
        System.out.println("Player value: " + playerHand.value());
        System.out.println();
    }

    public static void printHand(Hand hand) {
        for (int i = 0; i < hand.cardCount(); i++) {
            System.out.print(hand.cardAt(i));
            if (i < hand.cardCount() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}
