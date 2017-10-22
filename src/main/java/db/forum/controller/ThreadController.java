package db.forum.controller;

import db.forum.model.Forum;
import db.forum.model.Post;
import db.forum.service.ForumService;
import db.forum.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/thread/")
public class ThreadController {

    private final ThreadService threadService;
    private Logger logger;

    @Autowired
    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
        this.logger = LoggerFactory.getLogger(ThreadService.class);
    }

    @RequestMapping(value = "/{slug_or_id}/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createPost(@PathVariable(name = "slug_or_id") String slug_or_id,
                                            @RequestBody ArrayList<Post> posts) {
        return threadService.createPosts(slug_or_id, posts);
    }

}
