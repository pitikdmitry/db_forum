package db.forum.service;

import db.forum.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ThreadService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String createPosts(String slug_or_id, ArrayList<Post> posts) {
        return null;
    }
}
