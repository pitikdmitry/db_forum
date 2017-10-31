package db.forum.repository;


import db.forum.model.ServiceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServiceRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ServiceModel status() {
        String sqlForum = "SELECT COUNT(*) from forums;";
        String sqlPost = "SELECT COUNT(*) from posts;";
        String sqlThread = "SELECT COUNT(*) from threads;";
        String sqlUser = "SELECT COUNT(*) from users;";

        Integer forumCount = jdbcTemplate.queryForObject(sqlForum, Integer.class);
        Integer postCount = jdbcTemplate.queryForObject(sqlPost, Integer.class);
        Integer threadCount = jdbcTemplate.queryForObject(sqlThread, Integer.class);
        Integer userCount = jdbcTemplate.queryForObject(sqlUser, Integer.class);

        return new ServiceModel(forumCount, postCount, threadCount, userCount);
    }

    public void clear() {
        String sql = "TRUNCATE forums CASCADE; " +
                "TRUNCATE posts CASCADE; " +
                "TRUNCATE threads CASCADE; " +
                "TRUNCATE users CASCADE; " +
                "TRUNCATE vote CASCADE; ";
        jdbcTemplate.update(sql);
    }
}
