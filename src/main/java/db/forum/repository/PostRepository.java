package db.forum.repository;

import db.forum.Mappers.PostMapper;
import db.forum.My_Exceptions.NoThreadException;
import db.forum.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Transactional
@Service
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Post getById(Integer post_id) {
        String sql = "SELECT post_id, author, created, forum, is_edited, message, parent_id, thread_id FROM posts WHERE post_id = ?;";
        Object[] args = new Object[]{post_id};
        return jdbcTemplate.queryForObject(sql, args, new PostMapper());
    }

    private java.sql.Array createSqlArray(List<Integer> list){
        java.sql.Array intArray = null;
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            intArray = connection.createArrayOf("INT", list.toArray());
        } catch (SQLException ignore) {
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return intArray;
    }

    public List<Integer> get_m_path(Integer parent_id, Integer post_id) {
        if (parent_id == null) {
            return null;
        }
        String sql = "SELECT m_path FROM posts WHERE post_id = ?";
        Object[] args = new Object[]{(int)parent_id};

        try {
            Array arr = jdbcTemplate.queryForObject(sql, args, Array.class);
            List<Integer> list = new ArrayList<>(Arrays.asList((Integer[]) arr.getArray()));

            if (list.size() == 1) {
                if (list.get(0) == null) {
                    return null;
                }
            }

            list.add(post_id);
            return list;
        } catch (Exception ex) {
            System.out.println("CANT GET MASS MAPTH: " + ex);
            return null;
        }
    }

    public List<Integer> get_new_m_path(Integer post_id) {
       List<Integer> m_path = new ArrayList<>();
       m_path.add(post_id);
       return m_path;
    }

    public List<Post> getPostFlat(Integer threadId, Integer limit, Integer since, Boolean desc) throws NoThreadException {
        List<Object> arguments = new ArrayList<Object>();
        String sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ?";
        arguments.add(threadId);

        if (since != null) {
            if(desc != null && desc) {
                sql += " AND post_id < ?";
            } else {
                sql += " AND post_id > ?";
            }
            arguments.add(since);
        }
        if (desc != null && desc) {
            sql += " ORDER BY post_id DESC";
        } else {
            sql += " ORDER BY post_id";
        }
        if (limit != null) {
            sql += " LIMIT ?;";
            arguments.add(limit);
        } else {
            sql += ";";
        }
        return jdbcTemplate.query(sql, arguments.toArray(), new PostMapper());
    }

    public List<Post> getPostTree(Integer threadId, Integer limit, Integer since, Boolean desc) {
        List<Object> arguments = new ArrayList<Object>();
        String sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ?";
        arguments.add(threadId);

        if (since != null) {
            if(desc != null && desc) {
                sql += " AND m_path < (SELECT m_path FROM posts WHERE post_id = ?)";
            }
            else {
                sql += " AND m_path > (SELECT m_path FROM posts WHERE post_id = ?)";
            }
            arguments.add(since);
        }
        if (desc != null && desc) {
            sql += " ORDER BY m_path DESC";
        } else {
            sql += " ORDER BY m_path";
        }
        if (limit != null) {
            sql += " LIMIT ?;";
            arguments.add(limit);
        } else {
            sql += ";";
        }
        return jdbcTemplate.query(sql, arguments.toArray(), new PostMapper());
    }

    public List<Post> getPostsParentTree(Integer threadId, Integer limit, Integer since, Boolean desc) {
        List<Object> arguments = new ArrayList<>();
        String sql = null;
        Integer id = threadId;

        if(since == null && limit == null && desc == null) {
            sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? ORDER BY m_path;";
            arguments.add(id);
        }
        else if(since != null && limit == null && desc == null) {
            sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                    " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                    " m_path > (SELECT m_path FROM posts WHERE post_id = ? AND parent_id = 0)" +
                    " ORDER BY post_id) ORDER BY m_path;";
            arguments.add(id);
            arguments.add(id);
            arguments.add(since);
        }
        else if(since == null && limit != null && desc == null) {
            sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                    " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0" +
                    " ORDER BY post_id LIMIT ?) ORDER BY m_path;";
            arguments.add(id);
            arguments.add(id);
            arguments.add(limit);
        }
        else if(since == null && limit == null && desc != null) {
            if(desc) {
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? " +
                        "ORDER BY m_path DESC;";
            }
            else{
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? " +
                        "ORDER BY m_path ASC;";
            }
            arguments.add(id);
        }
        else if(since != null && limit != null && desc == null) {
            sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                    " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                    " m_path > (SELECT m_path FROM posts WHERE post_id = ?)" +
                    " ORDER BY post_id LIMIT ?) ORDER BY m_path;";
            arguments.add(id);
            arguments.add(id);
            arguments.add(since);
            arguments.add(limit);
        }
        else if(since == null && limit != null && desc != null) {
            if(desc) {
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0" +
                        " ORDER BY post_id DESC LIMIT ?) ORDER BY m_path DESC;";
            }
            else{
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0" +
                        " ORDER BY post_id ASC LIMIT ?) ORDER BY m_path ASC;";
            }

            arguments.add(id);
            arguments.add(id);
            arguments.add(limit);
        }
        else if( since != null && limit == null && desc != null) {
            if(desc) {
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path < (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id DESC) ORDER BY m_path DESC;";
            }
            else {
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path > (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id ASC) ORDER BY m_path ASC;";
            }

            arguments.add(id);
            arguments.add(id);
            arguments.add(since);
        }
        else if( since != null && limit != null && desc != null){
            if(desc) {
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path < (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id DESC LIMIT ?) ORDER BY m_path DESC;";
            }
            else{
                sql = "SELECT author, created, forum, post_id, is_edited, message, parent_id, thread_id FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path > (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id ASC LIMIT ?) ORDER BY m_path ASC;";
            }
            arguments.add(id);
            arguments.add(id);
            arguments.add(since);
            arguments.add(limit);
        }

        return jdbcTemplate.query(sql, arguments.toArray(), new PostMapper());
    }

    public Post update(Integer id, Post post) {
        Post responsePost = checkPostUpdate(id);
        if(responsePost.getMessage().equals(post.getMessage())) {
            return responsePost;
        }
        ArrayList<Object> args = new ArrayList<>();
        String sql = "UPDATE posts SET message = ?, is_edited = ? WHERE post_id = ? RETURNING *";
        args.add(post.getMessage());
        args.add(true);
        args.add(id);
        return jdbcTemplate.queryForObject(sql, args.toArray(), new PostMapper());
    }

    private Post checkPostUpdate(Integer id) {
        String sql = "SELECT * FROM posts WHERE post_id = ?";
        Object[] args = new Object[]{id};
        return jdbcTemplate.queryForObject(sql, args, new PostMapper());
    }

    public void executePosts(List<Post> posts) {
        Connection connection = null;
        String sql = "INSERT INTO posts (post_id, thread_id, forum, author, parent_id, " +
                "message, created, is_edited, m_path) VALUES (?, ?, ?::citext, ?::citext, ?, ?, ?::timestamptz, ?, ?) RETURNING *;";

        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for(Post p : posts) {
                preparedStatement.setInt(1, (int)p.getId());
                preparedStatement.setInt(2, p.getThreadId());
                preparedStatement.setString(3, p.getForum());
                preparedStatement.setString(4, p.getAuthor());
                preparedStatement.setInt(5, p.getParent());
                preparedStatement.setString(6, p.getMessage());
                preparedStatement.setTimestamp(7, p.getCreated());
                preparedStatement.setBoolean(8, p.getEdited());
                preparedStatement.setArray(9, createSqlArray(p.getM_path()));
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getNext() {
        return jdbcTemplate.queryForObject("SELECT nextval('posts_post_id_seq')", Integer.class);
    }
}