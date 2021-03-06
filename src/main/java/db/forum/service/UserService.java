package db.forum.service;

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

    private final UserRepository userRepository;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.userRepository = new UserRepository(jdbcTemplate);
    }

    public ResponseEntity<?> create(User user, String nickname) {
        try {
            User responseUser = userRepository.create(user, nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.CREATED);
        } catch (DuplicateKeyException ex) {
        }
        try{
            List<User> responseUsers = userRepository.getByNicknameAndEmail(nickname, user.getEmail());
            return new ResponseEntity<>(User.getJsonArray(responseUsers).toString(), HttpStatus.CONFLICT);
        } catch(Exception ex) {
            //ignored
        }
        return null;
    }

    public ResponseEntity<?> getProfile(String nickname) {
        try {
            User responseUser = userRepository.get_by_nickname(nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        }
        catch (Exception ex) {
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateProfile(User user, String nickname) {

        return checkUpdateParameters(user, nickname);
    }


    private ResponseEntity<?> checkUpdateParameters(User user, String nickname) {
        if((user.getEmail() == null) && (user.getFullname() == null) && (user.getAbout() == null)) {
            return updateByEmpty(nickname);

        } else if(user.getEmail() == null && user.getFullname() == null) {
            return updateAboutByNickname(user, nickname);

        } else if(user.getAbout() == null && user.getFullname() == null) {
            return updateEmailByNickname(user, nickname);

        } else if(user.getAbout() == null && user.getEmail() == null) {
            return updateFullnameByNickname(user, nickname);

        } else if(user.getAbout() == null) {
            return updateEmailAndFullnameByNickname(user, nickname);

        } else if(user.getEmail() == null) {
            return updateAboutAndFullnameByNickname(user, nickname);

        } else if(user.getFullname() == null) {
            return updateEmailAndAboutByNickname(user, nickname);

        } else {
            return updateAllByNickname(user, nickname);

        }
    }

    private ResponseEntity<?> updateEmailAndFullnameByNickname(User user, String nickname) {
        try {
            User responseUser = userRepository.updateEmailAndFullnameByNickname(user.getEmail(), user.getFullname(), nickname);
//            userRepository.updateEmailAndFullnameByNicknamePUT(user.getEmail(), user.getFullname(), nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Message message = new Message("Error update by email and fullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> updateAboutAndFullnameByNickname(User user, String nickname) {
        try {
            User responseUser = userRepository.updateAboutAndFullnameByNickname(user.getAbout(), user.getFullname(), nickname);
//            userRepository.updateAboutAndFullnameByNicknamePUT(user.getAbout(), user.getFullname(), nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Message message = new Message("Error updateByAboutAndFullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> updateEmailAndAboutByNickname(User user, String nickname) {
        try {
            User responseUser = userRepository.updateEmailAndAboutByNickname(user.getEmail(), user.getAbout(), nickname);
//            userRepository.updateEmailAndAboutByNicknamePUT(user.getEmail(), user.getAbout(), nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Message message = new Message("Error updateByAboutAndFullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> updateByEmpty(String nickname) {
        try {
            User responseUser = userRepository.get_by_nickname(nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Message message = new Message("Can't find user by nickname: " + nickname);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> updateFullnameByNickname(User user, String nickname) {
        try {
            User responseUser = userRepository.updateFullnameByNickname(user.getFullname(), nickname);
//            userRepository.updateFullnameByNicknamePUT(user.getFullname(), nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Message message = new Message("Error update by fullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> updateEmailByNickname(User user, String nickname) {
        try {
            User responseUser = userRepository.updateEmailByNickname(user.getEmail(), nickname);
//            userRepository.updateEmailByNicknamePUT(user.getEmail(), nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            User existsEmailUser = userRepository.get_by_email(user.getEmail());
            Message message = new Message("This email is already registered by user: " + existsEmailUser.getNickname());
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    private ResponseEntity<?> updateAboutByNickname(User user, String nickname) {
        try {
            User responseUser = userRepository.updateAboutByNickname(user.getAbout(), nickname);
//            userRepository.updateAboutByNicknamePUT(user.getAbout(), nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    private ResponseEntity<?> updateAllByNickname(User user, String nickname) {
        try {
            User responseUser = userRepository.updateAllByNickname(user, nickname);
//            userRepository.updateAllByNicknamePUT(user, nickname);
            return new ResponseEntity<>(responseUser.getJson().toString(), HttpStatus.OK);
        } catch (EmptyResultDataAccessException ex) {
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

}































