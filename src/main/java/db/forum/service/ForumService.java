package db.forum.service;

import db.forum.Converter.ForumConverter;
import db.forum.DTO.ForumDTO;
import db.forum.Mappers.ForumDTOMapper;
import db.forum.model.Forum;
import db.forum.model.Message;
import db.forum.model.Thread;
import db.forum.model.User;
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
    private final ForumConverter forumConverter;


    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        userRepository = new UserRepository(jdbcTemplate);
        forumConverter = new ForumConverter(jdbcTemplate);
    }

    public ResponseEntity<?> create(Forum forum) {
        ForumDTO resultForumDTO = null;
        Forum resultForum = null;
        String sql = "INSERT INTO forums (slug, user_id, title) VALUES (?, ?, ?)" +
                " RETURNING *;";

        User user = userRepository.get_by_nickname(forum.getUser());

        Integer user_id = null;
        try {
            user_id = user.getUser_id();
        }
        catch(Exception ex) {
            System.out.println("[ForumService] User not found!");
            Message message = new Message("Can't find user with id #42");
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
        }
        return null;
    }

    public Thread createThread(Thread thread, String slug) {
        return null;
    }

    public Forum getDetails(String slug) {
        return null;
    }

    public String getThreads(String slug, Integer limit, String since, Boolean desc) {
        return null;
    }

    public String getUsers(String slug, Integer limit, String since, Boolean desc) {
        return null;
    }



}
