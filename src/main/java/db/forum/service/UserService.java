package db.forum.service;

import db.forum.model.User;
import db.forum.sqlQueries.UserQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User create(User user, String nickname) {
        final String  createUserFunction = UserQueries.getCreateUser();
        DbConnection.executeSqlFunction(createUserFunction, user.getAbout(), user.getEmail(), user.getFullname(), nickname);
//
        return null;
    }

    public User getProfile(User user) {
        return null;
    }

    public User updateProfile(User user) {
        return null;
    }
}
