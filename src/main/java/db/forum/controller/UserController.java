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
    public ResponseEntity<?> createUser(@PathVariable(name = "nickname") String nickname,
                                           @RequestBody User user) {

        return userService.create(user, nickname);
    }


    @RequestMapping(value = "/{nickname}/profile", method = RequestMethod.GET,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> getUserProfile(@PathVariable(name = "nickname") String nickname) {

        return userService.getProfile(nickname);
    }


    @RequestMapping(value = "/{nickname}/profile", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUserProfile(@PathVariable(name = "nickname") String nickname,
                                                @RequestBody User user) {

        return userService.updateProfile(user, nickname);
    }
}































