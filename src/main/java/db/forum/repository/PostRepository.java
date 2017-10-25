package db.forum.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        if(parent_id == null) {
            return null;
        }
        int par_id = parent_id;
        String sql = "SELECT m_path FROM posts WHERE post_id = ?";
        Object[] args = new Object[]{par_id};

        try {
            Array arr = jdbcTemplate.queryForObject(sql, args, Array.class);
            List<Integer> list = new ArrayList<>(Arrays.asList((Integer[]) arr.getArray()));

            if(list.size() == 1) {
                if(list.get(0) == null) {
                    return null;
                }
            }
            return list;
        } catch(Exception ex) {
            System.out.println("CANT GET MASS MAPTH: " + ex);
            return null;
        }
    }

}
