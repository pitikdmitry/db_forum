package db.forum.repository;

import db.forum.Converter.PostConverter;
import db.forum.DTO.PostDTO;
import db.forum.Mappers.PostDTOMapper;
import db.forum.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ThreadRepository threadRepository;
    private final PostConverter postConverter;

    @Autowired
    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.postConverter = new PostConverter(jdbcTemplate);
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
        List<PostDTO> postDTOs = jdbcTemplate.query(sql, arguments.toArray(), new PostDTOMapper());
        return postConverter.getModelList(postDTOs);
    }

    public List<Post> getPostFlat(String slug_or_id, Integer limit, Integer since, Boolean desc) {
        List<Object> arguments = new ArrayList<Object>();
        String sql = "SELECT * FROM posts WHERE thread_id = ?";
        Integer id = threadRepository.get_id_from_slug_or_id(slug_or_id);

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
        List<PostDTO> postDTOs = jdbcTemplate.query(sql, arguments.toArray(), new PostDTOMapper());
        return postConverter.getModelList(postDTOs);
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
        List<PostDTO> postDTOs = jdbcTemplate.query(sql, arguments.toArray(), new PostDTOMapper());
        return postConverter.getModelList(postDTOs);
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

        List<PostDTO> postDTOs = jdbcTemplate.query(sql, arguments.toArray(), new PostDTOMapper());
        return postConverter.getModelList(postDTOs);
    }
}