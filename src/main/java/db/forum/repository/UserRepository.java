package db.forum.repository;

import db.forum.Converter.UserConverter;
import db.forum.DTO.UserDTO;
import db.forum.model.User;
import db.forum.Mappers.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserConverter userConverter;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userConverter = new UserConverter(jdbcTemplate);
    }

    public User get_by_id(int user_id) {
        UserDTO resultUserDTO = null;
        User resultUser = null;
        String sql = "SELECT * FROM users WHERE user_id = ?;";
        try {
            Object[] args = new Object[]{user_id};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return resultUser;
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_id] exc: " + ex);
            return null;
        }
    }

    public User get_by_slug_or_id(String slug_or_id) {
        UserDTO resultUserDTO = null;
        User resultUser = null;
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
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return resultUser;
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_nickname] exc: " + ex);
            throw ex;
        }
    }

    public User get_by_nickname(String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;
        String sql = "SELECT * FROM users WHERE nickname = ?::citext;";
        try {
            Object[] args = new Object[]{nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return resultUser;
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_nickname] exc: " + ex);
            throw ex;
        }
    }

    public User get_by_email(String email) {
        UserDTO resultUserDTO = null;
        User resultUser = null;
        String sql = "SELECT * FROM users WHERE email = ?::citext;";
        try {
            Object[] args = new Object[]{email};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return resultUser;
        }
        catch (Exception ex) {
            System.out.println("[UserConverter.get_by_email] exc: " + ex);
            return null;
        }
    }
}
