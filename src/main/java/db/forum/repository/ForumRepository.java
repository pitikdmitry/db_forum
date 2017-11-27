package db.forum.repository;

import db.forum.Mappers.ForumMapper;
import db.forum.Mappers.ThreadMapper;
import db.forum.model.Forum;
import db.forum.model.Thread;
import db.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
        String sql = "INSERT INTO forums (slug, user_id, user_nickname, title) VALUES (?::citext, ?, ?::citext, ?) RETURNING *;";
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

    public void incrementPostStat(Integer forum_id) {
        String sql = "UPDATE forums SET posts = posts + 1 WHERE forum_id = ?";
        Object[] args = new Object[]{forum_id};
        jdbcTemplate.update(sql, args);
    }

    public void addPostStat(Integer add, Integer forum_id) {
        String sql = "UPDATE forums SET posts = posts + ? WHERE forum_id = ?";
        Object[] args = new Object[]{add, forum_id};
        jdbcTemplate.update(sql, args);
    }

    public List<Thread> getThreads(String forum_slug, Integer limit, String since, Boolean desc) {
        String sql = null;
        Object[] args = null;

        if((limit == null) && (since == null) && (desc == null)) {
            sql = "SELECT * FROM threads WHERE forum = ?::citext ORDER BY created ASC;";
            args = new Object[]{forum_slug};

        } else if((since == null) && (desc == null) && (limit != null)) {
            sql = "SELECT * FROM threads WHERE forum = ?::citext ORDER BY created ASC LIMIT ?;";
            args = new Object[]{forum_slug, limit};

        } else if((limit == null) && (desc == null) && (since != null)) {
            sql = "SELECT * FROM threads WHERE forum = ?::citext and created >= ?::timestamptz ORDER BY created ASC;";
            args = new Object[]{forum_slug, since};

        } else if((limit == null) && (since == null) && (desc != null)) {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum = ?::citext ORDER BY created DESC;";
            } else {
                sql = "SELECT * FROM threads WHERE forum = ?::citext ORDER BY created ASC;";
            }
            args = new Object[]{forum_slug};

        } else if((since == null) && (desc != null) && (limit != null)) {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum = ?::citext ORDER BY created DESC LIMIT ?;";
            } else {
                sql = "SELECT * FROM threads WHERE forum = ?::citext ORDER BY created ASC LIMIT ?;";
            }
            args = new Object[]{forum_slug, limit};

        } else if((limit == null) && (since != null) && (desc != null)) {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum = ?::citext and created <= ?::timestamptz ORDER BY created DESC;";
            } else {
                sql = "SELECT * FROM threads WHERE forum = ?::citext and created >= ?::timestamptz ORDER BY created ASC;";
            }
            args = new Object[]{forum_slug, since};

        } else if((desc == null) && (since != null) && (limit != null)) {
            sql = "SELECT * FROM threads WHERE forum = ?::citext and created >= ?::timestamptz ORDER BY created ASC LIMIT ?;";
            args = new Object[]{forum_slug, since, limit};

        } else if((desc != null) && (since != null) && (limit != null)) {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum = ?::citext and created <= ?::timestamptz ORDER BY created DESC LIMIT ?;";
            } else {
                sql = "SELECT * FROM threads WHERE forum = ?::citext and created >= ?::timestamptz ORDER BY created ASC LIMIT ?;";
            }
            args = new Object[]{forum_slug, since, limit};

        } else {
            return null;
        }

        return jdbcTemplate.query(sql, args, new ThreadMapper());
    }
}
