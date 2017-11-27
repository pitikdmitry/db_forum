package db.forum.service;

import db.forum.model.*;
import db.forum.model.Thread;
import db.forum.repository.ForumRepository;
import db.forum.repository.PostRepository;
import db.forum.repository.ThreadRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;
    private final ForumRepository forumRepository;

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate) {
        this.postRepository = new PostRepository(jdbcTemplate);
        this.userRepository = new UserRepository(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
    }

    public ResponseEntity<?> details(Integer id, String related) {

        Post post = null;
        try {
            post = postRepository.getById(id);
        } catch (Exception ex) {
            Message message = new Message("Can't find post with id: " + id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        User user = null;
        Thread thread = null;
        Forum forum = null;//НАПИСАть через join
        if(related != null && post != null) {
            if (related.contains("user")) {
                if(post.getAuthor() != null) {
                    try {
                        user = userRepository.get_by_nickname(post.getAuthor());
                    } catch (Exception ex) {
                        Message message = new Message("Can't find user with nickname: " + post.getAuthor());
                        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
                    }
                }
            }
            if(related.contains("thread")) {
                if(post.getThreadId() != null) {
                    try {
                        thread = threadRepository.get_by_id(post.getThreadId());
                    } catch (Exception ex) {
                        Message message = new Message("Can't find thread with id: " + post.getThreadId());
                        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
                    }
                }
            }
            if(related.contains("forum")) {
                if(post.getForum() != null) {
                    try {
                        forum = forumRepository.get_by_slug(post.getForum());
                    } catch (Exception ex) {
                        Message message = new Message("Can't find forum with slug: " + post.getForum());
                        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
                    }
                }
            }
        }

        return new ResponseEntity<>(Post.getJsonObjects(user, forum, post, thread).toString(), HttpStatus.OK);
    }

    public ResponseEntity<?> update(Integer id, Post post) {
        try {
            Post responsePost = postRepository.update(id, post);
            return new ResponseEntity<>(responsePost.getJson().toString(), HttpStatus.OK);
        } catch(EmptyResultDataAccessException ex) {
            Message message = new Message("Can't find post with id: " + id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } catch(Exception ex) {
            Post responsePost = postRepository.getById(id);
            return new ResponseEntity<>(responsePost.getJson().toString(), HttpStatus.OK);
        }

    }
}
