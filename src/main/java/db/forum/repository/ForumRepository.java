package db.forum.repository;

import db.forum.Converter.ForumConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ForumRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ForumConverter forumConverter;

    @Autowired
    public ForumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.forumConverter = new ForumConverter(jdbcTemplate);
    }



}
