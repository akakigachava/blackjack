package blackjack.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Select;

/** Report queries over the persisted game history. */
public interface StatsMapper {

    @Select("SELECT s.id AS session_id, p.name AS player_name, s.started_at, s.ended_at,"
            + " COUNT(r.id) AS rounds_played"
            + " FROM sessions s"
            + " JOIN players p ON s.player_id = p.id"
            + " LEFT JOIN rounds r ON r.session_id = s.id"
            + " GROUP BY s.id, p.name, s.started_at, s.ended_at"
            + " ORDER BY s.started_at DESC, s.id DESC"
            + " LIMIT #{limit}")
    List<SessionSummary> recentSessions(int limit);

    @Select("SELECT p.name AS player_name,"
            + " SUM(CASE WHEN r.outcome = 'PLAYER_WINS' THEN 1 ELSE 0 END) AS wins,"
            + " SUM(CASE WHEN r.outcome = 'DEALER_WINS' THEN 1 ELSE 0 END) AS losses,"
            + " SUM(CASE WHEN r.outcome = 'PUSH' THEN 1 ELSE 0 END) AS pushes,"
            + " COUNT(r.id) AS total_rounds"
            + " FROM players p"
            + " JOIN sessions s ON s.player_id = p.id"
            + " JOIN rounds r ON r.session_id = s.id"
            + " GROUP BY p.name"
            + " ORDER BY wins DESC, p.name")
    List<PlayerOutcomes> playerOutcomeCounts();

    @Select("SELECT p.name AS player_name,"
            + " COUNT(DISTINCT s.id) AS session_count,"
            + " COUNT(r.id) AS round_count,"
            + " CAST(COUNT(r.id) AS DOUBLE) / COUNT(DISTINCT s.id) AS average_rounds_per_session"
            + " FROM players p"
            + " JOIN sessions s ON s.player_id = p.id"
            + " LEFT JOIN rounds r ON r.session_id = s.id"
            + " GROUP BY p.name"
            + " ORDER BY p.name")
    List<PlayerRoundAverage> averageRoundsPerSession();

    @Select("SELECT p.name AS player_name, r.session_id, r.round_number,"
            + " r.player_cards, r.dealer_cards, r.player_value, r.dealer_value,"
            + " r.outcome, r.played_at"
            + " FROM rounds r"
            + " JOIN sessions s ON r.session_id = s.id"
            + " JOIN players p ON s.player_id = p.id"
            + " ORDER BY r.played_at DESC, r.id DESC"
            + " LIMIT #{limit}")
    List<RoundHistoryEntry> recentRounds(int limit);
}
