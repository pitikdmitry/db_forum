package db.forum.Mappers;

import db.forum.DTO.ThreadDTO;
import db.forum.DTO.VoteDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteDTOMapper implements RowMapper<VoteDTO> {
    @Override
    public VoteDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final VoteDTO voteDTO = new VoteDTO();
        voteDTO.setVote_id(rs.getInt("vote_id"));
        voteDTO.setThread_id(rs.getInt("thread_id"));
        voteDTO.setUser_id(rs.getInt("user_id"));
        voteDTO.setVote_value(rs.getInt("vote_value"));
        return voteDTO;
    }
}