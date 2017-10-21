package db.forum.controller;

import db.forum.model.User;
import db.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user/")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping(value = "/{nickname}/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@PathVariable(name = "nickname") String nickname,
                                           @RequestBody User user) {

        return userService.create(user, nickname);
    }


    @RequestMapping(value = "/{nickname}/profile", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<?> getProfile(@PathVariable(name = "nickname") String nickname) {

        return userService.getProfile(nickname);
    }


    @RequestMapping(value = "/{nickname}/profile", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateProfile(@PathVariable(name = "nickname") String nickname,
                                                @RequestBody User user) {

        return userService.updateProfile(user, nickname);
    }
}































