package db.forum.service;

import db.forum.Converter.ForumConverter;
import db.forum.Converter.ThreadConverter;
import db.forum.DTO.ForumDTO;
import db.forum.DTO.ThreadDTO;
import db.forum.Mappers.ForumDTOMapper;
import db.forum.Mappers.ThreadDTOMapper;
import db.forum.model.Forum;
import db.forum.model.Message;
import db.forum.model.Thread;
import db.forum.model.User;
import db.forum.repository.ForumRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ForumService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ForumConverter forumConverter;
    private final ThreadConverter threadConverter;


    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        userRepository = new UserRepository(jdbcTemplate);
        forumRepository = new ForumRepository(jdbcTemplate);
        forumConverter = new ForumConverter(jdbcTemplate);
        threadConverter = new ThreadConverter(jdbcTemplate);
    }

    public ResponseEntity<?> create(Forum forum) {
        ForumDTO resultForumDTO = null;
        Forum resultForum = null;
        String sql = "INSERT INTO forums (slug, user_id, title) VALUES (?, ?, ?)" +
                " RETURNING *;";


        Integer user_id = null;
        User user = null;
        try {
            user = userRepository.get_by_nickname(forum.getUser());
            user_id = user.getUser_id();
        }
        catch(Exception ex) {
            System.out.println("[ForumService] User not found!");
            Message message = new Message("Can't find user with nickname: " + forum.getUser());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        try {
            Object[] args = new Object[]{forum.getSlug(), user_id, forum.getTitle()};

            resultForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
            resultForum = forumConverter.getModel(resultForumDTO);

            return new ResponseEntity<>(resultForum, HttpStatus.CREATED);
        }
        catch (DuplicateKeyException ex) {
            System.out.println("[ForumService.DuplicateKeyException] " + ex);
            sql = "SELECT * FROM forums WHERE user_id = ?;";
            Object[] args = new Object[]{user_id};

            ForumDTO existsForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
            resultForum = forumConverter.getModel(existsForumDTO);
            return new ResponseEntity<>(resultForum, HttpStatus.CONFLICT);
        }
        catch (Exception ex) {
            System.out.println("[OTHER EXCEPTION]: " + ex);
            Message message = new Message("Can't find user with nickname: " + forum.getUser());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> createThread(Thread thread, String slug) {
        ThreadDTO resultThreadDTO = null;
        Thread resultThread = null;
        Boolean has_slug = false;
        if(thread.getSlug() != null) {
            has_slug = true;
            slug = thread.getSlug();
        }

        Integer user_id = null;
        User user = null;
        Forum forum = null;
        Integer forum_id = null;
        try {
            user = userRepository.get_by_nickname(thread.getAuthor());
            user_id = user.getUser_id();

            forum = forumRepository.get_by_slug(thread.getForum());
            forum_id = forum.getForum_id();
        }
        catch(Exception ex) {
            System.out.println("[ForumService] createThread User not found!");
            Message message = new Message("Can't find user with nickname: " + thread.getAuthor());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        try {
            Object[] args = null;
            if(thread.getCreated() != null) {
                String sql = "INSERT INTO threads (slug, forum_id, user_id, created, message, title)" +
                        " VALUES (?::citext, ?, ?, ?, ?, ?) RETURNING *;";
                args = new Object[]{slug, forum_id, user_id, thread.getCreated(),
                        thread.getMessage(), thread.getTitle()};
                resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            }
            else {
                String sql = "INSERT INTO threads (slug, forum_id, user_id, message, title)" +
                        " VALUES (?::citext, ?, ?, ?, ?) RETURNING *;";
                args = new Object[]{slug, forum_id, user_id,
                        thread.getMessage(), thread.getTitle()};
                resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            }

            resultThread = threadConverter.getModel(resultThreadDTO);
            return new ResponseEntity<>(resultThread.getJson(has_slug).toString(), HttpStatus.CREATED);
        }
        catch (Exception ex) {
            System.out.println("[OTHER EXCEPTION]: " + ex);
            Message message = new Message("Can't 42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getDetails(String slug) {
        ForumDTO resultForumDTO = null;
        Forum resultForum = null;
        String sql = "SELECT * FROM forums WHERE slug = ?::citext;";

        try {
            Object[] args = new Object[]{slug};

            resultForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
            resultForum = forumConverter.getModel(resultForumDTO);

            return new ResponseEntity<>(resultForum, HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[ForumService] get details exc: " + ex);
            Message message = new Message("Error updateByAboutAndFullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public String getThreads(String slug, Integer limit, String since, Boolean desc) {
        return null;
    }

    public String getUsers(String slug, Integer limit, String since, Boolean desc) {
        return null;
    }



}
