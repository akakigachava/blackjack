import blackjack.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackjackCharacterizationTest {

    @BeforeEach
    void resetStaticState() {
        Main.resetGame();
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
            Main.startRound();

            assertEquals(2, Main.playerCardCount);
            assertEquals(2, Main.dealerCardCount);
            assertEquals(4, Main.deck.position());
            assertEquals("AH", Main.playerHand[0]);
            assertEquals("2H", Main.dealerHand[0]);
            assertEquals("3H", Main.playerHand[1]);
            assertEquals("4H", Main.dealerHand[1]);
        }
    }

    @Nested
    class HandValue {

        private int valueOf(String... cards) {
            Main.clearHands();
            for (int i = 0; i < cards.length; i++) {
                Main.playerHand[i] = cards[i];
            }
            Main.playerCardCount = cards.length;
            return Main.handValue(Main.playerHand, Main.playerCardCount);
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
        void nullSlotsWithinCountAreSkipped() {
            Main.clearHands();
            Main.playerHand[0] = "KH";
            Main.playerHand[1] = null;
            Main.playerCardCount = 2;

            assertEquals(10, Main.handValue(Main.playerHand, Main.playerCardCount));
        }
    }

    @Nested
    class PlayerActions {

        @Test
        void hitAddsExactlyOneCardFromTheDeck() {
            Main.startRound();

            Main.playerHit();

            assertEquals(3, Main.playerCardCount);
            assertEquals(5, Main.deck.position());
            assertEquals("5H", Main.playerHand[2]);
        }

        @Test
        void handOverTwentyOneIsABust() {
            Main.clearHands();
            Main.playerHand[0] = "KH";
            Main.playerHand[1] = "QD";
            Main.playerHand[2] = "2S";
            Main.playerCardCount = 3;

            assertTrue(Main.handValue(Main.playerHand, Main.playerCardCount) > 21);
        }
    }

    @Nested
    class DealerBehavior {

        @Test
        void dealerDrawsWhileBelowSeventeenAndMayBust() {
            Main.clearHands();
            Main.dealerHand[0] = "2H";
            Main.dealerHand[1] = "3D";
            Main.dealerCardCount = 2;
            Main.deck = new Deck("4C", "5S", "KH");

            Main.dealerPlay();

            assertEquals(5, Main.dealerCardCount);
            assertEquals(24, Main.handValue(Main.dealerHand, Main.dealerCardCount));
        }

        @Test
        void dealerStandsOnHardSeventeen() {
            Main.clearHands();
            Main.dealerHand[0] = "KH";
            Main.dealerHand[1] = "7D";
            Main.dealerCardCount = 2;

            Main.dealerPlay();

            assertEquals(2, Main.dealerCardCount);
        }

        @Test
        void dealerStandsOnSoftSeventeen() {
            Main.clearHands();
            Main.dealerHand[0] = "AH";
            Main.dealerHand[1] = "6D";
            Main.dealerCardCount = 2;

            Main.dealerPlay();

            assertEquals(2, Main.dealerCardCount);
        }
    }

    @Nested
    class Outcomes {

        @Test
        void playerBustMeansDealerWins() {
            assertEquals("Dealer wins", Main.determineOutcome(22, 18));
        }

        @Test
        void dealerBustMeansPlayerWins() {
            assertEquals("Player wins", Main.determineOutcome(18, 22));
        }

        @Test
        void bothBustMeansDealerWinsBecausePlayerBustIsCheckedFirst() {
            assertEquals("Dealer wins", Main.determineOutcome(25, 26));
        }

        @Test
        void higherValueWins() {
            assertEquals("Player wins", Main.determineOutcome(20, 18));
            assertEquals("Dealer wins", Main.determineOutcome(17, 19));
        }

        @Test
        void equalValuesArePush() {
            assertEquals("Push", Main.determineOutcome(18, 18));
        }
    }

    @Nested
    class CliRounds {

        private String playRound(String typedInput) {
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            Scanner originalInput = Main.input;
            try {
                System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
                Main.input = new Scanner(
                        new ByteArrayInputStream(typedInput.getBytes(StandardCharsets.UTF_8)));
                Main.main(new String[0]);
            } finally {
                System.setOut(originalOut);
                Main.input = originalInput;
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
            assertTrue(!output.contains("Player wins") && !output.contains("Dealer wins"), output);
        }
    }
}
