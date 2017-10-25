package db.forum.repository;

import db.forum.Converter.ForumConverter;
import db.forum.Converter.ThreadConverter;
import db.forum.Converter.VoteConverter;
import db.forum.DTO.ThreadDTO;
import db.forum.DTO.VoteDTO;
import db.forum.Mappers.UserDTOMapper;
import db.forum.Mappers.VoteDTOMapper;
import db.forum.model.Thread;
import db.forum.Mappers.ThreadDTOMapper;
import db.forum.model.User;
import db.forum.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public class ThreadRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ThreadConverter threadConverter;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final VoteConverter voteConverter;

    @Autowired
    public ThreadRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.threadConverter = new ThreadConverter(jdbcTemplate);
        this.voteRepository = new VoteRepository(jdbcTemplate);
        this.userRepository = new UserRepository(jdbcTemplate);
        this.voteConverter = new VoteConverter(jdbcTemplate);
    }

    public Thread get_by_id(int thread_id) {
        ThreadDTO resultThreadDTO = null;
        Thread resultThread = null;
        String sql = "SELECT * FROM threads WHERE thread_id = ?;";
        try {
            Object[] args = new Object[]{thread_id};
            resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            resultThread = threadConverter.getModel(resultThreadDTO);
            return resultThread;
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_by_id] exc: " + ex);
            return null;
        }
    }

    public Thread get_by_slug(String slug) {
        ThreadDTO resultThreadDTO = null;
        Thread resultThread = null;
        String sql = "SELECT * FROM threads WHERE slug = ?::citext;";
        try {
            Object[] args = new Object[]{slug};
            resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            resultThread = threadConverter.getModel(resultThreadDTO);
            return resultThread;
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_by_islug] exc: " + ex);
            return null;
        }
    }

    public Thread get_by_slug_or_id(String slug_or_id) {
        ThreadDTO resultThreadDTO = null;
        Thread resultThread = null;
        String sql = null;
        Object[] args = null;
        try {
            Integer id = Integer.parseInt(slug_or_id);
            sql = "SELECT * FROM threads WHERE thread_id = ?;";
            args = new Object[]{id};
        }
        catch(Exception ex) {
            sql = "SELECT * FROM threads WHERE slug = ?::citext;";
            args = new Object[]{slug_or_id};
        }
        try {
            resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            resultThread = threadConverter.getModel(resultThreadDTO);
            return resultThread;
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_by_slug_or_id] exc: " + ex);
            return null;
        }
    }

    public Integer get_forum_id_by_thread_id(int thread_id) {
        Integer forum_id = null;
        String sql = "SELECT forum_id FROM threads WHERE thread_id = ?;";
        try {
            Object[] args = new Object[]{thread_id};
            forum_id = jdbcTemplate.queryForObject(sql, args, Integer.class);
            return forum_id;
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_forum_id_by_thread_id] exc: " + ex);
            return null;
        }
    }

    public Thread increment_vote_rating(Thread old_thread, Integer vote_value, Boolean double_increment) {
        String sql = null;
        Object[] args = null;
        ThreadDTO resultThreadDTO = null;
        Thread resultThread = null;

        //////////апдейтим количество голосов у данной thread
        try{
            sql = "UPDATE threads SET votes = ? WHERE thread_id = ? RETURNING *;";
            try{
                if(double_increment) {
                    args = new Object[]{(old_thread.getVotes() + 2* vote_value), old_thread.getId()};
                }
                else {
                    args = new Object[]{(old_thread.getVotes() + vote_value), old_thread.getId()};
                }
            }
            catch(Exception ex) {
                args = new Object[]{vote_value, old_thread.getId()};
            }
            resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            resultThread = threadConverter.getModel(resultThreadDTO);
            return resultThread;
        }
        catch(Exception ex) {
            System.out.println(("BAF IF HEREE"));
            return null;
        }
    }

    public Vote get_exists_vote(String nickname, String slug_or_id) {
        VoteDTO resultVoteDTO = null;
        Vote resultVote = null;

        try{
            User user = userRepository.get_by_slug_or_id(nickname);
            Thread thread = get_by_slug_or_id(slug_or_id);

            String sql = "SELECT * FROM vote WHERE user_id = ? and thread_id = ?;";

            Object[] args = new Object[]{user.getUser_id(), thread.getId()};
            resultVoteDTO = jdbcTemplate.queryForObject(sql, args, new VoteDTOMapper());
            resultVote = voteConverter.getModel(resultVoteDTO);
            return resultVote;
        }
        catch (Exception ex) {
            System.out.println("[VoteRepository.get_by_author_nickname] exc: " + ex);
            return null;
        }

    }

    public Integer get_id_from_slug_or_id(String slug_or_id) {
        Integer id = null;
        try {
            id = Integer.parseInt(slug_or_id);
        }
        catch(Exception ex) {
            String sql = "SELECT thread_id FROM threads WHERE slug = ?::citext;";
            Object[] args = new Object[]{slug_or_id};
            id = jdbcTemplate.queryForObject(sql, args, Integer.class);
        }
        return id;
    }
}

