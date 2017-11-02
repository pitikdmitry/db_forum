package db.forum.Converter;

import db.forum.DTO.VoteDTO;
import db.forum.model.User;
import db.forum.model.Vote;
import db.forum.repository.DateRepository;
import db.forum.repository.ForumRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class VoteConverter {
    JdbcTemplate jdbcTemplate;
    UserRepository userRepository;
    ForumRepository forumRepository;
    DateRepository dateRepository;

    @Autowired
    public VoteConverter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
        this.dateRepository = new DateRepository();
    }

    public Vote getModel(VoteDTO voteDTO) {
        Vote vote = new Vote();

        int vote_id = voteDTO.getVote_id();

        User author = userRepository.get_by_id(voteDTO.getUser_id());
        String nickname = author.getNickname();

        Integer voice = voteDTO.getVote_value();

        vote.fill(vote_id, nickname, voice);
        return vote;
    }
}
