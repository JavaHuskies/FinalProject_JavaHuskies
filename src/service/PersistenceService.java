package service;

import java.sql.SQLException;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

public class PersistenceService {

    private static PersistenceService instance;

    /** Database (SQLite connector) */ 
    private JdbcConnectionSource connectionSource;

    public static PersistenceService getInstance() throws SQLException {
        if (instance == null) {
            instance = new PersistenceService();
        }
        return instance;
    }

    private PersistenceService() throws SQLException {
        initialize();
    }

    public final void initialize() throws SQLException {
        if (connectionSource != null) {
            return;
        }

        ConfigService config = ConfigService.getInstance();
        String dbUrl = config.get("db.url");
        // JdbcConnectionSource implements AutoCloseable
        connectionSource = new JdbcConnectionSource(dbUrl);
    }

    public JdbcConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
