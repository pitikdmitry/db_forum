package db.forum.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Integer> get_m_path(Integer parent_id) {
        List<Integer> resultMpath = null;
        String sql = "SELECT m_path FROM posts WHERE post_id = ?";
        Object[] args = new Object[]{parent_id};

        try {
            resultMpath = jdbcTemplate.queryForList(sql, args, Integer.class);
            if(resultMpath.size() == 1) {
                if(resultMpath.get(0) == null) {
                    return null;
                }
            }
            return resultMpath;
        } catch(Exception ex) {
            System.out.println("CANT GET MASS MAPTH: " + ex);
            return null;
        }
    }
}
