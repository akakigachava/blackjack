package blackjack.persistence;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

public interface RoundMapper {

    @Insert("INSERT INTO rounds (session_id, round_number, player_cards, dealer_cards,"
            + " player_value, dealer_value, outcome, played_at)"
            + " VALUES (#{sessionId}, #{roundNumber}, #{playerCards}, #{dealerCards},"
            + " #{playerValue}, #{dealerValue}, #{outcome}, #{playedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RoundRecord round);

    @Insert("INSERT INTO round_actions (round_id, seq, action)"
            + " VALUES (#{roundId}, #{seq}, #{action})")
    void insertAction(@Param("roundId") long roundId,
                      @Param("seq") int seq,
                      @Param("action") String action);
}
