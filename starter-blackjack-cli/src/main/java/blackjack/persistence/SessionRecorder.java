package blackjack.persistence;

import blackjack.Card;
import blackjack.Game;
import blackjack.Outcome;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Records one CLI session: opens the session row on start, appends each
 * completed round, and stamps the end time on close. If the database
 * cannot be opened the recorder degrades to a no-op so the game itself
 * always remains playable.
 */
public class SessionRecorder implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SessionRecorder.class.getName());

    private final HistoryRepository repository;
    private final long sessionId;
    private int roundsRecorded;

    private SessionRecorder(HistoryRepository repository, long sessionId) {
        this.repository = repository;
        this.sessionId = sessionId;
    }

    /** Opens a session for the named player, or a no-op recorder on DB failure. */
    public static SessionRecorder start(String playerName) {
        try {
            HistoryRepository repository = new HistoryRepository(Database.fromEnvironment());
            long playerId = repository.ensurePlayer(playerName);
            long sessionId = repository.startSession(playerId);
            LOGGER.info(() -> "Session " + sessionId + " started for player " + playerName);
            return new SessionRecorder(repository, sessionId);
        } catch (RuntimeException e) {
            LOGGER.warning("Persistence unavailable, playing without history: " + e.getMessage());
            return new SessionRecorder(null, -1);
        }
    }

    /** Persists the finished round and the player actions that led to it. */
    public void recordRound(Game game, Outcome outcome, List<String> actions) {
        if (repository == null) {
            return;
        }
        RoundRecord round = new RoundRecord();
        round.setSessionId(sessionId);
        round.setRoundNumber(++roundsRecorded);
        round.setPlayerCards(cardCodes(game.playerCards()));
        round.setDealerCards(cardCodes(game.dealerCards()));
        round.setPlayerValue(game.playerValue());
        round.setDealerValue(game.dealerValue());
        round.setOutcome(outcome.name());
        repository.saveRound(round, actions);
    }

    @Override
    public void close() {
        if (repository != null) {
            repository.endSession(sessionId);
            LOGGER.info(() -> "Session " + sessionId + " ended after "
                    + roundsRecorded + " round(s)");
        }
    }

    private static String cardCodes(List<Card> cards) {
        return cards.stream().map(Card::toString).collect(Collectors.joining(" "));
    }
}
