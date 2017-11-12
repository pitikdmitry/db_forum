package db.forum.repository;

import db.forum.Mappers.ForumMapper;
import db.forum.model.Forum;
import db.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class ForumRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ForumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Forum get_by_id(int forum_id) {
        String sql = "SELECT * FROM forums WHERE forum_id = ?;";
        Object[] args = new Object[]{forum_id};
        return jdbcTemplate.queryForObject(sql, args, new ForumMapper());
    }

    public Forum get_by_slug(String slug) {
        String sql = "SELECT * FROM forums WHERE slug = ?::citext;";
        Object[] args = new Object[]{slug};
        return jdbcTemplate.queryForObject(sql, args, new ForumMapper());
    }

    public Integer get_id_by_slug(String slug) {
        String sql = "SELECT forum_id FROM forums WHERE slug = ?::citext;";
        Object[] args = new Object[]{slug};
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }

    public Forum create(User user, Forum forum) {
        String sql = "INSERT INTO forums (slug, user_id, user_nickname, title) VALUES (?::citext, ?, ?::citext, ?)" +
                    " RETURNING *;";
        Object[] args = new Object[]{forum.getSlug(), user.getUser_id(), user.getNickname(), forum.getTitle()};
        return jdbcTemplate.queryForObject(sql, args, new ForumMapper());
    }

    public Forum getByUserId(Integer user_id) {
        String sql = "SELECT * FROM forums WHERE user_id = ?;";
        Object[] args = new Object[]{user_id};
        return jdbcTemplate.queryForObject(sql, args, new ForumMapper());
    }

    public void incrementThreadStat(Integer forum_id) {
        String sql = "UPDATE forums SET threads = threads + 1 WHERE forum_id = ?";
        Object[] args = new Object[]{forum_id};
        jdbcTemplate.update(sql, args);
    }

    public void incrementPostStat(Integer current_posts, Integer forum_id) {
        String sql = "UPDATE forums SET posts = ? WHERE forum_id = ?";
        Object[] args = new Object[]{current_posts + 1, forum_id};
        jdbcTemplate.update(sql, args);
    }
}
