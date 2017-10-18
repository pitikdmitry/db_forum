package db.forum.service;

import db.forum.model.Forum;
import db.forum.model.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ForumService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Forum create(Forum forum) {
        return null;
    }

    public Thread createThread(Thread thread, String slug) {
        return null;
    }

    public Forum getDetails(String slug) {
        return null;
    }

    public String getThreads(String slug, Integer limit, String since, Boolean desc) {
        return null;
    }

    public String getUsers(String slug, Integer limit, String since, Boolean desc) {
        return null;
    }



}
