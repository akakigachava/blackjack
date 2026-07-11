import blackjack.Card;
import blackjack.Command;
import blackjack.Deck;
import blackjack.Game;
import blackjack.Hand;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackjackCharacterizationTest {

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
        void drawFromExhaustedDeckReturnsPhantomAceOfHeartsWithoutAdvancing() {
            Deck deck = new Deck("2H");
            deck.draw();

            assertEquals("AH", deck.draw().toString());
            assertEquals("AH", deck.draw().toString());
            assertEquals(1, deck.position());
        }
    }

    @Nested
    class InitialDeal {

        @Test
        void startRoundDealsTwoCardsEachAlternatingPlayerFirst() {
            Deck deck = new Deck();
            Game game = new Game(deck);

            game.startRound();

            assertEquals(2, game.playerHand().cardCount());
            assertEquals(2, game.dealerHand().cardCount());
            assertEquals(4, deck.position());
            assertEquals("AH", game.playerHand().cardAt(0).toString());
            assertEquals("2H", game.dealerHand().cardAt(0).toString());
            assertEquals("3H", game.playerHand().cardAt(1).toString());
            assertEquals("4H", game.dealerHand().cardAt(1).toString());
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
    }

    @Nested
    class PlayerActions {

        @Test
        void hitAddsExactlyOneCardFromTheDeck() {
            Deck deck = new Deck();
            Game game = new Game(deck);
            game.startRound();

            game.playerHit();

            assertEquals(3, game.playerHand().cardCount());
            assertEquals(5, deck.position());
            assertEquals("5H", game.playerHand().cardAt(2).toString());
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

        @Test
        void dealerDrawsWhileBelowSeventeenAndMayBust() {
            Game game = new Game(new Deck("4C", "5S", "KH"));
            game.dealerHand().add(Card.fromCode("2H"));
            game.dealerHand().add(Card.fromCode("3D"));

            game.dealerPlay();

            assertEquals(5, game.dealerHand().cardCount());
            assertEquals(24, game.dealerHand().value());
        }

        @Test
        void dealerStandsOnHardSeventeen() {
            Game game = new Game();
            game.dealerHand().add(Card.fromCode("KH"));
            game.dealerHand().add(Card.fromCode("7D"));

            game.dealerPlay();

            assertEquals(2, game.dealerHand().cardCount());
        }

        @Test
        void dealerStandsOnSoftSeventeen() {
            Game game = new Game();
            game.dealerHand().add(Card.fromCode("AH"));
            game.dealerHand().add(Card.fromCode("6D"));

            game.dealerPlay();

            assertEquals(2, game.dealerHand().cardCount());
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
            assertEquals("Dealer wins", Rules.determineOutcome(22, 18));
        }

        @Test
        void dealerBustMeansPlayerWins() {
            assertEquals("Player wins", Rules.determineOutcome(18, 22));
        }

        @Test
        void bothBustMeansDealerWinsBecausePlayerBustIsCheckedFirst() {
            assertEquals("Dealer wins", Rules.determineOutcome(25, 26));
        }

        @Test
        void higherValueWins() {
            assertEquals("Player wins", Rules.determineOutcome(20, 18));
            assertEquals("Dealer wins", Rules.determineOutcome(17, 19));
        }

        @Test
        void equalValuesArePush() {
            assertEquals("Push", Rules.determineOutcome(18, 18));
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

        @Test
        void standingImmediatelyLosesTheDeterministicOpeningRound() {
            String output = playRound("stand\n");

            assertTrue(output.contains("Dealer value: 17"), output);
            assertTrue(output.contains("Dealer wins"), output);
        }

        @Test
        void hittingOnceThenStandingIsAPush() {
            String output = playRound("hit\nstand\n");

            assertTrue(output.contains("Player value: 19"), output);
            assertTrue(output.contains("Push"), output);
        }

        @Test
        void hittingUntilBustEndsTheRoundWithDealerWin() {
            String output = playRound("hit\nhit\nhit\n");

            assertTrue(output.contains("Player busts. Dealer wins."), output);
        }

        @Test
        void invalidCommandForcesAStandAndEndsTheRound() {
            String output = playRound("blackjack!\n");

            assertTrue(output.contains("Invalid command. You stand."), output);
            assertTrue(output.contains("Dealer wins"), output);
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
