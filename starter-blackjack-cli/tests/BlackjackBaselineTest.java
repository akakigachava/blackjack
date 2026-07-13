import blackjack.Card;
import blackjack.Deck;
import blackjack.Game;
import blackjack.Hand;
import blackjack.Outcome;
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

        assertEquals(2, game.playerCards().size(), "player card count");
        assertEquals(2, game.dealerCards().size(), "dealer card count");
        assertEquals(4, deck.position(), "deck position after initial deal");
    }

    private static void testPlayerHitAddsOneCard() {
        Deck deck = new Deck();
        Game game = new Game(deck);
        game.startRound();

        game.playerHit();

        assertEquals(3, game.playerCards().size(), "player card count after hit");
        assertEquals(5, deck.position(), "deck position after hit");
    }

    private static void testDealerDrawsUntilSeventeen() {
        Hand dealerHand = new Hand();
        dealerHand.add(Card.fromCode("2H"));
        dealerHand.add(Card.fromCode("3D"));
        Game game = new Game(new Deck("4C", "5S", "KH"), new Hand(), dealerHand);

        game.dealerPlay();

        assertEquals(5, game.dealerCards().size(), "dealer card count after drawing");
        assertEquals(24, game.dealerValue(), "dealer final value");
    }

    private static void testEqualValuesArePush() {
        Outcome result = Rules.determineOutcome(18, 18);

        assertEquals(Outcome.PUSH.name(), result.name(), "push outcome");
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
