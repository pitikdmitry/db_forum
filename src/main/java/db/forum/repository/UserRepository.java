package db.forum.repository;

import db.forum.model.Post;
import db.forum.model.User;
import db.forum.Mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Transactional
@Service
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addThreadToUser(User user, Integer forum_id) {
        String sql = "INSERT INTO posts_users_threads (user_id, forum_id) VALUES(?, ?);";
        Object[] args = new Object[]{user.getUser_id(), forum_id};

        jdbcTemplate.update(sql, args);
    }

    public void executeUsersWithForum(HashMap<Integer, Integer> usersWithForum) {
        Connection connection = null;
        String sql = "INSERT INTO posts_users_threads (user_id, forum_id) VALUES(?, ?);";

        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (Map.Entry<Integer, Integer> entry : usersWithForum.entrySet()) {

                Integer user_id = entry.getKey();
                Integer forum_id = entry.getValue();
                preparedStatement.setInt(1, user_id);
                preparedStatement.setInt(2, forum_id);
                try {
                    preparedStatement.execute();
                    System.out.println("done");
                } catch (DuplicateKeyException ex) {
                    //normal
                } catch(Exception ex) {
                    //normal
                    System.out.println(ex);
                }
//                preparedStatement.addBatch();
            }

//            preparedStatement.executeBatch();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
//    public List<User> getUsers(Integer forum_id, Integer limit, String since, Boolean desc) {
//        ArrayList<Object> args = new ArrayList<>();
//        String sql = "SELECT DISTINCT user_id, nickname, email, fullname, about from " +
//                "(SELECT DISTINCT u.user_id, u.nickname, u.email, u.fullname, u.about" +
//                " FROM users u JOIN posts p ON u.user_id=p.user_id WHERE p.forum_id = ? UNION" +
//                " SELECT DISTINCT u2.user_id, u2.nickname, u2.email, u2.fullname, u2.about" +
//                " FROM users u2 JOIN threads t ON u2.user_id=t.user_id WHERE t.forum_id = ?) as sub1";
//        args.add(forum_id);
//        args.add(forum_id);
//        if(since != null) {
//            if(desc != null && desc) {
//                sql += " WHERE sub1.nickname < ?::citext";
//            } else {
//                sql += " WHERE sub1.nickname > ?::citext";
//            }
//            args.add(since);
//        }
//        if(desc != null && desc) {
//            sql += " ORDER BY sub1.nickname DESC";
//        } else {
//            sql += " ORDER BY sub1.nickname ASC";
//        }
//        if(limit != null) {
//            sql += " LIMIT ?;";
//            args.add(limit);
//        } else{
//            sql += ";";
//        }
//        return jdbcTemplate.query(sql, args.toArray(), new UserMapper());
//    }


    public List<User> getUsers(Integer forum_id, Integer limit, String since, Boolean desc) {
        ArrayList<Object> args = new ArrayList<>();
        String sql = "SELECT u.user_id, u.nickname, u.email, u.fullname, u.about FROM posts_users_threads put " +
                "JOIN users u ON u.user_id=put.user_id " +
                "WHERE forum_id = ?";
        args.add(forum_id);
        if(since != null) {
            if(desc != null && desc) {
                sql += " AND nickname < ?::citext";
            } else {
                sql += " AND nickname > ?::citext";
            }
            args.add(since);
        }
        if(desc != null && desc) {
            sql += " ORDER BY nickname DESC";
        } else {
            sql += " ORDER BY nickname ASC";
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
        String sql = "INSERT INTO users (nickname, email, about, fullname) VALUES (?::citext, ?::citext, ?, ?) RETURNING *;";
        Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname()};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public List<User> getByNicknameAndEmail(String nickname, String email) {
        String sql = "SELECT * FROM users WHERE nickname = ?::citext or email = ?::citext;";
        Object[] args = new Object[]{nickname, email};
        return jdbcTemplate.query(sql, args, new UserMapper());
    }

    public User updateFullnameByNickname(String fullname, String nickname) {
        String sql = "UPDATE users SET fullname = ? WHERE nickname = ?::citext RETURNING *;";
        Object[] args = new Object[]{fullname, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public void updateFullnameByNicknamePUT(String fullname, String nickname) {
        Integer userId = null;
        try{
            userId = checkUserInPUT(nickname);
        } catch(Exception ex) {
            return;
        }
        if(userId == null) {
            return;
        }
        String sql = "UPDATE posts_users_threads SET fullname = ? WHERE nickname = ?::citext;";
        Object[] args = new Object[]{fullname, nickname};
        jdbcTemplate.update(sql, args);
    }

    public User updateEmailByNickname(String email, String nickname) {
        String sql = "UPDATE users SET email = ? WHERE nickname = ?::citext RETURNING *;";
        Object[] args = new Object[]{email, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public void updateEmailByNicknamePUT(String email, String nickname) {
        Integer userId = null;
        try{
            userId = checkUserInPUT(nickname);
        } catch(Exception ex) {
            return;
        }
        if(userId == null) {
            return;
        }
        String sql = "UPDATE posts_users_threads SET email = ? WHERE nickname = ?::citext;";
        Object[] args = new Object[]{email, nickname};
        jdbcTemplate.update(sql, args);
    }

    public User updateAboutByNickname(String about, String nickname) {
        String sql = "UPDATE users SET about = ? WHERE nickname = ?::citext RETURNING *;";
        Object[] args = new Object[]{about, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public void updateAboutByNicknamePUT(String about, String nickname) {
        Integer userId = null;
        try{
            userId = checkUserInPUT(nickname);
        } catch(Exception ex) {
            return;
        }
        if(userId == null) {
            return;
        }
        String sql = "UPDATE posts_users_threads SET about = ? WHERE nickname = ?::citext;";
        Object[] args = new Object[]{about, nickname};
        jdbcTemplate.update(sql, args);
    }

    public User updateAllByNickname(User user, String nickname) {
        String sql = "UPDATE users SET nickname = ?, email = ?, about = ?, fullname = ?" +
                " WHERE users.nickname = ?::citext RETURNING *;";

        Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname(), nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public void updateAllByNicknamePUT(User user, String nickname) {
        Integer userId = null;
        try{
            userId = checkUserInPUT(nickname);
        } catch(Exception ex) {
            return;
        }
        if(userId == null) {
            return;
        }
        String sql = "UPDATE posts_users_threads SET nickname = ?, email = ?, about = ?, fullname = ?" +
                " WHERE users.nickname = ?::citext;";

        Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname(), nickname};
        jdbcTemplate.update(sql, args);
    }

    public User updateEmailAndFullnameByNickname(String email, String fullname, String nickname) {
        String sql = "UPDATE users SET email = ?::citext, fullname = ? WHERE nickname = ?::citext RETURNING *;";
        Object[] args = new Object[]{email, fullname, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public void updateEmailAndFullnameByNicknamePUT(String email, String fullname, String nickname) {
        Integer userId = null;
        try{
            userId = checkUserInPUT(nickname);
        } catch(Exception ex) {
            return;
        }
        if(userId == null) {
            return;
        }
        String sql = "UPDATE posts_users_threads SET email = ?::citext, fullname = ? WHERE nickname = ?::citext;";
        Object[] args = new Object[]{email, fullname, nickname};
        jdbcTemplate.update(sql, args);
    }

    public User updateAboutAndFullnameByNickname(String about, String fullname, String nickname) {
        String sql = "UPDATE users SET about = ?, fullname = ? WHERE nickname = ?::citext RETURNING *;";
        Object[] args = new Object[]{about, fullname, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public void updateAboutAndFullnameByNicknamePUT(String about, String fullname, String nickname) {
        Integer userId = null;
        try{
            userId = checkUserInPUT(nickname);
        } catch(Exception ex) {
            return;
        }
        if(userId == null) {
            return;
        }
        String sql = "UPDATE posts_users_threads SET about = ?, fullname = ? WHERE nickname = ?::citext;";
        Object[] args = new Object[]{about, fullname, nickname};
        jdbcTemplate.update(sql, args);
    }

    public User updateEmailAndAboutByNickname(String email, String about, String nickname) {
        String sql = "UPDATE users SET email = ?::citext, about = ? WHERE nickname = ?::citext RETURNING *;";
        Object[] args = new Object[]{email, about, nickname};
        return jdbcTemplate.queryForObject(sql, args, new UserMapper());
    }

    public void updateEmailAndAboutByNicknamePUT(String email, String about, String nickname) {
        Integer userId = null;
        try{
            userId = checkUserInPUT(nickname);
        } catch(Exception ex) {
            return;
        }
        if(userId == null) {
            return;
        }
        String sql = "UPDATE posts_users_threads SET email = ?::citext, about = ? WHERE nickname = ?::citext;";
        Object[] args = new Object[]{email, about, nickname};
        jdbcTemplate.update(sql, args);
    }

    public Integer checkUserInPUT(String nickname) {
        String sql = "SELECT user_id FROM posts_users_threads where nickname=?::citext;";
        Object[] args = new Object[]{nickname};
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }
}
