package db.forum.controller;

import db.forum.model.Forum;
import db.forum.model.Thread;
import db.forum.service.ForumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/forum/")
public class ForumController {

    private final ForumService forumService;
    private Logger logger;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
        this.logger = LoggerFactory.getLogger(ForumService.class);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<Forum> createForum(@RequestBody Forum forum, HttpSession session) {
        final Forum createdForum = forumService.create(forum);
        if (createdForum == null) {
            this.logger.error("[createForum] createdForum == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(createdForum, HttpStatus.OK);
    }

    @RequestMapping(value = "/{slug}/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<Thread> createThread(@PathVariable(name = "slug") String slug,
                                               @RequestBody Thread thread, HttpSession session) {
        final Thread resultThread = forumService.createThread(thread, slug);
        if (resultThread == null) {
            this.logger.error("[createThread] resultThread == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(resultThread, HttpStatus.OK);
    }

    @RequestMapping(value = "/{slug}/details", method = RequestMethod.GET,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<Forum> getDetails(@PathVariable(name = "slug") String slug) {
        final Forum resultForum = forumService.getDetails(slug);
        if (resultForum == null) {
            this.logger.error("[getDetails] resultForum == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(resultForum, HttpStatus.OK);
    }

    @RequestMapping(value = "/{slug}/threads", method = RequestMethod.GET,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> getThreads(@PathVariable(name = "slug") String slug,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "since", required = false) String since,
                                            @RequestParam(value = "desc", required = false) Boolean desc) {
        final String forumThreads = forumService.getThreads(slug, limit, since, desc);
        if (forumThreads == null) {
            this.logger.error("[getThreads] forumThreads == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(forumThreads, HttpStatus.OK);
    }

    @RequestMapping(value = "/{slug}/users", method = RequestMethod.GET,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> getUsers(@PathVariable(name = "slug") String slug,
                                             @RequestParam(value = "limit", required = false) Integer limit,
                                             @RequestParam(value = "since", required = false) String since,
                                             @RequestParam(value = "desc", required = false) Boolean desc) {
        final String forumUsers = forumService.getUsers(slug, limit, since, desc);
        if (forumUsers == null) {
            this.logger.error("[getUsers] forumUsers == null");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(forumUsers, HttpStatus.OK);
    }

}
