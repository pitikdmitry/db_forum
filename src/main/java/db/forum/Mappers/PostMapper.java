package db.forum.Mappers;

import db.forum.model.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Post postDTO = new Post();
        postDTO.setId(rs.getInt("post_id"));
        postDTO.setAuthor(rs.getString("author"));
        postDTO.setUser_id(rs.getInt("user_id"));
        postDTO.setCreated(rs.getTimestamp("created"));
        postDTO.setForum(rs.getString("forum"));
        postDTO.setForum_id(rs.getInt("forum_id"));
        postDTO.setEdited(rs.getBoolean("is_edited"));
        postDTO.setMessage(rs.getString("message"));
        postDTO.setParent(rs.getInt("parent_id"));
        postDTO.setThread(rs.getString("thread"));
        postDTO.setThread_id(rs.getInt("thread_id"));
        return postDTO;
    }
}
