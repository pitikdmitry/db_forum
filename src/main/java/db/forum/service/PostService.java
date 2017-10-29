package db.forum.service;

import db.forum.model.Post;
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
        if(related == null) {
            Post responsePost = postRepository.getById(id);
            return new ResponseEntity<>(responsePost.getJson(), HttpStatus.OK);
        }
        return null;
    }
}
