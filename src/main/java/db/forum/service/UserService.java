package db.forum.service;

import db.forum.model.Message;
import db.forum.model.User;
import db.forum.sqlQueries.UserQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.CallSite;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResponseEntity<?> create(User user, String nickname) {

        User resultUser = null;
        String sql = "INSERT INTO users (nickname, email, about, fullname) VALUES (?, ?, ?, ?) RETURNING *;";
        try {
            Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname()};
//            jdbcTemplate.update(sql, args);
            resultUser = jdbcTemplate.queryForObject(sql, args, new UserMapper());
//            resultUser = user;
//            resultUser.setNickname(nickname);
        }
        catch (Exception ex) {
            System.out.println("[Exception in create user]: " + ex);
            sql = "SELECT * FROM users WHERE nickname = ? or email = ?;";
            Object[] args = new Object[]{nickname, user.getEmail()};
//            resultUser = jdbcTemplate.queryForObject(sql, args, new UserMapper());
            List<User> users = jdbcTemplate.query(sql, args, new UserMapper());
            return new ResponseEntity<>(users, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resultUser, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getProfile(String nickname) {
        User resultUser = null;
        String sql = "SELECT * FROM users WHERE nickname = ?;";
        try {
            resultUser = jdbcTemplate.queryForObject(sql, new Object[]{nickname}, new UserMapper());

        }
        catch (Exception ex) {
            System.out.println("[Exception in getProfile user]: " + ex);
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(resultUser, HttpStatus.OK);
    }

    public ResponseEntity<?> updateProfile(User user, String nickname) {
        User resultUser = null;
        String sql = "UPDATE users SET nickname = ?, email = ?, about = ?, fullname = ?" +
                " WHERE users.nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname(), nickname};
            resultUser = jdbcTemplate.queryForObject(sql, args, new UserMapper());
        }
        catch (EmptyResultDataAccessException ex) {
            System.out.println("[Exception in updateProfile user]: " + ex);
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateProfile user]: " + ex);
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resultUser, HttpStatus.OK);
    }
}































