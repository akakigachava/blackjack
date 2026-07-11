import blackjack.Deck;

import java.util.Scanner;

public class Main {
    public static Deck deck = new Deck();
    public static String[] playerHand = new String[12];
    public static String[] dealerHand = new String[12];
    public static int playerCardCount = 0;
    public static int dealerCardCount = 0;
    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        resetGame();
        startRound();

        System.out.println("Partial Blackjack CLI");
        System.out.println("Commands: hit, stand, q");
        System.out.println("No betting, split, double down, insurance, or blackjack payout.");

        while (true) {
            printTable(false);

            if (handValue(playerHand, playerCardCount) > 21) {
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
                System.out.println(determineOutcome(
                        handValue(playerHand, playerCardCount),
                        handValue(dealerHand, dealerCardCount)));
                break;
            }

            System.out.println("Invalid command. You stand.");
            dealerPlay();
            printTable(true);
            System.out.println(determineOutcome(
                    handValue(playerHand, playerCardCount),
                    handValue(dealerHand, dealerCardCount)));
            break;
        }
    }

    public static void resetGame() {
        deck = new Deck();
        clearHands();
    }

    public static void clearHands() {
        for (int i = 0; i < playerHand.length; i++) {
            playerHand[i] = null;
        }
        for (int i = 0; i < dealerHand.length; i++) {
            dealerHand[i] = null;
        }
        playerCardCount = 0;
        dealerCardCount = 0;
    }

    public static void startRound() {
        clearHands();
        playerHand[playerCardCount] = drawCard();
        playerCardCount++;
        dealerHand[dealerCardCount] = drawCard();
        dealerCardCount++;
        playerHand[playerCardCount] = drawCard();
        playerCardCount++;
        dealerHand[dealerCardCount] = drawCard();
        dealerCardCount++;
    }

    public static String drawCard() {
        return deck.draw().toString();
    }

    public static void playerHit() {
        playerHand[playerCardCount] = drawCard();
        playerCardCount++;
    }

    public static void dealerPlay() {
        while (handValue(dealerHand, dealerCardCount) < 17) {
            dealerHand[dealerCardCount] = drawCard();
            dealerCardCount++;
        }
    }

    public static int handValue(String[] hand, int count) {
        int total = 0;
        int aces = 0;

        for (int i = 0; i < count; i++) {
            String card = hand[i];
            if (card == null || card.length() == 0) {
                continue;
            }
            String rank = card.substring(0, card.length() - 1);
            if (rank.equals("A")) {
                total += 11;
                aces++;
            } else if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) {
                total += 10;
            } else {
                total += Integer.parseInt(rank);
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
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
            printHand(dealerHand, dealerCardCount);
            System.out.println("Dealer value: " + handValue(dealerHand, dealerCardCount));
        } else {
            System.out.println(dealerHand[0] + " [hidden]");
        }

        System.out.println("Player:");
        printHand(playerHand, playerCardCount);
        System.out.println("Player value: " + handValue(playerHand, playerCardCount));
        System.out.println();
    }

    public static void printHand(String[] hand, int count) {
        for (int i = 0; i < count; i++) {
            System.out.print(hand[i]);
            if (i < count - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}

