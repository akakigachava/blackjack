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
        Main.playerHand.add(blackjack.Card.fromCode("KH"));
        Main.playerHand.add(blackjack.Card.fromCode("QS"));

        assertEquals(20, Main.playerHand.value(), "face-card value");
    }

    private static void testAceCountsAsElevenWhenSafe() {
        Main.clearHands();
        Main.playerHand.add(blackjack.Card.fromCode("AH"));
        Main.playerHand.add(blackjack.Card.fromCode("9S"));

        assertEquals(20, Main.playerHand.value(), "soft ace value");
    }

    private static void testAceCountsAsOneWhenElevenWouldBust() {
        Main.clearHands();
        Main.playerHand.add(blackjack.Card.fromCode("AH"));
        Main.playerHand.add(blackjack.Card.fromCode("9S"));
        Main.playerHand.add(blackjack.Card.fromCode("5D"));

        assertEquals(15, Main.playerHand.value(), "ace adjusted value");
    }

    private static void testStartRoundDealsTwoCardsEach() {
        Main.resetGame();
        Main.startRound();

        assertEquals(2, Main.playerHand.cardCount(), "player card count");
        assertEquals(2, Main.dealerHand.cardCount(), "dealer card count");
        assertEquals(4, Main.deck.position(), "deck position after initial deal");
    }

    private static void testPlayerHitAddsOneCard() {
        Main.resetGame();
        Main.startRound();

        Main.playerHit();

        assertEquals(3, Main.playerHand.cardCount(), "player card count after hit");
        assertEquals(5, Main.deck.position(), "deck position after hit");
    }

    private static void testDealerDrawsUntilSeventeen() {
        Main.resetGame();
        Main.clearHands();
        Main.dealerHand.add(blackjack.Card.fromCode("2H"));
        Main.dealerHand.add(blackjack.Card.fromCode("3D"));
        Main.deck = new blackjack.Deck("4C", "5S", "KH");

        Main.dealerPlay();

        assertEquals(5, Main.dealerHand.cardCount(), "dealer card count after drawing");
        assertEquals(24, Main.dealerHand.value(), "dealer final value");
    }

    private static void testEqualValuesArePush() {
        String result = blackjack.Rules.determineOutcome(18, 18);

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

