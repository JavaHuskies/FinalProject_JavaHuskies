package dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import model.User;

public class UserDao extends BaseDaoImpl<User, String> {

    public UserDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, User.class);
    }

    public User findByEmail(String email) throws SQLException {
        return queryBuilder()
            .where().eq("email", email)
            .queryForFirst();
    }
}
