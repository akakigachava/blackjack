import blackjack.Card;
import blackjack.Deck;
import blackjack.Game;
import blackjack.Hand;
import blackjack.Rules;

public class BlackjackBaselineTest {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("blackjack").setUseParentHandlers(false);
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
        Deck deck = new Deck();

        assertEquals(52, deck.size(), "deck size");
        assertEquals("AH", deck.cardAt(0).toString(), "first deterministic card");
        assertEquals("KS", deck.cardAt(51).toString(), "last deterministic card");
    }

    private static void testFaceCardsCountAsTen() {
        Hand hand = new Hand();
        hand.add(Card.fromCode("KH"));
        hand.add(Card.fromCode("QS"));

        assertEquals(20, hand.value(), "face-card value");
    }

    private static void testAceCountsAsElevenWhenSafe() {
        Hand hand = new Hand();
        hand.add(Card.fromCode("AH"));
        hand.add(Card.fromCode("9S"));

        assertEquals(20, hand.value(), "soft ace value");
    }

    private static void testAceCountsAsOneWhenElevenWouldBust() {
        Hand hand = new Hand();
        hand.add(Card.fromCode("AH"));
        hand.add(Card.fromCode("9S"));
        hand.add(Card.fromCode("5D"));

        assertEquals(15, hand.value(), "ace adjusted value");
    }

    private static void testStartRoundDealsTwoCardsEach() {
        Deck deck = new Deck();
        Game game = new Game(deck);

        game.startRound();

        assertEquals(2, game.playerHand().cardCount(), "player card count");
        assertEquals(2, game.dealerHand().cardCount(), "dealer card count");
        assertEquals(4, deck.position(), "deck position after initial deal");
    }

    private static void testPlayerHitAddsOneCard() {
        Deck deck = new Deck();
        Game game = new Game(deck);
        game.startRound();

        game.playerHit();

        assertEquals(3, game.playerHand().cardCount(), "player card count after hit");
        assertEquals(5, deck.position(), "deck position after hit");
    }

    private static void testDealerDrawsUntilSeventeen() {
        Game game = new Game(new Deck("4C", "5S", "KH"));
        game.dealerHand().add(Card.fromCode("2H"));
        game.dealerHand().add(Card.fromCode("3D"));

        game.dealerPlay();

        assertEquals(5, game.dealerHand().cardCount(), "dealer card count after drawing");
        assertEquals(24, game.dealerHand().value(), "dealer final value");
    }

    private static void testEqualValuesArePush() {
        String result = Rules.determineOutcome(18, 18);

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
