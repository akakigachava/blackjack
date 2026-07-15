import blackjack.Deck;
import blackjack.Game;
import blackjack.persistence.Database;
import blackjack.persistence.HistoryRepository;
import blackjack.persistence.PlayerBankrollHigh;
import blackjack.persistence.PlayerOutcomes;
import blackjack.persistence.PlayerRoundAverage;
import blackjack.persistence.RoundHistoryEntry;
import blackjack.persistence.RoundRecord;
import blackjack.persistence.SessionRecorder;
import blackjack.persistence.SessionSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Persistence and report-query tests. Every test runs against its own
 * throwaway in-memory H2 database, so nothing touches the real game
 * history or any machine-specific state.
 */
class BlackjackPersistenceTest {

    static {
        java.util.logging.Logger.getLogger("blackjack").setUseParentHandlers(false);
    }

    private static int dbCounter = 0;

    private HistoryRepository repository;

    @BeforeEach
    void freshDatabase() {
        String url = "jdbc:h2:mem:persistence-test-" + (++dbCounter) + ";DB_CLOSE_DELAY=-1";
        repository = new HistoryRepository(Database.open(url, "sa", ""));
    }

    private long sessionFor(String playerName) {
        return repository.startSession(repository.ensurePlayer(playerName));
    }

    private void saveRound(long sessionId, int roundNumber, String outcome, String... actions) {
        saveBettedRound(sessionId, roundNumber, outcome, 10, 0, 100, actions);
    }

    private void saveBettedRound(long sessionId, int roundNumber, String outcome,
                                 int bet, int change, int after, String... actions) {
        RoundRecord round = new RoundRecord();
        round.setSessionId(sessionId);
        round.setRoundNumber(roundNumber);
        round.setPlayerCards("AH 3H");
        round.setDealerCards("2H 4H 5H 6H");
        round.setPlayerValue(14);
        round.setDealerValue(17);
        round.setOutcome(outcome);
        round.setBet(bet);
        round.setBankrollChange(change);
        round.setBankrollAfter(after);
        repository.saveRound(round, List.of(actions));
    }

    @Nested
    class PlayersAndSessions {

        @Test
        void ensurePlayerCreatesThePlayerOnce() {
            long first = repository.ensurePlayer("Akaki");
            long second = repository.ensurePlayer("Akaki");
            long other = repository.ensurePlayer("Dealerbane");

            assertEquals(first, second);
            assertTrue(other != first);
        }

        @Test
        void sessionsRecordStartAndEndTimestamps() {
            long sessionId = sessionFor("Akaki");
            repository.endSession(sessionId);

            SessionSummary summary = repository.recentSessions(1).get(0);
            assertEquals(sessionId, summary.getSessionId());
            assertEquals("Akaki", summary.getPlayerName());
            assertNotNull(summary.getStartedAt());
            assertNotNull(summary.getEndedAt());
        }

        @Test
        void unfinishedSessionHasNoEndTimestamp() {
            sessionFor("Akaki");

            assertNull(repository.recentSessions(1).get(0).getEndedAt());
        }
    }

    @Nested
    class Rounds {

        @Test
        void savedRoundKeepsHandsValuesOutcomeAndActions() {
            long sessionId = sessionFor("Akaki");
            saveRound(sessionId, 1, "DEALER_WINS", "HIT", "STAND");

            RoundHistoryEntry round = repository.recentRounds(1).get(0);
            assertEquals("Akaki", round.getPlayerName());
            assertEquals(sessionId, round.getSessionId());
            assertEquals(1, round.getRoundNumber());
            assertEquals("AH 3H", round.getPlayerCards());
            assertEquals("2H 4H 5H 6H", round.getDealerCards());
            assertEquals(14, round.getPlayerValue());
            assertEquals(17, round.getDealerValue());
            assertEquals("DEALER_WINS", round.getOutcome());
            assertNotNull(round.getPlayedAt());
        }

        @Test
        void savedRoundKeepsBetAndBankrollChanges() {
            long sessionId = sessionFor("Akaki");
            saveBettedRound(sessionId, 1, "PLAYER_BLACKJACK", 10, 15, 115, "STAND");

            RoundHistoryEntry round = repository.recentRounds(1).get(0);
            assertEquals(10, round.getBet());
            assertEquals(15, round.getBankrollChange());
            assertEquals(115, round.getBankrollAfter());
        }
    }

    @Nested
    class Reports {

        @Test
        void recentSessionsListsNewestFirstWithRoundCounts() {
            long older = sessionFor("Akaki");
            saveRound(older, 1, "PUSH", "STAND");
            long newer = sessionFor("Akaki");

            List<SessionSummary> sessions = repository.recentSessions(10);

            assertEquals(2, sessions.size());
            assertEquals(newer, sessions.get(0).getSessionId());
            assertEquals(0, sessions.get(0).getRoundsPlayed());
            assertEquals(older, sessions.get(1).getSessionId());
            assertEquals(1, sessions.get(1).getRoundsPlayed());
        }

        @Test
        void playerOutcomeCountsAggregateAllOutcomeKinds() {
            long sessionId = sessionFor("Akaki");
            saveRound(sessionId, 1, "PLAYER_WINS", "STAND");
            saveRound(sessionId, 2, "PLAYER_BLACKJACK", "STAND");
            saveRound(sessionId, 3, "DEALER_WINS", "STAND");
            saveRound(sessionId, 4, "PUSH", "STAND");
            saveRound(sessionId, 5, "SURRENDER", "SURRENDER");

            List<PlayerOutcomes> outcomes = repository.playerOutcomeCounts();

            assertEquals(1, outcomes.size());
            PlayerOutcomes akaki = outcomes.get(0);
            assertEquals("Akaki", akaki.getPlayerName());
            assertEquals(2, akaki.getWins());
            assertEquals(1, akaki.getBlackjacks());
            assertEquals(1, akaki.getLosses());
            assertEquals(1, akaki.getPushes());
            assertEquals(1, akaki.getSurrenders());
            assertEquals(5, akaki.getTotalRounds());
        }

        @Test
        void highestBankrollReportsTheBestBalanceReached() {
            long akaki = sessionFor("Akaki");
            saveBettedRound(akaki, 1, "PLAYER_WINS", 10, 10, 110, "STAND");
            saveBettedRound(akaki, 2, "PLAYER_BLACKJACK", 10, 15, 125, "STAND");
            saveBettedRound(akaki, 3, "DEALER_WINS", 20, -20, 105, "STAND");
            long rival = sessionFor("Dealerbane");
            saveBettedRound(rival, 1, "DEALER_WINS", 50, -50, 50, "STAND");

            List<PlayerBankrollHigh> highs = repository.highestBankrolls();

            assertEquals(2, highs.size());
            assertEquals("Akaki", highs.get(0).getPlayerName());
            assertEquals(125, highs.get(0).getHighestBankroll());
            assertEquals("Dealerbane", highs.get(1).getPlayerName());
            assertEquals(50, highs.get(1).getHighestBankroll());
        }

        @Test
        void averageRoundsPerSessionDividesRoundsBySessions() {
            long first = sessionFor("Akaki");
            saveRound(first, 1, "PUSH", "STAND");
            saveRound(first, 2, "PLAYER_WINS", "STAND");
            long second = sessionFor("Akaki");
            saveRound(second, 1, "DEALER_WINS", "STAND");

            PlayerRoundAverage average = repository.averageRoundsPerSession().get(0);

            assertEquals("Akaki", average.getPlayerName());
            assertEquals(2, average.getSessionCount());
            assertEquals(3, average.getRoundCount());
            assertEquals(1.5, average.getAverageRoundsPerSession(), 0.001);
        }

        @Test
        void recentRoundsHonorsTheLimitNewestFirst() {
            long sessionId = sessionFor("Akaki");
            saveRound(sessionId, 1, "PUSH", "STAND");
            saveRound(sessionId, 2, "DEALER_WINS", "STAND");
            saveRound(sessionId, 3, "PLAYER_WINS", "STAND");

            List<RoundHistoryEntry> rounds = repository.recentRounds(2);

            assertEquals(2, rounds.size());
            assertEquals(3, rounds.get(0).getRoundNumber());
            assertEquals(2, rounds.get(1).getRoundNumber());
        }
    }

    @Nested
    class RecorderIntegration {

        private String previousUrl;

        private void useDatabaseUrl(String url) {
            previousUrl = System.getProperty("blackjack.db.url");
            System.setProperty("blackjack.db.url", url);
        }

        private void restoreDatabaseUrl() {
            if (previousUrl == null) {
                System.clearProperty("blackjack.db.url");
            } else {
                System.setProperty("blackjack.db.url", previousUrl);
            }
        }

        @Test
        void recorderPersistsARealGameRound() {
            String url = "jdbc:h2:mem:recorder-test-" + (++dbCounter) + ";DB_CLOSE_DELAY=-1";
            useDatabaseUrl(url);
            try (SessionRecorder recorder = SessionRecorder.start("RecorderTest")) {
                Game game = new Game(new Deck());
                game.startRound();
                game.dealerPlay();
                recorder.recordRound(game, game.outcome(), List.of("STAND"), 10, -10, 90);
            } finally {
                restoreDatabaseUrl();
            }

            HistoryRepository written = new HistoryRepository(Database.open(url, "sa", ""));
            RoundHistoryEntry round = written.recentRounds(1).get(0);
            assertEquals("RecorderTest", round.getPlayerName());
            assertEquals("AH 3H", round.getPlayerCards());
            assertEquals("DEALER_WINS", round.getOutcome());
            assertEquals(10, round.getBet());
            assertEquals(-10, round.getBankrollChange());
            assertEquals(90, round.getBankrollAfter());
            assertNotNull(written.recentSessions(1).get(0).getEndedAt());
        }

        @Test
        void recorderDegradesToNoOpWhenDatabaseIsUnavailable() {
            useDatabaseUrl("jdbc:h2:tcp://127.0.0.1:1/unreachable");
            try {
                assertDoesNotThrow(() -> {
                    try (SessionRecorder recorder = SessionRecorder.start("Nobody")) {
                        Game game = new Game(new Deck());
                        game.startRound();
                        recorder.recordRound(game, game.outcome(), List.of("STAND"), 10, -10, 90);
                    }
                });
            } finally {
                restoreDatabaseUrl();
            }
        }
    }
}
