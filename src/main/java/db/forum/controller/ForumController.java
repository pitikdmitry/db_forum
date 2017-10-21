package db.forum.controller;

import db.forum.model.Forum;
import db.forum.model.Thread;
import db.forum.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/forum/")
public class ForumController {

    private final ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody Forum forum) {

        return forumService.create(forum);
    }

    @RequestMapping(value = "/{slug}/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createThread(@PathVariable(name = "slug") String slug,
                                               @RequestBody Thread thread) {

        return forumService.createThread(thread, slug);
    }

    @RequestMapping(value = "/{slug}/details", method = RequestMethod.GET,
                    produces = "application/json")
    public ResponseEntity<?> getDetails(@PathVariable(name = "slug") String slug) {

        return forumService.getDetails(slug);
    }

    @RequestMapping(value = "/{slug}/threads", method = RequestMethod.GET,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> getThreads(@PathVariable(name = "slug") String slug,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "since", required = false) String since,
                                            @RequestParam(value = "desc", required = false) Boolean desc) {
        final String forumThreads = forumService.getThreads(slug, limit, since, desc);

        return new ResponseEntity<>(forumThreads, HttpStatus.OK);
    }

    @RequestMapping(value = "/{slug}/users", method = RequestMethod.GET,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> getUsers(@PathVariable(name = "slug") String slug,
                                             @RequestParam(value = "limit", required = false) Integer limit,
                                             @RequestParam(value = "since", required = false) String since,
                                             @RequestParam(value = "desc", required = false) Boolean desc) {
        final String forumUsers = forumService.getUsers(slug, limit, since, desc);

        return new ResponseEntity<>(forumUsers, HttpStatus.OK);
    }

}
