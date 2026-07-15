import blackjack.Bankroll;
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
import static org.junit.jupiter.api.Assertions.assertNull;
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
        void naturalIsExactlyATwoCardTwentyOne() {
            Hand natural = new Hand();
            natural.add(Card.fromCode("AH"));
            natural.add(Card.fromCode("KS"));
            assertTrue(natural.isNatural());

            Hand threeCardTwentyOne = new Hand();
            threeCardTwentyOne.add(Card.fromCode("7H"));
            threeCardTwentyOne.add(Card.fromCode("7S"));
            threeCardTwentyOne.add(Card.fromCode("7D"));
            assertFalse(threeCardTwentyOne.isNatural());

            Hand twoCardTwenty = new Hand();
            twoCardTwenty.add(Card.fromCode("AH"));
            twoCardTwenty.add(Card.fromCode("9S"));
            assertFalse(twoCardTwenty.isNatural());
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
            assertEquals("Blackjack! Player wins", ConsoleView.displayText(Outcome.PLAYER_BLACKJACK));
            assertEquals("Player surrenders", ConsoleView.displayText(Outcome.SURRENDER));
        }

        @Test
        void naturalBlackjackOutranksARegularTwentyOne() {
            assertEquals(Outcome.PLAYER_BLACKJACK, Rules.naturalOutcome(true, false));
            assertEquals(Outcome.DEALER_WINS, Rules.naturalOutcome(false, true));
            assertEquals(Outcome.PUSH, Rules.naturalOutcome(true, true));
            assertNull(Rules.naturalOutcome(false, false));
        }

        @Test
        void gameDetectsNaturalsFromTheOpeningHands() {
            Hand natural = new Hand();
            natural.add(Card.fromCode("AH"));
            natural.add(Card.fromCode("QS"));
            Hand ordinary = new Hand();
            ordinary.add(Card.fromCode("9H"));
            ordinary.add(Card.fromCode("9S"));

            Game playerNatural = new Game(new Deck(), natural, ordinary);
            assertEquals(Outcome.PLAYER_BLACKJACK, playerNatural.naturalOutcome());

            Game noNaturals = new Game(new Deck(), ordinary, ordinary);
            assertNull(noNaturals.naturalOutcome());
        }

        @Test
        void payoutsFollowTheDocumentedRates() {
            assertEquals(10, Rules.payout(Outcome.PLAYER_WINS, 10));
            assertEquals(15, Rules.payout(Outcome.PLAYER_BLACKJACK, 10));
            assertEquals(7, Rules.payout(Outcome.PLAYER_BLACKJACK, 5));
            assertEquals(-10, Rules.payout(Outcome.DEALER_WINS, 10));
            assertEquals(0, Rules.payout(Outcome.PUSH, 10));
            assertEquals(-5, Rules.payout(Outcome.SURRENDER, 10));
            assertEquals(-5, Rules.payout(Outcome.SURRENDER, 11));
        }
    }

    @Nested
    class BankrollBehavior {

        @Test
        void bankrollTracksChipsAcrossRounds() {
            Bankroll bankroll = new Bankroll(100);

            bankroll.apply(15);
            assertEquals(115, bankroll.chips());
            bankroll.apply(-40);
            assertEquals(75, bankroll.chips());
            assertFalse(bankroll.isEmpty());
        }

        @Test
        void bankrollKnowsWhatItCanAfford() {
            Bankroll bankroll = new Bankroll(20);

            assertTrue(bankroll.canAfford(20));
            assertFalse(bankroll.canAfford(21));
        }

        @Test
        void bankrollIsEmptyAtZero() {
            Bankroll bankroll = new Bankroll(10);
            bankroll.apply(-10);

            assertTrue(bankroll.isEmpty());
        }
    }

    @Nested
    class CommandParsing {

        @Test
        void knownCommandsParse() {
            assertEquals(Command.HIT, Command.parse("hit"));
            assertEquals(Command.STAND, Command.parse("stand"));
            assertEquals(Command.DOUBLE, Command.parse("double"));
            assertEquals(Command.SURRENDER, Command.parse("surrender"));
            assertEquals(Command.QUIT, Command.parse("q"));
            assertEquals(Command.QUIT, Command.parse("quit"));
        }

        @Test
        void surroundingWhitespaceIsIgnored() {
            assertEquals(Command.HIT, Command.parse("  hit  "));
        }

        @Test
        void anythingElseIsInvalid() {
            assertEquals(Command.INVALID, Command.parse("split"));
            assertEquals(Command.INVALID, Command.parse("HIT"));
            assertEquals(Command.INVALID, Command.parse(""));
        }
    }

    @Nested
    class CliRounds {

        /**
         * Deck seeds discovered for deterministic CLI rounds:
         * seed 0 - no naturals in the first two rounds; opening hands are
         *          player 9S KD (19) vs dealer QD JC (20)
         * seed 1 - no naturals; player opens 6S 4D (10), so one hit can
         *          never bust (dealer AC 7C, 18)
         * seed 6 - player is dealt a natural blackjack (10H AH)
         * seed 8 - dealer is dealt a natural blackjack (KD AH)
         */
        private static final long NO_NATURALS = 0;
        private static final long LOW_OPENING = 1;
        private static final long PLAYER_NATURAL = 6;
        private static final long DEALER_NATURAL = 8;

        private String play(long deckSeed, String typedInput) {
            System.setProperty("blackjack.deck.seed", Long.toString(deckSeed));
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
                System.clearProperty("blackjack.deck.seed");
            }
            return captured.toString(StandardCharsets.UTF_8);
        }

        private int occurrences(String output, String needle) {
            int count = 0;
            int index = output.indexOf(needle);
            while (index >= 0) {
                count++;
                index = output.indexOf(needle, index + needle.length());
            }
            return count;
        }

        @Test
        void standingSettlesTheBetAgainstTheDealer() {
            String output = play(NO_NATURALS, "\nstand\n");

            assertTrue(output.contains("Player value: 19"), output);
            assertTrue(output.contains("Dealer value: 20"), output);
            assertTrue(output.contains("Dealer wins"), output);
            assertTrue(output.contains("You lose 10. Chips: 90"), output);
        }

        @Test
        void playerNaturalBlackjackPaysThreeToTwo() {
            String output = play(PLAYER_NATURAL, "\n");

            assertTrue(output.contains("Blackjack! Player wins"), output);
            assertTrue(output.contains("You win 15. Chips: 115"), output);
        }

        @Test
        void dealerNaturalEndsTheRoundBeforeAnyAction() {
            String output = play(DEALER_NATURAL, "\n");

            assertTrue(output.contains("Dealer wins"), output);
            assertTrue(output.contains("You lose 10. Chips: 90"), output);
            assertFalse(output.contains("Player action>"), output);
        }

        @Test
        void surrenderLosesHalfTheBet() {
            String output = play(NO_NATURALS, "\nsurrender\n");

            assertTrue(output.contains("Player surrenders"), output);
            assertTrue(output.contains("You lose 5. Chips: 95"), output);
        }

        @Test
        void doubleDownPlaysForTwiceTheBet() {
            String output = play(NO_NATURALS, "\ndouble\n");

            assertTrue(output.contains("You win 20.")
                            || output.contains("You lose 20.")
                            || output.contains("Bet returned."),
                    output);
        }

        @Test
        void doubleDownIsOnlyAllowedAsFirstAction() {
            // Seed 1 opens with a 10, so the hit cannot bust and the
            // rejected double is followed by a normal stand.
            String output = play(LOW_OPENING, "\nhit\ndouble\nstand\n");

            assertTrue(output.contains("You can only double as your first action."), output);
        }

        @Test
        void hittingUntilBustLosesTheBet() {
            // 25 hits guarantee a bust even across reshuffles: any hand of
            // 22+ cards exceeds 21.
            String output = play(NO_NATURALS, "\n" + "hit\n".repeat(25));

            assertTrue(output.contains("Player busts. Dealer wins."), output);
            assertTrue(output.contains("You lose 10. Chips: 90"), output);
        }

        @Test
        void invalidCommandForcesAStandAndSettlesTheBet() {
            String output = play(NO_NATURALS, "\nblackjack!\n");

            assertTrue(output.contains("Invalid command. You stand."), output);
            assertTrue(output.contains("Dealer wins"), output);
        }

        @Test
        void invalidBetIsRejectedWithAMessage() {
            String output = play(NO_NATURALS, "abc\n0\n101\n\nstand\n");

            assertEquals(3, occurrences(output, "Enter a whole number between 1 and 100."), output);
            assertTrue(output.contains("Dealer wins"), output);
        }

        @Test
        void sessionSupportsMultipleRounds() {
            String output = play(NO_NATURALS, "\nstand\n\nstand\nq\n");

            assertTrue(occurrences(output, "Player action> ") >= 2, output);
            assertTrue(occurrences(output, "Bet (chips: ") >= 3, output);
            assertTrue(output.contains("Game stopped."), output);
        }

        @Test
        void quitAtTheBetPromptPlaysNoRound() {
            String output = play(NO_NATURALS, "q\n");

            assertTrue(output.contains("Game stopped."), output);
            assertFalse(output.contains("Player action>"), output);
            assertFalse(output.contains("Dealer wins"), output);
        }

        @Test
        void quitMidRoundStopsTheSession() {
            String output = play(NO_NATURALS, "\nq\n");

            assertTrue(output.contains("Player action> "), output);
            assertTrue(output.contains("Game stopped."), output);
        }
    }
}
