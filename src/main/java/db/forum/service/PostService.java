package db.forum.service;

import db.forum.model.*;
import db.forum.model.Thread;
import db.forum.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate) {
        this.postRepository = new PostRepository(jdbcTemplate);
    }

    public ResponseEntity<?> details(Integer id, String[] related) {
        User user = null;
        Forum forum = null;
        Post post = null;
        Thread thread = null;
        if(related == null) {
            post = postRepository.getById(id);
        }
        return new ResponseEntity<>(Post.getJsonObjects(user, forum, post, thread).toString(), HttpStatus.OK);
    }

    public ResponseEntity<?> update(Integer id, Post post) {
        try {
            Post responsePost = postRepository.update(id, post);
            return new ResponseEntity<>(responsePost.getJson().toString(), HttpStatus.OK);
        } catch(Exception ex) {
            System.out.println("[postService] update exc: " + ex);
            Message message = new Message("Can't find user with id #" + id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

    }
}
