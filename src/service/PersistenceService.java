package service;

import java.sql.SQLException;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Booking;
import model.CasinoSession;
import model.Complaint;
import model.Enterprise;
import model.GameRound;
import model.Guest;
import model.Network;
import model.NotificationLog;
import model.Organization;
import model.Report;
import model.StatusChange;
import model.User;
import model.WorkRequest;
import model.WorkRequestComment;

public class PersistenceService {
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
}
