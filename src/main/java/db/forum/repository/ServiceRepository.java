package db.forum.repository;


import db.forum.Mappers.ServiceModelMapper;
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
        String sqlForum = "SELECT COUNT(*) as forums_count, " +
                "(SELECT COUNT(*) FROM posts) as posts_count, " +
                "(SELECT COUNT(*) FROM threads) as threads_count, " +
                "(SELECT COUNT(*) FROM users) as users_count " +
                "FROM forums;";

        return jdbcTemplate.queryForObject(sqlForum, new ServiceModelMapper());
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
