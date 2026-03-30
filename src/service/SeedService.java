package service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Initializes the database schema and static seed data on first launch.
 *
 * Sequence:
 *   1. Run schema.sql  — creates all tables (IF NOT EXISTS, safe to re-run)
 *   2. Run seed.sql    — inserts network, enterprises, orgs (INSERT OR IGNORE)
 *   3. seedUsers()     — inserts stub admin users with BCrypt hashes computed
 *                        at runtime via AuthService (INSERT OR IGNORE)
 *
 * <p>TRANSITIONAL: This class is a bootstrap shim only. Once Anan delivers
 * PersistenceService, the startup call in ApplicationFrame switches from
 * SeedService.initialize() to PersistenceService.initializeSchema().
 * See handoff doc — SeedService section.
 *
 * Call SeedService.initialize(connection) from ApplicationFrame on startup.
 */
public class SeedService {

    private static final Logger log = Logger.getLogger(SeedService.class.getName());

    // Default password for all seed accounts — must satisfy password policy
    private static final String defaultPassword = "Admin1!";

    // ── Public entry point ────────────────────────────────────────────────────

    /**
     * Runs schema + seed scripts then inserts stub users.
     * Safe to call on every launch — all operations use INSERT OR IGNORE.
     *
     * @param conn open JDBC connection
     */
    public static void initialize(Connection conn) {
        try {
            runScript(conn, "schema.sql");
            runScript(conn, "seed.sql");
            seedUsers(conn);
            log.info("SeedService: database initialized successfully");
        } catch (Exception e) {
            log.severe("SeedService: initialization failed — " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    // ── Script runner ─────────────────────────────────────────────────────────

    /**
     * Reads a SQL script from the classpath root and executes each statement.
     * Statements are split on semicolons; blank lines and comments are skipped.
     *
     * @param conn         open JDBC connection
     * @param resourceName filename of the SQL script on the classpath root
     * @throws Exception if the script is not found or a statement fails
     */
    private static void runScript(Connection conn, String resourceName)
            throws Exception {
        InputStream is = SeedService.class.getClassLoader()
                .getResourceAsStream(resourceName);
        if (is == null) {
            throw new IllegalStateException(
                "SQL script not found on classpath: " + resourceName);
        }

        String sql;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
                sb.append(line).append('\n');
            }
            sql = sb.toString();
        }

        try (Statement stmt = conn.createStatement()) {
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.executeUpdate(trimmed);
                }
            }
        }
        log.fine("SeedService: executed " + resourceName);
    }

    // ── User seed ─────────────────────────────────────────────────────────────

    /**
     * Inserts stub users if they don't already exist.
     * Passwords are hashed at runtime — never hardcoded in SQL.
     * All role values match the CHECK constraint in schema.sql and the
     * Claims role constants.
     *
     * @param conn open JDBC connection
     * @throws SQLException if any insert fails
     */
    private static void seedUsers(Connection conn) throws SQLException {
        AuthService auth = AuthService.getInstance();
        String hash = auth.hashPassword(defaultPassword);

        // columns: user_id, org_id, enterprise_id, first_name, last_name, email, role
        Object[][] users = {

            // ── Network level ─────────────────────────────────────────────────
            { "admin",    "slartibartfastPictures", "magratheaStudios",
              "Arthur",  "Dent",        "admin@deepthought.com",    "networkAdmin"       },
            { "netadmin", "slartibartfastPictures", "magratheaStudios",
              "Ford",    "Prefect",     "netadmin@deepthought.com", "networkAdmin"       },

            // ── Group level ───────────────────────────────────────────────────
            { "grpceo",   "slartibartfastPictures", "magratheaStudios",
              "Zaphod",  "Beeblebrox",  "grpceo@deepthought.com",   "groupCeo"           },
            { "grpcfo",   "slartibartfastPictures", "magratheaStudios",
              "Humma",   "Kavula",      "grpcfo@deepthought.com",   "groupCfo"           },

            // ── Enterprise level ──────────────────────────────────────────────
            { "entadmin", "magratheaThemeWorlds",   "starshipTitanicLeisure",
              "Trillian","McMillan",    "entadmin@deepthought.com", "enterpriseAdmin"    },
            { "entpres",  "milliwaysEntertainment", "starshipTitanicLeisure",
              "Slartibartfast", "—",   "entpres@deepthought.com",  "enterprisePresident"},

            // ── Org level ─────────────────────────────────────────────────────
            { "orgdir1",  "infiniteImprobabilityStreaming", "galacticBroadcasting",
              "Marvin",  "Android",     "orgdir1@deepthought.com",  "orgDirector"        },
            { "creative1","bistromathAnimation",    "magratheaStudios",
              "Fenchurch","—",          "creative1@deepthought.com","creativeLead"       },
            { "tech1",    "megadodoLicensing",      "siriusCybernetics",
              "Eddie",   "Ship",        "tech1@deepthought.com",    "technologyLead"     },
            { "mktg1",    "panGalacticBroadcast",   "galacticBroadcasting",
              "Veet",    "Voojagig",    "mktg1@deepthought.com",    "marketingLead"      },
            { "analyst1", "hooloovooRetail",         "siriusCybernetics",
              "Deep",    "Thought",     "analyst1@deepthought.com", "dataAnalyst"        },

            // ── Compliance Officers — one per enterprise ───────────────────────
            { "comply1",  "slartibartfastPictures", "magratheaStudios",
              "Wonko",   "Sane",        "comply1@deepthought.com",  "complianceOfficer"  },
            { "comply2",  "milliwaysEntertainment", "starshipTitanicLeisure",
              "Effrafax","Wug",         "comply2@deepthought.com",  "complianceOfficer"  },
            { "comply3",  "infiniteImprobabilityStreaming", "galacticBroadcasting",
              "Prak",    "—",           "comply3@deepthought.com",  "complianceOfficer"  },
            { "comply4",  "megadodoLicensing",      "siriusCybernetics",
              "Agrajag", "—",           "comply4@deepthought.com",  "complianceOfficer"  },
        };

        String sql = """
            INSERT OR IGNORE INTO user
                (user_id, org_id, enterprise_id, first_name, last_name,
                 email, password_hash, role)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] u : users) {
                ps.setString(1, (String) u[0]); // user_id
                ps.setString(2, (String) u[1]); // org_id
                ps.setString(3, (String) u[2]); // enterprise_id
                ps.setString(4, (String) u[3]); // first_name
                ps.setString(5, (String) u[4]); // last_name
                ps.setString(6, (String) u[5]); // email
                ps.setString(7, hash);           // password_hash
                ps.setString(8, (String) u[6]); // role
                ps.executeUpdate();
            }
        }
        log.info("SeedService: " + users.length + " seed users inserted (or already existed)");
    }
}