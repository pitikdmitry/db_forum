package db.forum.Mappers;

import db.forum.DTO.ThreadDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadDTOMapper implements RowMapper<ThreadDTO>{
    @Override
    public ThreadDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final ThreadDTO threadDTO = new ThreadDTO();
        threadDTO.setThread_id(rs.getInt("thread_id"));
        threadDTO.setSlug(rs.getString("slug"));
        threadDTO.setForum_id(rs.getInt("forum_id"));
        threadDTO.setUser_id(rs.getInt("user_id"));
        threadDTO.setCreated(rs.getString("created"));
        threadDTO.setMessage(rs.getString("message"));
        threadDTO.setTitle(rs.getString("title"));
        return threadDTO;
    }
}