package db.forum.repository;

import db.forum.model.User;
import db.forum.Mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User get_by_id(int user_id) {
        String sql = "SELECT * FROM users WHERE user_id = ?;";
        Object[] args = new Object[]{user_id};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public List<User> getUsers(Integer forum_id, Integer limit, String since, Boolean desc) {
        ArrayList<Object> args = new ArrayList<>();
        String sql = "SELECT user_id, nickname, email, fullname, about from " +
                    "(SELECT DISTINCT u.user_id, u.nickname, u.email, u.fullname, u.about" +
                    " FROM users u JOIN posts p ON u.user_id=p.user_id WHERE p.forum_id = ? UNION" +
                    " SELECT DISTINCT u2.user_id, u2.nickname, u2.email, u2.fullname, u2.about" +
                    " FROM users u2 JOIN threads t ON u2.user_id=t.user_id WHERE t.forum_id = ?) as sub1";
        args.add(forum_id);
        args.add(forum_id);
        if(since != null) {
            if(desc != null && desc) {
                sql += " WHERE sub1.nickname < ?::citext";
            } else {
                sql += " WHERE sub1.nickname > ?::citext";
            }
            args.add(since);
        }
        if(desc != null && desc) {
            sql += " ORDER BY sub1.nickname DESC";
        } else {
            sql += " ORDER BY sub1.nickname ASC";
        }
        if(limit != null) {
            sql += " LIMIT ?;";
            args.add(limit);
        } else{
            sql += ";";
        }
        return jdbcTemplate.query(sql, args.toArray(), new UserMapper());
    }

    public User get_by_slug_or_id(String slug_or_id) {
        String sql = null;
        Object[] args = null;
        try {
            Integer user_id = Integer.parseInt(slug_or_id);
            sql = "SELECT * FROM users WHERE user_id = ?;";
            args = new Object[]{user_id};
        }
        catch(Exception ex) {
            sql = "SELECT * FROM users WHERE nickname = ?::citext;";
            args = new Object[]{slug_or_id};
        }
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User get_by_nickname(String nickname) {
        String sql = "SELECT * FROM users WHERE nickname = ?::citext;";
        Object[] args = new Object[]{nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User get_by_email(String email) {
        String sql = "SELECT * FROM users WHERE email = ?::citext;";
        Object[] args = new Object[]{email};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User create(User user, String nickname) {
        String sql = "INSERT INTO users (nickname, email, about, fullname) VALUES (?, ?, ?, ?) RETURNING *;";
        Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname()};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public List<User> getByNicknameAndEmail(String nickname, String email) {
        String sql = "SELECT * FROM users WHERE nickname = ?::citext or email = ?::citext;";
        Object[] args = new Object[]{nickname, email};
        return jdbcTemplate.query(sql, args, new UserMapper());
    }

    public User updateFullnameByNickname(String fullname, String nickname) {
        String sql = "UPDATE users SET fullname = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{fullname, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User updateEmailByNickname(String email, String nickname) {
        String sql = "UPDATE users SET email = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{email, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User updateAboutByNickname(String about, String nickname) {
        String sql = "UPDATE users SET about = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{about, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User updateAllByNickname(User user, String nickname) {
        String sql = "UPDATE users SET nickname = ?, email = ?, about = ?, fullname = ?" +
                " WHERE users.nickname = ? RETURNING *;";

        Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname(), nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User updateEmailAndFullnameByNickname(String email, String fullname, String nickname) {
        String sql = "UPDATE users SET email = ?::citext, fullname = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{email, fullname, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User updateAboutAndFullnameByNickname(String about, String fullname, String nickname) {
        String sql = "UPDATE users SET about = ?, fullname = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{about, fullname, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public User updateEmailAndAboutByNickname(String email, String about, String nickname) {
        String sql = "UPDATE users SET email = ?::citext, about = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{email, about, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }
}
