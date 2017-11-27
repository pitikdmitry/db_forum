package db.forum.Mappers;

import org.springframework.jdbc.core.RowMapper;
import db.forum.model.Thread;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadMapper implements RowMapper<Thread>{
    @Override
    public Thread mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Thread thread = new Thread();
        thread.setId(rs.getInt("thread_id"));
        thread.setAuthor(rs.getString("author"));
        thread.setSlug(rs.getString("slug"));
        thread.setForum(rs.getString("forum"));
        thread.setCreated(rs.getTimestamp("created"));
        thread.setMessage(rs.getString("message"));
        thread.setTitle(rs.getString("title"));
        thread.setVotes(rs.getInt("votes"));
        return thread;
    }
}