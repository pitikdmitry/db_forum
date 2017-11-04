package db.forum.Mappers;

import db.forum.model.Forum;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumMapper implements RowMapper<Forum> {
    @Override
    public Forum mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Forum forum = new Forum();
        forum.setForum_id(rs.getInt("forum_id"));
        forum.setPosts(rs.getInt("posts"));
        forum.setSlug(rs.getString("slug"));
        forum.setThreads(rs.getInt("threads"));
        forum.setTitle(rs.getString("title"));
        forum.setUser_id(rs.getInt("user_id"));
        forum.setUser(rs.getString("user_nickname"));
        return forum;
    }
}
