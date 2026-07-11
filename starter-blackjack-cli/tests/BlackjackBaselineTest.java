public class BlackjackBaselineTest {
    public static void main(String[] args) {
        testNewDeckHasFiftyTwoCards();
        testFaceCardsCountAsTen();
        testAceCountsAsElevenWhenSafe();
        testAceCountsAsOneWhenElevenWouldBust();
        testStartRoundDealsTwoCardsEach();
        testPlayerHitAddsOneCard();
        testDealerDrawsUntilSeventeen();
        testEqualValuesArePush();
        System.out.println("All baseline checks passed.");
    }

    private static void testNewDeckHasFiftyTwoCards() {
        Main.resetGame();

        assertEquals(52, Main.deck.size(), "deck size");
        assertEquals("AH", Main.deck.cardAt(0).toString(), "first deterministic card");
        assertEquals("KS", Main.deck.cardAt(51).toString(), "last deterministic card");
    }

    private static void testFaceCardsCountAsTen() {
        Main.clearHands();
        Main.playerHand[0] = "KH";
        Main.playerHand[1] = "QS";
        Main.playerCardCount = 2;

        assertEquals(20, Main.handValue(Main.playerHand, Main.playerCardCount), "face-card value");
    }

    private static void testAceCountsAsElevenWhenSafe() {
        Main.clearHands();
        Main.playerHand[0] = "AH";
        Main.playerHand[1] = "9S";
        Main.playerCardCount = 2;

        assertEquals(20, Main.handValue(Main.playerHand, Main.playerCardCount), "soft ace value");
    }

    private static void testAceCountsAsOneWhenElevenWouldBust() {
        Main.clearHands();
        Main.playerHand[0] = "AH";
        Main.playerHand[1] = "9S";
        Main.playerHand[2] = "5D";
        Main.playerCardCount = 3;

        assertEquals(15, Main.handValue(Main.playerHand, Main.playerCardCount), "ace adjusted value");
    }

    private static void testStartRoundDealsTwoCardsEach() {
        Main.resetGame();
        Main.startRound();

        assertEquals(2, Main.playerCardCount, "player card count");
        assertEquals(2, Main.dealerCardCount, "dealer card count");
        assertEquals(4, Main.deck.position(), "deck position after initial deal");
    }

    private static void testPlayerHitAddsOneCard() {
        Main.resetGame();
        Main.startRound();

        Main.playerHit();

        assertEquals(3, Main.playerCardCount, "player card count after hit");
        assertEquals(5, Main.deck.position(), "deck position after hit");
    }

    private static void testDealerDrawsUntilSeventeen() {
        Main.resetGame();
        Main.clearHands();
        Main.dealerHand[0] = "2H";
        Main.dealerHand[1] = "3D";
        Main.dealerCardCount = 2;
        Main.deck = new blackjack.Deck("4C", "5S", "KH");

        Main.dealerPlay();

        assertEquals(5, Main.dealerCardCount, "dealer card count after drawing");
        assertEquals(24, Main.handValue(Main.dealerHand, Main.dealerCardCount), "dealer final value");
    }

    private static void testEqualValuesArePush() {
        String result = Main.determineOutcome(18, 18);

        assertEquals("Push", result, "push outcome");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }
}

