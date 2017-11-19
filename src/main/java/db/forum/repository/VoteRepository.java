package db.forum.repository;

import db.forum.Mappers.VoteMapper;
import db.forum.model.Thread;
import db.forum.model.User;
import db.forum.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class VoteRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    @Autowired
    public VoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
    }

    public Vote create(Integer thread_id, User user, Integer vote_value) {
        String sql = "INSERT INTO vote (thread_id, user_id, vote_value, nickname) VALUES (?, ?, ?, ?::citext) RETURNING *;";
        Object[] args = new Object[]{thread_id, user.getUser_id(), vote_value, user.getNickname()};
        return jdbcTemplate.queryForObject(sql, args, new VoteMapper());
    }

    public Vote get_exists_vote(Integer user_id, Integer thread_id) {
        String sql = "SELECT * FROM vote WHERE user_id = ? and thread_id = ?;";
        Object[] args = new Object[]{user_id, thread_id};
        return jdbcTemplate.queryForObject(sql, args, new VoteMapper());
    }

    public void updateVoteValue(Integer vote_id, Integer vote_value) {
        String sql = "UPDATE vote SET vote_value = ? WHERE vote_id = ?;";
        Object[] args = new Object[]{vote_value, vote_id};
        jdbcTemplate.update(sql, args);
    }

    public Vote get_by_id(Integer vote_id) {
        String sql = "SELECT * FROM vote WHERE vote_id = ?;";
        try {
            Object[] args = new Object[]{vote_id};
            return jdbcTemplate.queryForObject(sql, args, new VoteMapper());
        }
        catch (Exception ex) {
            System.out.println("[VoteRepository.get_by_id] exc: " + ex);
            return null;
        }
    }

    public Vote get_by_author_nickname(String slug_or_id) {
        User user = userRepository.get_by_slug_or_id(slug_or_id);
        Integer author_id = user.getUser_id();

        String sql = "SELECT * FROM vote WHERE user_id = ?;";
        try {
            Object[] args = new Object[]{author_id};
            return jdbcTemplate.queryForObject(sql, args, new VoteMapper());
        }
        catch (Exception ex) {
            System.out.println("[VoteRepository.get_by_author_nickname] exc: " + ex);
            return null;
        }
    }
}
