package db.forum.service;

import db.forum.Converter.UserConverter;
import db.forum.DTO.UserDTO;
import db.forum.Mappers.UserDTOMapper;
import db.forum.model.Message;
import db.forum.model.User;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;
    private final UserConverter userConverter;
    private final UserRepository userRepository;


    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userConverter = new UserConverter(jdbcTemplate);
        this.userRepository = new UserRepository(jdbcTemplate);
    }

    public ResponseEntity<?> create(User user, String nickname) {

        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "INSERT INTO users (nickname, email, about, fullname) VALUES (?, ?, ?, ?) RETURNING *;";

        try {
            Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname()};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());

            resultUser = userConverter.getModel(resultUserDTO);

            return new ResponseEntity<>(resultUser, HttpStatus.CREATED);
        }
        catch (DuplicateKeyException ex) {
            System.out.println("[UserService.DuplicateKeyException] " + ex);

            sql = "SELECT * FROM users WHERE nickname = ?::citext or email = ?::citext;";
            Object[] args = new Object[]{nickname, user.getEmail()};
            List<UserDTO> existsUsersDTO = jdbcTemplate.query(sql, args, new UserDTOMapper());

            List<User> existsUsers = userConverter.getModelList(existsUsersDTO);

            return new ResponseEntity<>(existsUsers, HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<?> getProfile(String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;
        String sql = "SELECT * FROM users WHERE nickname = ?::citext;";
        try {
            resultUserDTO = jdbcTemplate.queryForObject(sql, new Object[]{nickname}, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in getProfile user]: " + ex);
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateProfile(User user, String nickname) {

        return checkUpdateParameters(user, nickname);
    }


    private ResponseEntity<?> checkUpdateParameters(User user, String nickname) {
        if((user.getEmail() == null) && (user.getFullname() == null) && (user.getAbout() == null)) {
            return updateByEmpty(user, nickname);
        }
        else if((user.getEmail() == null) && (user.getFullname() == null)) {
            return updateByAbout(user, nickname);
        }
        else if((user.getAbout() == null) && (user.getFullname() == null)) {
            return updateByEmail(user, nickname);
        }
        else if((user.getAbout() == null) && (user.getEmail() == null)) {
            return updateByFullname(user, nickname);
        }
        else if(user.getAbout() == null) {
            return updateByEmailAndFullname(user, nickname);
        }
        else if(user.getEmail() == null) {
            return updateByAboutAndFullname(user, nickname);

        }
        else if(user.getFullname() == null) {
            return updateByEmailAndAbout(user, nickname);
        }
        else {
            return updateByAll(user, nickname);
        }
    }

    public ResponseEntity<?> updateByEmailAndFullname(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "UPDATE users SET email = ?::citext, fullname = ? WHERE nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{user.getEmail(), user.getFullname(), nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateByEmail user]: " + ex);
            Message message = new Message("Error update by email and fullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateByAboutAndFullname(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "UPDATE users SET about = ?, fullname = ? WHERE nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{user.getAbout(), user.getFullname(), nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateByAboutAndFullname user]: " + ex);
            Message message = new Message("Error updateByAboutAndFullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateByEmailAndAbout(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "UPDATE users SET email = ?::citext, about = ? WHERE nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{user.getEmail(), user.getAbout(), nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateByEmailAndAbout user]: " + ex);
            Message message = new Message("Error updateByAboutAndFullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateByEmpty(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        try {
            resultUser = userRepository.get_by_nickname(nickname);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateEMPTY user]: " + ex);
            User existsEmailUser = userRepository.get_by_email(user.getEmail());
            Message message = new Message("Can't find user by nickname: " + nickname);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateByFullname(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "UPDATE users SET fullname = ? WHERE nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{user.getFullname(), nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateByFullname user]: " + ex);
            Message message = new Message("Error update by fullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateByEmail(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "UPDATE users SET email = ? WHERE nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{user.getEmail(), nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateByEmail user]: " + ex);
            User existsEmailUser = userRepository.get_by_email(user.getEmail());
            Message message = new Message("This email is already registered by user: " + existsEmailUser.getNickname());
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<?> updateByAbout(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "UPDATE users SET about = ? WHERE nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{user.getAbout(), nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateAbout user]: " + ex);
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<?> updateByAll(User user, String nickname) {
        UserDTO resultUserDTO = null;
        User resultUser = null;

        String sql = "UPDATE users SET nickname = ?, email = ?, about = ?, fullname = ?" +
                " WHERE users.nickname = ? RETURNING *;";
        try {
            Object[] args = new Object[]{nickname, user.getEmail(), user.getAbout(), user.getFullname(), nickname};
            resultUserDTO = jdbcTemplate.queryForObject(sql, args, new UserDTOMapper());
            resultUser = userConverter.getModel(resultUserDTO);
            return new ResponseEntity<>(resultUser, HttpStatus.OK);
        }
        catch (EmptyResultDataAccessException ex) {
            System.out.println("[Exception in updateByAll user]: " + ex);
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        catch (Exception ex) {
            System.out.println("[Exception in updateByAll user]: " + ex);
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

}































