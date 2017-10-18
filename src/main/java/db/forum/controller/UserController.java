package db.forum.controller;

import db.forum.model.User;
import db.forum.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user/")
public class UserController {

    private final UserService userService;
    private Logger logger;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.logger = LoggerFactory.getLogger(UserController.class);
    }


    @RequestMapping(value = "/{nickname}/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> createUser(@PathVariable(name = "nickname") String nickname,
                                           @RequestBody User user) {

        final User createdUser = userService.create(user, nickname);
        if (createdUser == null) {
            this.logger.error("[createUser] createdUser == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }


    @RequestMapping(value = "/{nickname}/profile", method = RequestMethod.GET,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> getUserProfile(@PathVariable(name = "nickname") String nickname) {

        final User resultUser = userService.getProfile(nickname);
        if (resultUser == null) {
            this.logger.error("[getUserProfile] resultUser == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(resultUser, HttpStatus.OK);
    }


    @RequestMapping(value = "/{nickname}/profile", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> updateUserProfile(@PathVariable(name = "nickname") String nickname,
                                                @RequestBody User user) {

        final User resultUser = userService.updateProfile(user, nickname);
        if (resultUser == null) {
            this.logger.error("[updateUserProfile] resultUser == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(resultUser, HttpStatus.OK);
    }
}































