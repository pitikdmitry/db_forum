package db.forum.Mappers;

import db.forum.DTO.PostDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostDTOMapper implements RowMapper<PostDTO> {
    @Override
    public PostDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final PostDTO postDTO = new PostDTO();
        postDTO.setPost_id(rs.getInt("post_id"));
        postDTO.setThread_id(rs.getInt("thread_id"));
        postDTO.setForum_id(rs.getInt("forum_id"));
        postDTO.setUser_id(rs.getInt("user_id"));
        postDTO.setParent_id(rs.getInt("parent_id"));
        postDTO.setMessage(rs.getString("message"));
        postDTO.setCreated(rs.getTimestamp("created"));
        postDTO.setIs_edited(rs.getBoolean("is_edited"));
        return postDTO;
    }
}
