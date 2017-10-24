package db.forum.service;

import db.forum.Converter.ForumConverter;
import db.forum.Converter.PostConverter;
import db.forum.Converter.ThreadConverter;
import db.forum.DTO.ForumDTO;
import db.forum.DTO.PostDTO;
import db.forum.DTO.VoteDTO;
import db.forum.Mappers.ForumDTOMapper;
import db.forum.Mappers.PostDTOMapper;
import db.forum.Mappers.VoteDTOMapper;
import db.forum.model.*;
import db.forum.model.Thread;
import db.forum.repository.DateRepository;
import db.forum.repository.ForumRepository;
import db.forum.repository.ThreadRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class ThreadService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ThreadRepository threadRepository;
    private final ForumConverter forumConverter;
    private final PostConverter postConverter;
    private final DateRepository dateRepository;

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.forumConverter = new ForumConverter(jdbcTemplate);
        this.dateRepository = new DateRepository();
        this.postConverter = new PostConverter(jdbcTemplate);
    }

    public ResponseEntity<?> createPosts(String slug_or_id, ArrayList<Post> posts) {
        ArrayList<Post> resultArr = new ArrayList<>();
        String created = null;

        for (Post p : posts) {
            try {
                Post res = createOnePost(slug_or_id, p, created);
                created = res.getCreated();
                resultArr.add(res);
            } catch (Exception ex) {

            }
        }

        return new ResponseEntity<>(resultArr, HttpStatus.CREATED);

    }

    public Post createOnePost(String slug_or_id, Post post, String created) {
        PostDTO resultPostDTO = null;
        Post resultPost = null;
        String sql = "INSERT INTO posts (thread_id, forum_id, user_id, parent_id, " +
                "message, created, is_edited) VALUES (?, ?, ?, ?, ?, ?::timestamptz, ?) RETURNING *;";

        User user = null;
        Thread thread = null;
        Integer forum_id = null;
        Integer parent_id = null;
        if (post.getParent() == null) {
            parent_id = 0;
        }
        else {
            parent_id = post.getParent();
        }
//        if (post.getCreated() == null) {
//            created = dateRepository.getCurrentDate();
//        }
        if(created == null) {
            created = dateRepository.getCurrentDate();
        }
        try {
            user = userRepository.get_by_nickname(post.getAuthor());
            thread = threadRepository.get_by_slug_or_id(slug_or_id);
            forum_id = threadRepository.get_forum_id_by_thread_id(thread.getId());
//            forum =  forumRepository.get_by_thread(thread.getId());
        } catch (Exception ex) {
            System.out.println("[ThreadService] User or thread not found!");
            Message message = new Message("createOnePost answer ");
//            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            Object[] args = new Object[]{thread.getId(), forum_id, user.getUser_id(), parent_id,
                    post.getMessage(), created, false};

            resultPostDTO = jdbcTemplate.queryForObject(sql, args, new PostDTOMapper());
            resultPost = postConverter.getModel(resultPostDTO);
            return resultPost;
//            return new ResponseEntity<>(resultPost, HttpStatus.CREATED);
        } catch (Exception ex) {
            System.out.println("POST NOT CREATED " + ex);
            Message message = new Message("createOnePost: answer2 ");
//            return message;
//            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return null;
    }

    public ResponseEntity<?> vote(String slug_or_id, Vote vote) {
        User user = null;
        Thread resultThread = null;
        Thread currentThread = null;
        VoteDTO resultVoteDTO = null;
        Vote resultVote = null;
        Object[] args = null;

        currentThread = threadRepository.get_by_slug_or_id(slug_or_id);

        try {
            //Если голоса нет то голосуем
            user = userRepository.get_by_nickname(vote.getNickname());
            String sql = "INSERT INTO vote (thread_id, user_id, vote_value) VALUES (?, ?, ?) RETURNING *;";
            args = new Object[]{currentThread.getId(), user.getUser_id(), vote.getVoice()};
            resultVoteDTO = jdbcTemplate.queryForObject(sql, args, new VoteDTOMapper());

            resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), false);
            //в resultThread уже лежит с обновленным рейтингом
            return new ResponseEntity<>(resultThread, HttpStatus.OK);
        } catch (Exception ex) {
            Vote exists_vote = threadRepository.get_exists_vote(vote.getNickname(), slug_or_id);

            if (exists_vote != null) {
                //Если голос такой же как новый то голосование не делаем
                if ((int) exists_vote.getVoice() == (int) vote.getVoice()) {
                    System.out.println("here");
                    resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
//                     resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice());
                    return new ResponseEntity<>(resultThread, HttpStatus.OK);
                }

                String sql = "UPDATE vote SET vote_value = ? WHERE vote_id = ? RETURNING *;";
                args = new Object[]{vote.getVoice(), exists_vote.getVote_id()};
                resultVoteDTO = jdbcTemplate.queryForObject(sql, args, new VoteDTOMapper());

                resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), true);
                //в resultThread уже лежит с обновленным рейтингом
                return new ResponseEntity<>(resultThread, HttpStatus.OK);
            }
            return null;
        }
    }

    public ResponseEntity<?> getDetails(String slug_or_id) {
        Thread resultThread = null;
        resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
        if (resultThread == null) {
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultThread, HttpStatus.OK);
    }
}
