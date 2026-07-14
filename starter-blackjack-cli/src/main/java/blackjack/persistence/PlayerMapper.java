package blackjack.persistence;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface PlayerMapper {

    @Select("SELECT id FROM players WHERE name = #{name}")
    Long findIdByName(String name);

    @Insert("INSERT INTO players (name, created_at) VALUES (#{name}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PlayerRecord player);
}
