package blackjack.persistence;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface SessionMapper {

    @Insert("INSERT INTO sessions (player_id, started_at) VALUES (#{playerId}, #{startedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SessionRecord session);

    @Update("UPDATE sessions SET ended_at = #{endedAt} WHERE id = #{id}")
    void markEnded(@Param("id") long id, @Param("endedAt") LocalDateTime endedAt);
}
