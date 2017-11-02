package db.forum.Mappers;

import db.forum.model.Vote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteMapper implements RowMapper<Vote> {
    @Override
    public Vote mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Vote vote = new Vote();
        vote.setVote_id(rs.getInt("vote_id"));
        vote.setThread_id(rs.getInt("thread_id"));
        vote.setUser_id(rs.getInt("user_id"));
        vote.setNickname(rs.getString("nickname"));
        vote.setVoice(rs.getInt("vote_value"));
        return vote;
    }
}