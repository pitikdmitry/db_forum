package db.forum.repository;

import db.forum.Converter.ForumConverter;
import db.forum.Converter.ThreadConverter;
import db.forum.DTO.ForumDTO;
import db.forum.DTO.ThreadDTO;
import db.forum.Mappers.ForumDTOMapper;
import db.forum.model.Forum;
import db.forum.model.Thread;
import db.forum.Mappers.ThreadDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;

public class ThreadRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ThreadConverter threadConverter;

    @Autowired
    public ThreadRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.threadConverter = new ThreadConverter(jdbcTemplate);
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
            ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            return threadConverter.getModel(resultThreadDTO);
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_by_id] exc: " + ex);
            return null;
        }
    }

    public Thread get_by_slug(String slug) {
        String sql = "SELECT * FROM threads WHERE slug = ?::citext;";
        try {
            Object[] args = new Object[]{slug};
            ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            return threadConverter.getModel(resultThreadDTO);
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_by_islug] exc: " + ex);
            return null;
        }
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

        ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
        return threadConverter.getModel(resultThreadDTO);
    }

    public Integer get_forum_id_by_thread_id(int thread_id) {
        Integer forum_id = null;
        String sql = "SELECT forum_id FROM threads WHERE thread_id = ?;";
        try {
            Object[] args = new Object[]{thread_id};
            forum_id = jdbcTemplate.queryForObject(sql, args, Integer.class);
            return forum_id;
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_forum_id_by_thread_id] exc: " + ex);
            return null;
        }
    }

    public Thread increment_vote_rating(Thread old_thread, Integer vote_value, Boolean double_increment) {
        String sql = null;
        Object[] args = null;

        //////////апдейтим количество голосов у данной thread
        try{
            sql = "UPDATE threads SET votes = ? WHERE thread_id = ? RETURNING *;";
            try{
                if(double_increment) {
                    args = new Object[]{(old_thread.getVotes() + 2* vote_value), old_thread.getId()};
                }
                else {
                    args = new Object[]{(old_thread.getVotes() + vote_value), old_thread.getId()};
                }
            }
            catch(Exception ex) {
                args = new Object[]{vote_value, old_thread.getId()};
            }
            ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            return threadConverter.getModel(resultThreadDTO);
        }
        catch(Exception ex) {
            System.out.println(("BAF IF HEREE"));
            return null;
        }
    }

    public Integer get_id_from_slug_or_id(String slug_or_id) {
        Integer id = null;
        try {
            id = Integer.parseInt(slug_or_id);
        }
        catch(Exception ex) {
            String sql = "SELECT thread_id FROM threads WHERE slug = ?::citext;";
            Object[] args = new Object[]{slug_or_id};
            id = jdbcTemplate.queryForObject(sql, args, Integer.class);
        }
        return id;
    }

    public Thread create(String slug, Integer forum_id, Integer user_id, Timestamp created, String message, String title) {
        Object[] args = null;
        String sql = null;
        if(created != null) {
            sql = "INSERT INTO threads (slug, forum_id, user_id, created, message, title)" +
                    " VALUES (?::citext, ?, ?, ?::timestamptz, ?, ?) RETURNING *;";
            args = new Object[]{slug, forum_id, user_id, created,
                    message, title};
        }
        else {
            sql = "INSERT INTO threads (slug, forum_id, user_id, message, title)" +
                    " VALUES (?::citext, ?, ?, ?, ?) RETURNING *;";
            args = new Object[]{slug, forum_id, user_id,
                    message, title};
        }
        ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
        return threadConverter.getModel(resultThreadDTO);
    }

    public Thread updateMessageTitle(Integer thread_id, String message, String title) {
        String sql = "UPDATE threads SET message = ?, title = ? WHERE thread_id = ? RETURNING *;";
        Object[] args = new Object[]{message, title, thread_id};
        ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
        return threadConverter.getModel(resultThreadDTO);
    }

    public Thread updateTitle(Integer thread_id, String title) {
        String sql = "UPDATE threads SET title = ? WHERE thread_id = ? RETURNING *;";
        Object[] args = new Object[]{title, thread_id};
        ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
        return threadConverter.getModel(resultThreadDTO);
    }

    public Thread updateMessage(Integer thread_id, String message) {
        String sql = "UPDATE threads SET message = ? WHERE thread_id = ? RETURNING *;";
        Object[] args = new Object[]{message, thread_id};
        ThreadDTO resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
        return threadConverter.getModel(resultThreadDTO);
    }

    public Thread checkThread(String slug_or_id) {
        String sql = "SELECT * FROM threads WHERE thread_id = ?;";
        Integer thread_id = get_id_from_slug_or_id(slug_or_id);
        if(thread_id == null) {
            return null;
            //nno thread
        }
        Object[] args = new Object[]{thread_id};
        ThreadDTO threadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
        return threadConverter.getModel(threadDTO);
    }

}

