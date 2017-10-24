package db.forum.repository;

import db.forum.Converter.ThreadConverter;
import db.forum.Converter.VoteConverter;
import db.forum.DTO.ThreadDTO;
import db.forum.DTO.VoteDTO;
import db.forum.Mappers.ThreadDTOMapper;
import db.forum.Mappers.VoteDTOMapper;
import db.forum.model.Thread;
import db.forum.model.User;
import db.forum.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class VoteRepository {
    private final JdbcTemplate jdbcTemplate;
    private final VoteConverter voteConverter;
    private final UserRepository userRepository;

    @Autowired
    public VoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.voteConverter = new VoteConverter(jdbcTemplate);
        this.userRepository = new UserRepository(jdbcTemplate);
    }

    public Vote get_by_id(Integer vote_id) {
        VoteDTO resultVoteDTO = null;
        Vote resultVote = null;
        String sql = "SELECT * FROM vote WHERE vote_id = ?;";
        try {
            Object[] args = new Object[]{vote_id};
            resultVoteDTO = jdbcTemplate.queryForObject(sql, args, new VoteDTOMapper());
            resultVote = voteConverter.getModel(resultVoteDTO);
            return resultVote;
        }
        catch (Exception ex) {
            System.out.println("[VoteRepository.get_by_id] exc: " + ex);
            return null;
        }
    }

    public Vote get_by_author_nickname(String slug_or_id) {
        VoteDTO resultVoteDTO = null;
        Vote resultVote = null;
        User user = userRepository.get_by_slug_or_id(slug_or_id);
        Integer author_id = user.getUser_id();

        String sql = "SELECT * FROM vote WHERE user_id = ?;";
        try {
            Object[] args = new Object[]{author_id};
            resultVoteDTO = jdbcTemplate.queryForObject(sql, args, new VoteDTOMapper());
            resultVote = voteConverter.getModel(resultVoteDTO);
            return resultVote;
        }
        catch (Exception ex) {
            System.out.println("[VoteRepository.get_by_author_nickname] exc: " + ex);
            return null;
        }
    }
}
