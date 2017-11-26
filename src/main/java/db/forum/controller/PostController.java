package db.forum.controller;

import db.forum.model.Post;
import db.forum.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post/")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) { this.postService = postService; }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<?> details(@PathVariable(name = "id") Integer id,
                                          @RequestParam(value = "related", required = false) String related) {
        return postService.details(id, related);
    }

    @RequestMapping(value = "/{id}/details", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> update(@PathVariable(name = "id") Integer id,
                                          @RequestBody Post post) {

        return postService.update(id, post);
    }


}
