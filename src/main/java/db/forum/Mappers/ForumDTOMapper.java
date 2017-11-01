package db.forum.Mappers;

import db.forum.DTO.ForumDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumDTOMapper implements RowMapper<ForumDTO> {
    @Override
    public ForumDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final ForumDTO forumDTO = new ForumDTO();
        forumDTO.setForum_id(rs.getInt("forum_id"));
        forumDTO.setPosts(rs.getInt("posts"));
        forumDTO.setSlug(rs.getString("slug"));
        forumDTO.setThreads(rs.getInt("threads"));
        forumDTO.setTitle(rs.getString("title"));
        forumDTO.setUser_id(rs.getInt("user_id"));
        return forumDTO;
    }
}
