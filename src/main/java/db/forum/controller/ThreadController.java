package db.forum.controller;

import db.forum.model.Post;
import db.forum.model.Vote;
import db.forum.model.Thread;
import db.forum.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/thread/")
public class ThreadController {

    private final ThreadService threadService;

    @Autowired
    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @RequestMapping(value = "/{slug_or_id}/create", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createPost(@PathVariable(name = "slug_or_id") String slug_or_id,
                                            @RequestBody ArrayList<Post> posts) {
        System.out.println("create posts");
        return threadService.createPosts(slug_or_id, posts);
    }

    @RequestMapping(value = "/{slug_or_id}/vote", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> vote(@PathVariable(name = "slug_or_id") String slug_or_id,
                                        @RequestBody Vote vote) {
        return threadService.vote(slug_or_id, vote);
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<?> getDetails(@PathVariable(name = "slug_or_id") String slug_or_id) {
        return threadService.getDetails(slug_or_id);
    }

    @RequestMapping(value = "/{slug_or_id}/posts", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<?> getPosts(@PathVariable(name = "slug_or_id") String slug_or_id,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "since", required = false) Integer since,
                                      @RequestParam(value = "sort", required = false) String sort,
                                      @RequestParam(value = "desc", required = false) Boolean desc) {
        return threadService.getPosts(slug_or_id, limit, since, sort, desc);
    }

    @RequestMapping(value = "/{slug_or_id}/details", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> update(@PathVariable(name = "slug_or_id") String slug_or_id,
                                    @RequestBody Thread thread) {
        return threadService.update(slug_or_id, thread);
    }

}
