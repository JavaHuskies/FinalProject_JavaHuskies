/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.SQLException;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import model.User;
import service.PersistenceService;

/**
 *
 * @author Anan Mikami <mikami.a@northeastern.edu>
 */
public class UserDao extends BaseDaoImpl<User, String> {

    public UserDao() throws SQLException {
        this(PersistenceService.getInstance().getConnectionSource());
    }

    public UserDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, User.class);
    }

}
