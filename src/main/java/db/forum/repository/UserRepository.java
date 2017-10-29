package db.forum.repository;

import db.forum.Converter.UserConverter;
import db.forum.DTO.UserDTO;
import db.forum.model.User;
import db.forum.Mappers.UserDTOMapper;
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
    private final UserConverter userConverter;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userConverter = new UserConverter();
    }

    public User get_by_id(int user_id) {
        String sql = "SELECT * FROM users WHERE user_id = ?;";
        try {
            Object[] args = new Object[]{user_id};
            UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            return userConverter.getModel(resultUserDTO);
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_id] exc: " + ex);
            return null;
        }
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
                sql += " WHERE LOWER(sub1.nickname COLLATE \"ucs_basic\") < LOWER(?::citext)";
            }
            else {
                sql += " WHERE LOWER(sub1.nickname COLLATE \"ucs_basic\") > LOWER(?::citext)";
            }
            args.add(since);
        }
        if(desc != null && desc) {
            sql += " ORDER BY LOWER(sub1.nickname) COLLATE \"ucs_basic\" DESC";
        }
        else {
            sql += " ORDER BY LOWER(sub1.nickname) COLLATE \"ucs_basic\" ASC";
        }
        if(limit != null) {
            sql += " LIMIT ?;";
            args.add(limit);
        }
        else{
            sql += ";";
        }
        List<UserDTO> resultUserDTO = jdbcTemplate.query(sql, args.toArray(), new UserDTOMapper());
        return userConverter.getModelList(resultUserDTO);
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
        try {
            UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            return userConverter.getModel(resultUserDTO);
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_nickname] exc: " + ex);
            throw ex;
        }
    }

    public User get_by_nickname(String nickname) {
        String sql = "SELECT * FROM users WHERE nickname = ?::citext;";
        try {
            Object[] args = new Object[]{nickname};
            UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            return userConverter.getModel(resultUserDTO);
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_nickname] exc: " + ex);
            throw ex;
        }
    }

    public User get_by_email(String email) {
        String sql = "SELECT * FROM users WHERE email = ?::citext;";
        try {
            Object[] args = new Object[]{email};
            UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            return userConverter.getModel(resultUserDTO);
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_email] exc: " + ex);
            return null;
        }
    }

    public User create(User user, String nickname) {
        String sql = "INSERT INTO users (nickname, email, about, fullname) VALUES (?, ?, ?, ?) RETURNING *;";
        Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname()};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }

    public List<User> getByNicknameAndEmail(String nickname, String email) {
        String sql = "SELECT * FROM users WHERE nickname = ?::citext or email = ?::citext;";
        Object[] args = new Object[]{nickname, email};
        List<UserDTO> existsUsersDTO = jdbcTemplate.query(sql, args, new UserDTOMapper());
        return userConverter.getModelList(existsUsersDTO);
    }

    public User updateFullnameByNickname(String fullname, String nickname) {
        String sql = "UPDATE users SET fullname = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{fullname, nickname};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }

    public User updateEmailByNickname(String email, String nickname) {
        String sql = "UPDATE users SET email = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{email, nickname};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }

    public User updateAboutByNickname(String about, String nickname) {
        String sql = "UPDATE users SET about = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{about, nickname};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }

    public User updateAllByNickname(User user, String nickname) {
        String sql = "UPDATE users SET nickname = ?, email = ?, about = ?, fullname = ?" +
                " WHERE users.nickname = ? RETURNING *;";

        Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname(), nickname};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }

    public User updateEmailAndFullnameByNickname(String email, String fullname, String nickname) {
        String sql = "UPDATE users SET email = ?::citext, fullname = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{email, fullname, nickname};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }

    public User updateAboutAndFullnameByNickname(String about, String fullname, String nickname) {
        String sql = "UPDATE users SET about = ?, fullname = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{about, fullname, nickname};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }

    public User updateEmailAndAboutByNickname(String email, String about, String nickname) {
        String sql = "UPDATE users SET email = ?::citext, about = ? WHERE nickname = ? RETURNING *;";
        Object[] args = new Object[]{email, about, nickname};
        UserDTO resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
        return userConverter.getModel(resultUserDTO);
    }
}
