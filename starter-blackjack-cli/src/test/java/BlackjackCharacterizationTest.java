import blackjack.Card;
import blackjack.Command;
import blackjack.ConsoleView;
import blackjack.Deck;
import blackjack.Game;
import blackjack.Hand;
import blackjack.Outcome;
import blackjack.Rules;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackjackCharacterizationTest {

    static {
        java.util.logging.Logger.getLogger("blackjack").setUseParentHandlers(false);
        // CLI round tests run the real Main; keep their session history in
        // an isolated in-memory database instead of the real ./data file.
        System.setProperty("blackjack.db.url", "jdbc:h2:mem:charization-tests;DB_CLOSE_DELAY=-1");
    }

    @Nested
    class DeckBehavior {

        @Test
        void newDeckHasFiftyTwoCards() {
            assertEquals(52, new Deck().size());
        }

        @Test
        void deckIsNeverShuffled_orderIsDeterministic() {
            Deck deck = new Deck();

            assertEquals("AH", deck.cardAt(0).toString());
            assertEquals("2H", deck.cardAt(1).toString());
            assertEquals("KH", deck.cardAt(12).toString());
            assertEquals("AD", deck.cardAt(13).toString());
            assertEquals("KS", deck.cardAt(51).toString());
        }

        @Test
        void drawReturnsCardsInOrderAndAdvancesPosition() {
            Deck deck = new Deck();

            assertEquals("AH", deck.draw().toString());
            assertEquals("2H", deck.draw().toString());
            assertEquals(2, deck.position());
        }

        @Test
        void drawFromExhaustedFixedDeckReturnsPhantomAceOfHeartsWithoutAdvancing() {
            Deck deck = new Deck("2H");
            deck.draw();

            assertEquals("AH", deck.draw().toString());
            assertEquals("AH", deck.draw().toString());
            assertEquals(1, deck.position());
        }

        @Test
        void shuffledDeckHasAllFiftyTwoUniqueCards() {
            Deck deck = new Deck();
            Deck shuffled = Deck.shuffled(new java.util.Random(42));

            java.util.Set<String> ordered = new java.util.HashSet<>();
            java.util.Set<String> randomized = new java.util.HashSet<>();
            for (int i = 0; i < 52; i++) {
                ordered.add(deck.cardAt(i).toString());
                randomized.add(shuffled.cardAt(i).toString());
            }
            assertEquals(52, randomized.size());
            assertEquals(ordered, randomized);
        }

        @Test
        void shuffleWithSameSeedIsReproducible() {
            Deck first = Deck.shuffled(new java.util.Random(42));
            Deck second = Deck.shuffled(new java.util.Random(42));

            for (int i = 0; i < 52; i++) {
                assertEquals(first.cardAt(i).toString(), second.cardAt(i).toString());
            }
        }

        @Test
        void shuffledDeckReshufflesInsteadOfRunningOut() {
            Deck deck = Deck.shuffled(new java.util.Random(42));
            for (int i = 0; i < 52; i++) {
                deck.draw();
            }

            assertEquals(0, deck.remaining());
            deck.draw();
            assertEquals(51, deck.remaining());
        }

        @Test
        void prepareForRoundReshufflesOnlyWhenLow() {
            Deck deck = Deck.shuffled(new java.util.Random(42));
            for (int i = 0; i < 40; i++) {
                deck.draw();
            }

            deck.prepareForRound(15);
            assertEquals(52, deck.remaining());

            Deck fixedDeck = new Deck("2H", "3H");
            fixedDeck.draw();
            fixedDeck.prepareForRound(15);
            assertEquals(1, fixedDeck.remaining());
        }
    }

    @Nested
    class InitialDeal {

        @Test
        void startRoundDealsTwoCardsEachAlternatingPlayerFirst() {
            Deck deck = new Deck();
            Game game = new Game(deck);

            game.startRound();

            assertEquals(2, game.playerCards().size());
            assertEquals(2, game.dealerCards().size());
            assertEquals(4, deck.position());
            assertEquals("AH", game.playerCards().get(0).toString());
            assertEquals("2H", game.dealerCards().get(0).toString());
            assertEquals("3H", game.playerCards().get(1).toString());
            assertEquals("4H", game.dealerCards().get(1).toString());
        }
    }

    @Nested
    class HandValue {

        private int valueOf(String... cards) {
            Hand hand = new Hand();
            for (String card : cards) {
                hand.add(Card.fromCode(card));
            }
            return hand.value();
        }

        @Test
        void numberCardsCountAsTheirNumber() {
            assertEquals(9, valueOf("2H", "3D", "4S"));
        }

        @Test
        void faceCardsCountAsTen() {
            assertEquals(30, valueOf("KH", "QS", "JD"));
        }

        @Test
        void aceCountsAsElevenWhenItDoesNotBust() {
            assertEquals(20, valueOf("AH", "9S"));
        }

        @Test
        void aceCountsAsOneWhenElevenWouldBust() {
            assertEquals(15, valueOf("AH", "9S", "5D"));
        }

        @Test
        void twoAcesCountAsTwelve() {
            assertEquals(12, valueOf("AH", "AS"));
        }

        @Test
        void addRejectsNullCards() {
            // Intentional change from the baseline: the old array-based
            // handValue silently skipped null entries. The Hand contract
            // now rejects them outright.
            Hand hand = new Hand();

            assertThrows(NullPointerException.class, () -> hand.add(null));
            assertEquals(0, hand.cardCount());
        }
    }

    @Nested
    class PlayerActions {

        @Test
        void hitAddsExactlyOneCardFromTheDeck() {
            Deck deck = new Deck();
            Game game = new Game(deck);
            game.startRound();

            game.playerHit();

            assertEquals(3, game.playerCards().size());
            assertEquals(5, deck.position());
            assertEquals("5H", game.playerCards().get(2).toString());
        }

        @Test
        void handOverTwentyOneIsABust() {
            Hand hand = new Hand();
            hand.add(Card.fromCode("KH"));
            hand.add(Card.fromCode("QD"));
            hand.add(Card.fromCode("2S"));

            assertTrue(hand.isBust());
        }
    }

    @Nested
    class DealerBehavior {

        /** Fixture: a game mid-round with the given dealer cards. */
        private Game gameWithDealerHand(Deck deck, String... dealerCards) {
            Hand dealerHand = new Hand();
            for (String card : dealerCards) {
                dealerHand.add(Card.fromCode(card));
            }
            return new Game(deck, new Hand(), dealerHand);
        }

        @Test
        void dealerDrawsWhileBelowSeventeenAndMayBust() {
            Game game = gameWithDealerHand(new Deck("4C", "5S", "KH"), "2H", "3D");

            game.dealerPlay();

            assertEquals(5, game.dealerCards().size());
            assertEquals(24, game.dealerValue());
        }

        @Test
        void dealerStandsOnHardSeventeen() {
            Game game = gameWithDealerHand(new Deck(), "KH", "7D");

            game.dealerPlay();

            assertEquals(2, game.dealerCards().size());
        }

        @Test
        void dealerStandsOnSoftSeventeen() {
            Game game = gameWithDealerHand(new Deck(), "AH", "6D");

            game.dealerPlay();

            assertEquals(2, game.dealerCards().size());
        }

        @Test
        void dealerDrawThresholdIsSeventeen() {
            assertTrue(Rules.dealerShouldDraw(16));
            assertFalse(Rules.dealerShouldDraw(17));
            assertFalse(Rules.dealerShouldDraw(18));
        }
    }

    @Nested
    class Outcomes {

        @Test
        void playerBustMeansDealerWins() {
            assertEquals(Outcome.DEALER_WINS, Rules.determineOutcome(22, 18));
        }

        @Test
        void dealerBustMeansPlayerWins() {
            assertEquals(Outcome.PLAYER_WINS, Rules.determineOutcome(18, 22));
        }

        @Test
        void bothBustMeansDealerWinsBecausePlayerBustIsCheckedFirst() {
            assertEquals(Outcome.DEALER_WINS, Rules.determineOutcome(25, 26));
        }

        @Test
        void higherValueWins() {
            assertEquals(Outcome.PLAYER_WINS, Rules.determineOutcome(20, 18));
            assertEquals(Outcome.DEALER_WINS, Rules.determineOutcome(17, 19));
        }

        @Test
        void equalValuesArePush() {
            assertEquals(Outcome.PUSH, Rules.determineOutcome(18, 18));
        }

        @Test
        void viewMapsOutcomesToTheExactBaselineStrings() {
            assertEquals("Player wins", ConsoleView.displayText(Outcome.PLAYER_WINS));
            assertEquals("Dealer wins", ConsoleView.displayText(Outcome.DEALER_WINS));
            assertEquals("Push", ConsoleView.displayText(Outcome.PUSH));
        }
    }

    @Nested
    class CommandParsing {

        @Test
        void knownCommandsParse() {
            assertEquals(Command.HIT, Command.parse("hit"));
            assertEquals(Command.STAND, Command.parse("stand"));
            assertEquals(Command.QUIT, Command.parse("q"));
            assertEquals(Command.QUIT, Command.parse("quit"));
        }

        @Test
        void surroundingWhitespaceIsIgnored() {
            assertEquals(Command.HIT, Command.parse("  hit  "));
        }

        @Test
        void anythingElseIsInvalid() {
            assertEquals(Command.INVALID, Command.parse("double"));
            assertEquals(Command.INVALID, Command.parse("HIT"));
            assertEquals(Command.INVALID, Command.parse(""));
        }
    }

    @Nested
    class CliRounds {

        private String playRound(String typedInput) {
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            InputStream originalIn = System.in;
            try {
                System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
                System.setIn(new ByteArrayInputStream(typedInput.getBytes(StandardCharsets.UTF_8)));
                Main.main(new String[0]);
            } finally {
                System.setOut(originalOut);
                System.setIn(originalIn);
            }
            return captured.toString(StandardCharsets.UTF_8);
        }

        private void assertSomeOutcomeShown(String output) {
            assertTrue(output.contains("Player wins")
                            || output.contains("Dealer wins")
                            || output.contains("Push"),
                    output);
        }

        @Test
        void standingEndsTheRoundWithAnOutcome() {
            String output = playRound("stand\n");

            assertTrue(output.contains("Player action> "), output);
            assertTrue(output.contains("Dealer value: "), output);
            assertSomeOutcomeShown(output);
        }

        @Test
        void hittingKeepsTheRoundGoingUntilStand() {
            // 25 hit commands guarantee a bust even across reshuffles (any
            // hand of 22+ cards exceeds 21), so the round always ends
            // whether or not the stand is ever reached.
            String output = playRound("hit\n".repeat(25) + "stand\n");

            assertSomeOutcomeShown(output);
        }

        @Test
        void hittingUntilBustEndsTheRoundWithDealerWin() {
            String output = playRound("hit\n".repeat(25));

            assertTrue(output.contains("Player busts. Dealer wins."), output);
        }

        @Test
        void invalidCommandForcesAStandAndEndsTheRound() {
            String output = playRound("blackjack!\n");

            assertTrue(output.contains("Invalid command. You stand."), output);
            assertSomeOutcomeShown(output);
        }

        @Test
        void quitCommandStopsTheGameWithoutAnOutcome() {
            String output = playRound("q\n");

            assertTrue(output.contains("Game stopped."), output);
            assertFalse(output.contains("Player wins"), output);
            assertFalse(output.contains("Dealer wins"), output);
        }
    }
}
