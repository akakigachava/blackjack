package blackjack.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * The game's single entry point to the database: players, sessions,
 * completed rounds, and the actions taken in them. All SQL stays in the
 * mapper interfaces; game code never sees it.
 */
public class HistoryRepository {
    private static final Logger LOGGER = Logger.getLogger(HistoryRepository.class.getName());

    private final SqlSessionFactory factory;

    public HistoryRepository(SqlSessionFactory factory) {
        this.factory = factory;
    }

    /** Returns the id for the named player, creating the row if needed. */
    public long ensurePlayer(String name) {
        try (SqlSession session = factory.openSession()) {
            PlayerMapper mapper = session.getMapper(PlayerMapper.class);
            Long id = mapper.findIdByName(name);
            if (id == null) {
                PlayerRecord player = new PlayerRecord();
                player.setName(name);
                player.setCreatedAt(LocalDateTime.now());
                mapper.insert(player);
                id = player.getId();
                LOGGER.info("New player persisted: " + name);
            }
            session.commit();
            return id;
        }
    }

    /** Opens a session row for the player and returns its id. */
    public long startSession(long playerId) {
        try (SqlSession session = factory.openSession()) {
            SessionRecord record = new SessionRecord();
            record.setPlayerId(playerId);
            record.setStartedAt(LocalDateTime.now());
            session.getMapper(SessionMapper.class).insert(record);
            session.commit();
            return record.getId();
        }
    }

    /** Stamps the session's end time. */
    public void endSession(long sessionId) {
        try (SqlSession session = factory.openSession()) {
            session.getMapper(SessionMapper.class).markEnded(sessionId, LocalDateTime.now());
            session.commit();
        }
    }

    /**
     * Persists a completed round and the player actions that led to it,
     * in one transaction. Returns the round id.
     */
    public long saveRound(RoundRecord round, List<String> actions) {
        try (SqlSession session = factory.openSession()) {
            RoundMapper mapper = session.getMapper(RoundMapper.class);
            if (round.getPlayedAt() == null) {
                round.setPlayedAt(LocalDateTime.now());
            }
            mapper.insert(round);
            for (int i = 0; i < actions.size(); i++) {
                mapper.insertAction(round.getId(), i + 1, actions.get(i));
            }
            session.commit();
            LOGGER.info(() -> "Round persisted: session " + round.getSessionId()
                    + " round " + round.getRoundNumber() + " outcome " + round.getOutcome());
            return round.getId();
        }
    }
}
