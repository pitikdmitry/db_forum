package db.forum.repository;

import db.forum.Mappers.PostMapper;
import db.forum.My_Exceptions.NoThreadException;
import db.forum.model.Forum;
import db.forum.model.Post;
import db.forum.model.User;
import db.forum.model.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ThreadRepository threadRepository;

    @Autowired
    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.threadRepository = new ThreadRepository(jdbcTemplate);
    }

    public Post getById(Integer post_id) {
        String sql = "SELECT * FROM posts WHERE post_id = ?;";
        Object[] args = new Object[]{post_id};
        return jdbcTemplate.queryForObject(sql, args, new PostMapper());
    }

    public Integer countPostsByForumId(Integer forum_id) {
        String sql = "SELECT count(*) FROM posts WHERE forum_id = ?;";
        Object[] args = new Object[]{forum_id};
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }

//    public Post createPost(Thread thread, Forum forum, User user,
//                           Integer parent_id, String message, Timestamp created, Boolean is_edited) {
//        String sql = "INSERT INTO posts (thread_id, thread, forum_id, forum, user_id, author, parent_id, " +
//                "message, created, is_edited) VALUES (?, ?::citext, ?, ?::citext, ?, ?::citext, ?, ?, ?::timestamptz, ?) RETURNING *;";
//        Object[] args = new Object[]{thread.getId(), thread.getSlug(), forum.getForum_id(), forum.getSlug(),
//                                    user.getUser_id(), user.getNickname(), parent_id, message, created, false};
//
//        Post resultPost = jdbcTemplate.queryForObject(sql, args, new PostMapper());
//        return updateMpath(parent_id, resultPost.getId());
//    }

    public List<Post> getAnotherPostWithSameParent(Integer parent_id) {
        String sql = "SELECT * FROM posts WHERE parent_id = ?;";
        Object[] args = new Object[]{parent_id};
        return jdbcTemplate.query(sql, args, new PostMapper());
    }

    public void updateMpath(Integer parent_id, Integer post_id) {
        java.sql.Array arr = null;
        List<Integer> m_path = get_m_path(parent_id);
        if(m_path == null) {
            m_path = new ArrayList<>();
        }

        m_path.add(post_id);

        String sql = "UPDATE posts SET m_path = ? WHERE post_id = ?;";
        if(m_path != null) {
            arr = createSqlArray(m_path);
        }
        Object[] args = new Object[]{arr, post_id};

        jdbcTemplate.update(sql, args);
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

    public List<Integer> get_m_path(Integer parent_id) {
        if (parent_id == null) {
            return null;
        }
        int par_id = parent_id;
        String sql = "SELECT m_path FROM posts WHERE post_id = ?";
        Object[] args = new Object[]{par_id};

        try {
            Array arr = jdbcTemplate.queryForObject(sql, args, Array.class);
            List<Integer> list = new ArrayList<>(Arrays.asList((Integer[]) arr.getArray()));

            if (list.size() == 1) {
                if (list.get(0) == null) {
                    return null;
                }
            }
            return list;
        } catch (Exception ex) {
            System.out.println("CANT GET MASS MAPTH: " + ex);
            return null;
        }
    }

    public List<Post> getPosts(String slug_or_id, Integer limit, Integer since, Boolean desc) {
        List<Object> arguments = new ArrayList<Object>();
        String sql = "SELECT * FROM posts WHERE thread_id = ?";

        Integer id = threadRepository.get_id_from_slug_or_id(slug_or_id);
        arguments.add(id);

        if(since != null) {
            if(desc != null && desc) {
                sql += " AND post_id < ?";
            }
            else {
                sql += " AND post_id > ?";
            }
            arguments.add(since);
        }
        if (desc != null && desc) {
            sql += "ORDER BY created DESC, post_id DESC";
        } else {
            sql += "ORDER BY created, post_id";
        }
        if (limit != null) {
            sql += " LIMIT ?;";
            arguments.add(limit);
        } else {
            sql += ";";
        }
        return jdbcTemplate.query(sql, arguments.toArray(), new PostMapper());
    }

    public List<Post> getPostFlat(String slug_or_id, Integer limit, Integer since, Boolean desc) throws NoThreadException {
        List<Object> arguments = new ArrayList<Object>();
        Integer id = null;
        String sql = "SELECT * FROM posts WHERE thread_id = ?";
        try {
            id = threadRepository.get_id_from_slug_or_id(slug_or_id);
        } catch(Exception ex) {
            throw new NoThreadException(slug_or_id);
        }
        arguments.add(id);
        if (since != null) {
            if(desc != null && desc) {
                sql += " AND post_id < ?";
            }
            else {
                sql += " AND post_id > ?";
            }
            arguments.add(since);
        }
        if (desc != null && desc) {
            sql += " ORDER BY created DESC, post_id DESC";
        } else {
            sql += " ORDER BY created, post_id";
        }
        if (limit != null) {
            sql += " LIMIT ?;";
            arguments.add(limit);
        } else {
            sql += ";";
        }
        return jdbcTemplate.query(sql, arguments.toArray(), new PostMapper());
    }

    public List<Post> getPostTree(String slug_or_id, Integer limit, Integer since, Boolean desc) {
        List<Object> arguments = new ArrayList<Object>();
        String sql = "SELECT * FROM posts WHERE thread_id = ?";
        Integer id = threadRepository.get_id_from_slug_or_id(slug_or_id);
        arguments.add(id);

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
            sql += " ORDER BY m_path DESC, post_id DESC";
        } else {
            sql += " ORDER BY m_path, post_id";
        }
        if (limit != null) {
            sql += " LIMIT ?;";
            arguments.add(limit);
        } else {
            sql += ";";
        }
        return jdbcTemplate.query(sql, arguments.toArray(), new PostMapper());
    }

    public List<Post> getPostsParentTree(String slug_or_id, Integer limit, Integer since, Boolean desc) {
        List<Object> arguments = new ArrayList<>();
        String sql = null;
        Integer id = threadRepository.get_id_from_slug_or_id(slug_or_id);

        if(since == null && limit == null && desc == null) {
            sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                    " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 ORDER BY post_id)" +
                    " ORDER BY m_path, post_id;";
            arguments.add(id);
            arguments.add(id);
        }
        else if(since != null && limit == null && desc == null) {
            sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                    " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                    " m_path > (SELECT m_path FROM posts WHERE post_id = ? AND parent_id = 0)" +
                    " ORDER BY post_id) ORDER BY m_path, post_id;";
            arguments.add(id);
            arguments.add(id);
            arguments.add(since);
        }
        else if(since == null && limit != null && desc == null) {
            sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                    " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0" +
                    " ORDER BY post_id LIMIT ?) ORDER BY m_path, post_id;";
            arguments.add(id);
            arguments.add(id);
            arguments.add(limit);
        }
        else if(since == null && limit == null && desc != null) {
            if(desc) {
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0" +
                        " ORDER BY post_id DESC) ORDER BY m_path DESC, post_id DESC;";
            }
            else{
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? ORDER BY post_id ASC)" +
                        " ORDER BY m_path ASC, post_id ASC;";
            }
            arguments.add(id);
            arguments.add(id);
        }
        else if(since != null && limit != null && desc == null) {
            sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                    " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                    " m_path > (SELECT m_path FROM posts WHERE post_id = ?)" +
                    " ORDER BY post_id LIMIT ?) ORDER BY m_path, post_id;";
            arguments.add(id);
            arguments.add(id);
            arguments.add(since);
            arguments.add(limit);
        }
        else if(since == null && limit != null && desc != null) {
            if(desc) {
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0" +
                        " ORDER BY post_id DESC LIMIT ?) ORDER BY m_path DESC, post_id DESC;";
            }
            else{
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0" +
                        " ORDER BY post_id ASC LIMIT ?) ORDER BY m_path ASC, post_id ASC;";
            }

            arguments.add(id);
            arguments.add(id);
            arguments.add(limit);
        }
        else if( since != null && limit == null && desc != null) {
            if(desc) {
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path < (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id DESC) ORDER BY m_path DESC, post_id DESC;";
            }
            else {
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path > (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id ASC) ORDER BY m_path ASC, post_id ASC;";
            }

            arguments.add(id);
            arguments.add(id);
            arguments.add(since);
        }
        else if( since != null && limit != null && desc != null){
            if(desc) {
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path < (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id DESC LIMIT ?) ORDER BY m_path DESC, post_id DESC;";
            }
            else{
                sql = "SELECT * FROM posts WHERE thread_id = ? AND m_path[1] = ANY" +
                        " (SELECT post_id FROM posts WHERE thread_id = ? AND parent_id = 0 AND" +
                        " m_path > (SELECT m_path FROM posts WHERE post_id = ?)" +
                        " ORDER BY post_id ASC LIMIT ?) ORDER BY m_path ASC, post_id ASC;";
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
        String sql = "INSERT INTO posts (post_id, thread_id, thread, forum_id, forum, user_id, author, parent_id, " +
                "message, created, is_edited) VALUES (?, ?, ?::citext, ?, ?::citext, ?, ?::citext, ?, ?, ?::timestamptz, ?) RETURNING *;";

        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for(Post p : posts) {
                preparedStatement.setInt(1, (int)p.getId());
                preparedStatement.setInt(2, (int)p.getThread_id());
                preparedStatement.setString(3, p.getThread());
                preparedStatement.setInt(4, (int)p.getForum_id());
                preparedStatement.setString(5, p.getForum());
                preparedStatement.setInt(6, (int)p.getUser_id());
                preparedStatement.setString(7, p.getAuthor());
                preparedStatement.setInt(8, p.getParent());
                preparedStatement.setString(9, p.getMessage());
                preparedStatement.setTimestamp(10, p.getCreated());
                preparedStatement.setBoolean(11, p.getEdited());
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