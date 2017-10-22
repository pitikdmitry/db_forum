package db.forum.service;

import db.forum.Converter.ForumConverter;
import db.forum.Converter.PostConverter;
import db.forum.Converter.ThreadConverter;
import db.forum.DTO.ForumDTO;
import db.forum.DTO.PostDTO;
import db.forum.Mappers.ForumDTOMapper;
import db.forum.Mappers.PostDTOMapper;
import db.forum.model.Forum;
import db.forum.model.Message;
import db.forum.model.Post;
import db.forum.model.User;
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

    public ResponseEntity<?>  createPosts(String slug_or_id, ArrayList<Post> posts) {
        ArrayList<Post> resultArr = new ArrayList<>();
        for(Post p : posts) {
            try {
                Post res = createOnePost(slug_or_id, p);
                resultArr.add(res);
            }
            catch(Exception ex) {

            }
        }

        return new ResponseEntity<>(resultArr, HttpStatus.CREATED);

    }

    public Post createOnePost(String slug_or_id, Post post) {
        PostDTO resultPostDTO = null;
        Post resultPost = null;
        String sql = "INSERT INTO posts (thread_id, forum_id, user_id, parent_id, " +
                "message, created, is_edited) VALUES (?, ?, ?, ?, ?, ?::timestamptz, ?) RETURNING *;";

        User user = null;
        Thread thread = null;
        Integer forum_id = null;
        String created = null;
        Integer thread_id = null;
        Integer parent_id = null;
        if(post.getParent() == null) {
            parent_id = 0;
        }
        if(post.getCreated() == null) {
            created = dateRepository.getCurrentDate();
        }
        try {
            user = userRepository.get_by_nickname(post.getAuthor());

            try{
                thread_id = Integer.parseInt(slug_or_id);
//                thread = threadRepository.get_by_id(thread_id);
            }
            catch(Exception ex) {
                System.out.println("[cant parse int]" + ex);
                thread = threadRepository.get_by_slug(slug_or_id);
                thread_id = thread.getId();
            }
            forum_id = threadRepository.get_forum_id_by_thread_id(thread_id);
//            forum =  forumRepository.get_by_thread(thread.getId());

        }
        catch(Exception ex) {
            System.out.println("[ThreadService] User or thread not found!");
            Message message = new Message("createOnePost answer ");
//            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            Object[] args = new Object[]{thread_id, forum_id, user.getUser_id(), parent_id,
                                        post.getMessage(), created, false};

            resultPostDTO = jdbcTemplate.queryForObject(sql, args, new PostDTOMapper());
            resultPost = postConverter.getModel(resultPostDTO);
            return resultPost;
//            return new ResponseEntity<>(resultPost, HttpStatus.CREATED);
        }
        catch (Exception ex) {
            System.out.println("[ThreadService.createPosts [OTHER EXCEPTION]] " + ex);
            Message message = new Message("createOnePost: answer2 ");
//            return message;
//            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return null;
    }

}
