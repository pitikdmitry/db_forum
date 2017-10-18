package db.forum.service;

import db.forum.model.User;
import db.forum.sqlQueries.UserQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.CallSite;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User create(User user, String nickname) {
        Connection con = null;
        CallableStatement proc = null;
        User resUser = null;
        String sql = "add_user(?::citext, ?::citext, ?, ?, ?, ?, ?, ?, ?)";

        HashMap<Integer, Object> parameters = new HashMap<>();
        parameters.put(1, nickname);
        parameters.put(2, user.getEmail());
        parameters.put(3, user.getAbout());
        parameters.put(4, user.getFullname());

        HashMap<Integer, Integer> outParameters = new HashMap<>();
        outParameters.put(5, Types.INTEGER);
        outParameters.put(6, Types.OTHER);
        outParameters.put(7, Types.OTHER);
        outParameters.put(8, Types.VARCHAR);
        outParameters.put(9, Types.VARCHAR);

        try {
            con = DbConnection.getConnection();

            proc = DbConnection.prepareCall(sql, con, parameters, outParameters);

            resUser = new User(proc.getInt(5), proc.getObject(6).toString(), proc.getObject(7).toString(),
                                        proc.getString(8), proc.getString(9));

            DbConnection.closeConnection(con);
        }
        catch(SQLException ex) {
            System.out.println("exc: " + ex);

        }

        return resUser;
    }

    public User getProfile(String nickname) {
        Connection con = null;
        CallableStatement proc = null;
        User resUser = null;
        String sql = "get_user_by_nickname(?::citext, ?, ?, ?, ?, ?)";

        HashMap<Integer, Object> parameters = new HashMap<>();
        parameters.put(1, nickname);

        HashMap<Integer, Integer> outParameters = new HashMap<>();
        outParameters.put(2, Types.INTEGER);
        outParameters.put(3, Types.OTHER);
        outParameters.put(4, Types.OTHER);
        outParameters.put(5, Types.VARCHAR);
        outParameters.put(6, Types.VARCHAR);

        try {
            con = DbConnection.getConnection();

            proc = DbConnection.prepareCall(sql, con, parameters, outParameters);

            resUser = new User(proc.getInt(2), proc.getObject(3).toString(), proc.getObject(4).toString(),
                    proc.getString(5), proc.getString(6));

            DbConnection.closeConnection(con);
        }
        catch(SQLException ex) {
            System.out.println("exc: " + ex);
        }

        return resUser;
    }

    public User updateProfile(User user, String nickname) {
        Connection con = null;
        CallableStatement proc = null;
        User resUser = null;
        String sql = "update_user(?::citext, ?::citext, ?, ?, ?, ?, ?, ?, ?)";

        HashMap<Integer, Object> parameters = new HashMap<>();
        parameters.put(1, nickname);
        parameters.put(2, user.getEmail());
        parameters.put(3, user.getAbout());
        parameters.put(4, user.getFullname());

        HashMap<Integer, Integer> outParameters = new HashMap<>();
        outParameters.put(5, Types.INTEGER);
        outParameters.put(6, Types.OTHER);
        outParameters.put(7, Types.OTHER);
        outParameters.put(8, Types.VARCHAR);
        outParameters.put(9, Types.VARCHAR);

        try {
            con = DbConnection.getConnection();

            proc = DbConnection.prepareCall(sql, con, parameters, outParameters);

            resUser = new User(proc.getInt(5), proc.getObject(6).toString(), proc.getObject(7).toString(),
                    proc.getString(8), proc.getString(9));

            DbConnection.closeConnection(con);
        }
        catch(SQLException ex) {
            System.out.println("exc: " + ex);
        }

        return resUser;
    }
}
