package db.forum.repository;

import db.forum.model.Forum;
import db.forum.model.Thread;
import db.forum.Mappers.ThreadMapper;
import db.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ThreadRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ThreadRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer countThreads(Integer forum_id) {
        String sql = "SELECT count(*) FROM threads WHERE forum_id = ?;";
        Object[] args = new Object[]{forum_id};
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }

    public Thread get_by_id(int thread_id) {
        String sql = "SELECT * FROM threads WHERE thread_id = ?;";
        try {
            Object[] args = new Object[]{thread_id};
            return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_by_id] exc: " + ex);
            return null;
        }
    }

    public Thread get_by_slug(String slug) {
        String sql = "SELECT * FROM threads WHERE slug = ?::citext;";
        Object[] args = new Object[]{slug};
        return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
    }

    public Thread get_by_slug_or_id(String slug_or_id) {
        String sql = null;
        Object[] args = null;
        try {
            Integer id = Integer.parseInt(slug_or_id);
            sql = "SELECT * FROM threads WHERE thread_id = ?;";
            args = new Object[]{id};
        }
        catch(Exception ex) {
            sql = "SELECT * FROM threads WHERE slug = ?::citext;";
            args = new Object[]{slug_or_id};
        }
        return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
    }

    public Thread increment_vote_rating(Thread old_thread, Integer vote_value, Boolean double_increment) {
        String sql = null;
        Object[] args = null;

        //////////апдейтим количество голосов у данной thread

        sql = "UPDATE threads SET votes = ? WHERE thread_id = ? RETURNING *;";
        try{
            if(double_increment) {
                args = new Object[]{(old_thread.getVotes() + 2 * vote_value), old_thread.getId()};
            }
            else {
                args = new Object[]{(old_thread.getVotes() + vote_value), old_thread.getId()};
            }
        }
        catch(Exception ex) {
            args = new Object[]{vote_value, old_thread.getId()};
        }
        return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
    }

    public Thread create(String slug, User user, Thread thread, Forum forum) {
        Object[] args = null;
        String sql = null;
        if(thread.getCreated() != null) {
            sql = "INSERT INTO threads (slug, forum, forum_id, author, user_id, created, message, title)" +
                    " VALUES (?::citext, ?::citext, ?, ?::citext, ?, ?::timestamptz, ?, ?) RETURNING *;";
            args = new Object[]{slug, forum.getSlug(), forum.getForum_id(), user.getNickname(), user.getUser_id(), thread.getCreated(),
                    thread.getMessage(), thread.getTitle()};
        }
        else {
            sql = "INSERT INTO threads (slug, forum, forum_id, author, user_id, message, title)" +
                    " VALUES (?::citext, ?::citext, ?, ?::citext, ?, ?, ?) RETURNING *;";
            args = new Object[]{slug, thread.getForum(), forum.getForum_id(), user.getNickname(), user.getUser_id(),
                    thread.getMessage(), thread.getTitle()};
        }
        return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
    }

    public Thread updateMessageTitle(Integer thread_id, String message, String title) {
        String sql = "UPDATE threads SET message = ?, title = ? WHERE thread_id = ? RETURNING *;";
        Object[] args = new Object[]{message, title, thread_id};
        return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
    }

    public Thread updateTitle(Integer thread_id, String title) {
        String sql = "UPDATE threads SET title = ? WHERE thread_id = ? RETURNING *;";
        Object[] args = new Object[]{title, thread_id};
        return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
    }

    public Thread updateMessage(Integer thread_id, String message) {
        String sql = "UPDATE threads SET message = ? WHERE thread_id = ? RETURNING *;";
        Object[] args = new Object[]{message, thread_id};
        return jdbcTemplate.queryForObject(sql, args, new ThreadMapper());
    }

    public Integer checkThread(String slug_or_id) {
        Integer id = null;
        String sql = null;
        Object[] args = null;
        try {
            id = Integer.parseInt(slug_or_id);
            sql = "SELECT thread_id FROM threads WHERE thread_id = ?;";
            args = new Object[]{id};
        } catch(Exception ex) {
            sql = "SELECT thread_id FROM threads WHERE slug = ?::citext;";
            args = new Object[]{slug_or_id};;
        }

        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }
}

