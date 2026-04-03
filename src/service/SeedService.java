package service;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import com.github.javafaker.Faker;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.table.TableUtils;
import model.Booking;
import model.Complaint;
import model.Enterprise;
import model.Guest;
import model.Network;
import model.NotificationLog;
import model.Organization;
import model.Report;
import model.StatusChange;
import model.User;
import model.UserRole;
import model.WorkRequest;
import model.WorkRequestComment;
import model.Casino.CasinoSession;
import model.Casino.GameRound;

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
    private static final String DEFAULT_PASSWORD = "Admin1!";

    // ── Public entry point ────────────────────────────────────────────────────

    /**
     * Runs schema + seed scripts then inserts stub users.
     * Safe to call on every launch — all operations use INSERT OR IGNORE.
     *
     * @param connectionSource JDBC connection
     */
    public static void initialize(JdbcConnectionSource connectionSource) throws Exception {    
        initializeSchema(connectionSource);
        seedAll(connectionSource);
    }

    public static void initializeSchema(JdbcConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Network.class);
        TableUtils.createTableIfNotExists(connectionSource, Enterprise.class);
        TableUtils.createTableIfNotExists(connectionSource, Organization.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Guest.class);
        TableUtils.createTableIfNotExists(connectionSource, WorkRequest.class);
        TableUtils.createTableIfNotExists(connectionSource, StatusChange.class);
        TableUtils.createTableIfNotExists(connectionSource, WorkRequestComment.class);
        TableUtils.createTableIfNotExists(connectionSource, Booking.class);
        TableUtils.createTableIfNotExists(connectionSource, Complaint.class);
        TableUtils.createTableIfNotExists(connectionSource, CasinoSession.class);
        TableUtils.createTableIfNotExists(connectionSource, GameRound.class);
        TableUtils.createTableIfNotExists(connectionSource, Report.class);
        TableUtils.createTableIfNotExists(connectionSource, NotificationLog.class);
    }

    private static void seedAll(JdbcConnectionSource connectionSource) throws SQLException {
        Network network = seedNetwork(connectionSource);
        List<Enterprise> enterprises = seedEnterprises(connectionSource, network);
        List<Organization> orgs = seedOrganizations(connectionSource, enterprises);
        seedUsers(connectionSource, orgs, enterprises);
    }

    private static Network seedNetwork(JdbcConnectionSource connectionSource) throws SQLException {
        Dao<Network, String> dao = DaoManager.createDao(connectionSource, Network.class);

        long count = dao.countOf();
        log.info("SeedService.seedNetwork: countOf=" + count);
        if (count == 0) {
            Faker faker = new Faker();
            Network network = new Network(
                faker.internet().uuid(),
                "Deep Thought Entertainment Group",
                "Magrathea",
                "1979"
            );
            dao.create(network);
            log.info("SeedService.seedNetwork: inserted rows=" + 1);
            return network;
        }

        return dao.queryForAll().get(0);
    }

    private static List<Enterprise> seedEnterprises(JdbcConnectionSource connectionSource, Network network) throws SQLException {
        Dao<Enterprise, String> dao = DaoManager.createDao(connectionSource, Enterprise.class);

        long count = dao.countOf();
        log.info("SeedService.seedEnterprises: countOf=" + count);
        if (count == 0) {
            List<Enterprise> enterprises = List.of(
                new Enterprise("magratheaStudios",       network, "Magrathea Studios",        "Production"),
                new Enterprise("starshipTitanicLeisure", network, "Starship Titanic Leisure",  "Hospitality"),
                new Enterprise("galacticBroadcasting",   network, "Galactic Broadcasting",     "Media"),
                new Enterprise("siriusCybernetics",      network, "Sirius Cybernetics",        "Technology")
            );
            TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
                public Void call() throws Exception {
                    for (Enterprise e : enterprises) {
                        dao.createIfNotExists(e);
                    }
                    return null;
                }
            });
            log.info("SeedService.seedEnterprises: inserted " + enterprises.size() + " enterprises");
        }

        return dao.queryForAll();
    }

    private static List<Organization> seedOrganizations(JdbcConnectionSource connectionSource, List<Enterprise> enterprises) throws SQLException {
        Dao<Organization, String> dao = DaoManager.createDao(connectionSource, Organization.class);

        long count = dao.countOf();
        log.info("SeedService.seedOrganizations: countOf=" + count);
        if (count == 0) {
            Enterprise magratheaStudios       = enterprises.get(0);
            Enterprise starshipTitanicLeisure = enterprises.get(1);
            Enterprise galacticBroadcasting   = enterprises.get(2);
            Enterprise siriusCybernetics      = enterprises.get(3);

            List<Organization> orgs = List.of(
                new Organization("slartibartfastPictures",         magratheaStudios,       "Slartibartfast Pictures",          "Film"),
                new Organization("bistromathAnimation",            magratheaStudios,       "Bistromath Animation",             "Animation"),
                new Organization("magratheaThemeWorlds",           starshipTitanicLeisure, "Magrathea Theme Worlds",           "Theme Park"),
                new Organization("milliwaysEntertainment",         starshipTitanicLeisure, "Milliways Entertainment",          "Events"),
                new Organization("infiniteImprobabilityStreaming", galacticBroadcasting,   "Infinite Improbability Streaming", "Streaming"),
                new Organization("panGalacticBroadcast",          galacticBroadcasting,   "Pan-Galactic Broadcast",           "Broadcast"),
                new Organization("megadodoLicensing",             siriusCybernetics,      "Megadodo Licensing",               "Licensing"),
                new Organization("hooloovooRetail",               siriusCybernetics,      "Hooloovoo Retail",                 "Retail")
            );
            TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
                public Void call() throws Exception {
                    for (Organization o : orgs) {
                        dao.createIfNotExists(o);
                    }
                    return null;
                }
            });
            log.info("SeedService.seedOrganizations: inserted " + orgs.size() + " organizations");
        }

        return dao.queryForAll();
    }

    private static void seedUsers(JdbcConnectionSource connectionSource, List<Organization> orgs, List<Enterprise> enterprises) throws SQLException {
        Dao<User, String> dao = DaoManager.createDao(connectionSource, User.class);

        long count = dao.countOf();
        log.info("SeedService.seedUsers: countOf=" + count);
        if (count > 0) return;

        String hash = AuthService.getInstance().hashPassword(DEFAULT_PASSWORD);
        Faker faker = new Faker();
        UserRole[] roles = UserRole.values();

        Enterprise magratheaStudios       = enterprises.get(0);
        Enterprise starshipTitanicLeisure = enterprises.get(1);
        Enterprise galacticBroadcasting   = enterprises.get(2);
        Enterprise siriusCybernetics      = enterprises.get(3);

        Organization slartibartfastPictures         = orgs.get(0);
        Organization bistromathAnimation            = orgs.get(1);
        Organization magratheaThemeWorlds           = orgs.get(2);
        Organization milliwaysEntertainment         = orgs.get(3);
        Organization infiniteImprobabilityStreaming = orgs.get(4);
        Organization panGalacticBroadcast           = orgs.get(5);
        Organization megadodoLicensing              = orgs.get(6);
        Organization hooloovooRetail                = orgs.get(7);

        List<User> users = List.of(
            new User("admin",      slartibartfastPictures,         magratheaStudios,       "Arthur",         "Dent",       "admin@deepthought.com",     hash, UserRole.networkAdmin),
            new User("netadmin",   slartibartfastPictures,         magratheaStudios,       "Ford",           "Prefect",    "netadmin@deepthought.com",  hash, UserRole.networkAdmin),
            new User("grpceo",     slartibartfastPictures,         magratheaStudios,       "Zaphod",         "Beeblebrox", "grpceo@deepthought.com",    hash, UserRole.groupCeo),
            new User("grpcfo",     slartibartfastPictures,         magratheaStudios,       "Humma",          "Kavula",     "grpcfo@deepthought.com",    hash, UserRole.groupCfo),
            new User("entadmin",   magratheaThemeWorlds,           starshipTitanicLeisure, "Trillian",       "McMillan",   "entadmin@deepthought.com",  hash, UserRole.enterpriseAdmin),
            new User("entpres",    milliwaysEntertainment,         starshipTitanicLeisure, "Slartibartfast", "-",         "entpres@deepthought.com",   hash, UserRole.enterprisePresident),
            new User("orgdir1",    infiniteImprobabilityStreaming, galacticBroadcasting,   "Marvin",         "Android",    "orgdir1@deepthought.com",   hash, UserRole.orgDirector),
            new User("creative1",  bistromathAnimation,            magratheaStudios,       "Fenchurch",      "-",         "creative1@deepthought.com", hash, UserRole.creativeLead),
            new User("tech1",      megadodoLicensing,              siriusCybernetics,      "Eddie",          "Ship",       "tech1@deepthought.com",     hash, UserRole.technologyLead),
            new User("mktg1",      panGalacticBroadcast,           galacticBroadcasting,   "Veet",           "Voojagig",   "mktg1@deepthought.com",     hash, UserRole.marketingLead),
            new User("analyst1",   hooloovooRetail,                siriusCybernetics,      "Deep",           "Thought",    "analyst1@deepthought.com",  hash, UserRole.dataAnalyst),
            new User("comply1",    slartibartfastPictures,         magratheaStudios,       "Wonko",          "Sane",       "comply1@deepthought.com",   hash, UserRole.complianceOfficer),
            new User("comply2",    milliwaysEntertainment,         starshipTitanicLeisure, "Effrafax",       "Wug",        "comply2@deepthought.com",   hash, UserRole.complianceOfficer),
            new User("comply3",    infiniteImprobabilityStreaming, galacticBroadcasting,   "Prak",           "-",         "comply3@deepthought.com",   hash, UserRole.complianceOfficer),
            new User("comply4",    megadodoLicensing,              siriusCybernetics,      "Agrajag",        "-",         "comply4@deepthought.com",   hash, UserRole.complianceOfficer)
        );

        TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
            public Void call() throws Exception {
                for (User user : users) {
                    dao.createIfNotExists(user);
                }
                return null;
            }
        });
    }
}
