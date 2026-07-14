package blackjack.persistence;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * Builds the MyBatis {@link SqlSessionFactory} and applies the schema.
 *
 * Connection settings come from system properties or the environment so no
 * credentials live in source code. Lookup order for each setting: system
 * property, then environment variable, then default.
 *   blackjack.db.url      / BLACKJACK_DB_URL      (default: jdbc:h2:file:./data/blackjack)
 *   blackjack.db.user     / BLACKJACK_DB_USER     (default: sa, the H2 embedded default)
 *   blackjack.db.password / BLACKJACK_DB_PASSWORD (default: empty)
 */
public final class Database {

    public static final String DEFAULT_URL = "jdbc:h2:file:./data/blackjack";
    private static final String SCHEMA_RESOURCE = "/db/schema.sql";

    private Database() {
    }

    /** Factory for the URL/user/password from properties/environment (or defaults). */
    public static SqlSessionFactory fromEnvironment() {
        String url = setting("blackjack.db.url", "BLACKJACK_DB_URL", DEFAULT_URL);
        String user = setting("blackjack.db.user", "BLACKJACK_DB_USER", "sa");
        String password = setting("blackjack.db.password", "BLACKJACK_DB_PASSWORD", "");
        return open(url, user, password);
    }

    /** Factory for an explicit JDBC URL; used directly by tests. */
    public static SqlSessionFactory open(String jdbcUrl, String user, String password) {
        UnpooledDataSource dataSource =
                new UnpooledDataSource("org.h2.Driver", jdbcUrl, user, password);
        Environment environment =
                new Environment("blackjack", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(PlayerMapper.class);
        configuration.addMapper(SessionMapper.class);
        configuration.addMapper(RoundMapper.class);

        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
        applySchema(factory);
        return factory;
    }

    private static void applySchema(SqlSessionFactory factory) {
        try (SqlSession session = factory.openSession();
             InputStream schema = Database.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (schema == null) {
                throw new IllegalStateException("Schema resource not found: " + SCHEMA_RESOURCE);
            }
            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runner.runScript(new InputStreamReader(schema, StandardCharsets.UTF_8));
            session.commit();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to apply database schema", e);
        }
    }

    private static String setting(String property, String envName, String fallback) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        return value == null || value.isBlank() ? fallback : value;
    }
}
