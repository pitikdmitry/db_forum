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
import db.forum.repository.PostRepository;
import db.forum.repository.ThreadRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ForumService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ThreadRepository threadRepository;
    private final ForumConverter forumConverter;
    private final ThreadConverter threadConverter;
    private final PostRepository postRepository;

    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
        this.forumConverter = new ForumConverter(jdbcTemplate);
        this.threadConverter = new ThreadConverter(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.postRepository = new PostRepository(jdbcTemplate);
    }

    public ResponseEntity<?> create(Forum forum) {
        User user = null;
        try {
            user = userRepository.get_by_nickname(forum.getUser());
        } catch(Exception ex) {
            System.out.println("[ForumService] User not found!");
            Message message = new Message("Can't find user with nickname: " + forum.getUser());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            Forum responseForum = forumRepository.create(user.getUser_id(), forum);
            return new ResponseEntity<>(responseForum.getJson().toString(), HttpStatus.CREATED);
        } catch (DuplicateKeyException ex) {
            System.out.println("[ForumService.DuplicateKeyException] " + ex);
            Forum responseForum = forumRepository.getByUserId(user.getUser_id());
            return new ResponseEntity<>(responseForum.getJson().toString(), HttpStatus.CONFLICT);
        } catch (Exception ex) {
            System.out.println("[OTHER EXCEPTION]: " + ex);
            Message message = new Message("Can't find user with nickname: " + forum.getUser());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> createThread(Thread thread, String slug) {
        Boolean has_slug = false;
        if(thread.getSlug() != null) {
            has_slug = true;
            slug = thread.getSlug();
        }

        Integer user_id = null;
        User user = null;
        Forum forum = null;
        Integer forum_id = null;
        Integer thread_id = null;
        try {
            user = userRepository.get_by_nickname(thread.getAuthor());
        } catch (Exception ex) {
            Message message = new Message("Can't find user with nickname: " + thread.getAuthor());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            forum = forumRepository.get_by_slug(thread.getForum());
            forum_id = forum.getForum_id();
        } catch(Exception ex) {
            try {
                Thread threadTemp = threadRepository.get_by_slug(slug);
                thread_id = threadTemp.getId();
                forum_id = threadRepository.get_forum_id_by_thread_id(thread_id);
            } catch(Exception e) {
                //ignored
                Message message = new Message("Can't find forum with forum_id: " + forum_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
        }
        try {
            Thread responseThread = threadRepository.create(slug, forum_id, user.getUser_id(), thread.getCreated(),
                    thread.getMessage(), thread.getTitle());
                return new ResponseEntity<>(responseThread.getJson(has_slug).toString(), HttpStatus.CREATED);
        } catch (DuplicateKeyException dub) {
            System.out.println("[Dublicate thread exception]: " + dub);
            if(thread_id == null) {
                Thread threadTemp = threadRepository.get_by_slug(slug);
                thread_id = threadTemp.getId();
            }
            Thread responseThread = threadRepository.get_by_id(thread_id);
            return new ResponseEntity<>(responseThread.getJson(has_slug).toString(), HttpStatus.CONFLICT);
        } catch (Exception ex) {
            System.out.println("[OTHER EXCEPTION]: " + ex);
            Message message = new Message("Can't 42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getDetails(String slug) {
        try {
            Forum responseForum = forumRepository.get_by_slug(slug);
            Integer postCount = postRepository.countPostsByForumId(responseForum.getForum_id());
            Integer threadsCount = threadRepository.countThreads(responseForum.getForum_id());
            responseForum.setPosts(postCount);
            responseForum.setThreads(threadsCount);
            return new ResponseEntity<>(responseForum.getJson().toString(), HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[ForumService] get details exc: " + ex);
            Message message = new Message("Error updateByAboutAndFullname: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getThreads(String slug, Integer limit, String since, Boolean desc) {
        Forum forum = null;
        Integer forum_id = null;
        try {
            forum = forumRepository.get_by_slug(slug);
            forum_id = forum.getForum_id();
        }
        catch(Exception ex) {
            System.out.println("[ForumService] getThreads exc: " + ex);
            Message message = new Message("Can't find forum by slug: " + slug);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        String sql = null;
        Object[] args = null;
        if((limit == null) && (since == null) && (desc == null)) {
            sql = "SELECT * FROM threads WHERE forum_id = ? ORDER BY created ASC;";
            args = new Object[]{forum_id};
        }
        else if((since == null) && (desc == null) && (limit != null)) {
            sql = "SELECT * FROM threads WHERE forum_id = ? ORDER BY created ASC LIMIT ?;";
            args = new Object[]{forum_id, limit};
        }else if((limit == null) && (desc == null) && (since != null)) {
            sql = "SELECT * FROM threads WHERE forum_id = ? and created >= ?::timestamptz ORDER BY created ASC;";
            args = new Object[]{forum_id, since};
        }
        else if((limit == null) && (since == null) && (desc != null)) {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum_id = ? ORDER BY created DESC;";
            }
            else {
                sql = "SELECT * FROM threads WHERE forum_id = ? ORDER BY created ASC;";
            }
            args = new Object[]{forum_id};
        }
        else if((since == null) && (desc != null) && (limit != null)) {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum_id = ? ORDER BY created DESC LIMIT ?;";
            }
            else {
                sql = "SELECT * FROM threads WHERE forum_id = ? ORDER BY created ASC LIMIT ?;";
            }
            args = new Object[]{forum_id, limit};
        }
        else if((limit == null) && (since != null) && (desc != null)) {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum_id = ? and created <= ?::timestamptz ORDER BY created DESC;";
            }
            else {
                sql = "SELECT * FROM threads WHERE forum_id = ? and created >= ?::timestamptz ORDER BY created ASC;";
            }
            args = new Object[]{forum_id, since};
        }
        else if((desc == null) && (since != null) && (limit != null)) {
            sql = "SELECT * FROM threads WHERE forum_id = ? and created >= ?::timestamptz ORDER BY created ASC LIMIT ?;";
            args = new Object[]{forum_id, since, limit};
        }
        else {
            if(desc == true) {
                sql = "SELECT * FROM threads WHERE forum_id = ? and created <= ?::timestamptz ORDER BY created DESC LIMIT ?;";
            }
            else {
                sql = "SELECT * FROM threads WHERE forum_id = ? and created >= ?::timestamptz ORDER BY created ASC LIMIT ?;";
            }
            args = new Object[]{forum_id, since, limit};
        }
        List<ThreadDTO> threadsDTO = jdbcTemplate.query(sql, args, new ThreadDTOMapper());
        List<Thread> threads = threadConverter.getModelList(threadsDTO);
        return new ResponseEntity<>(Thread.getJsonArray(threads).toString(), HttpStatus.OK);
    }

    public ResponseEntity<?> getUsers(String slug, Integer limit, String since, Boolean desc) {
        Forum forum = null;
        try {
            forum = forumRepository.get_by_slug(slug);
        } catch(Exception ex) {
            System.out.println(ex);
            Message message = new Message("Can't find forum by slug: " + slug);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            List<User> responseUsers = userRepository.getUsers(forum.getForum_id(), limit, since, desc);
            return new ResponseEntity<>(User.getJsonArray(responseUsers).toString(), HttpStatus.OK);
        }
        catch(Exception ex) {
            //ign
            System.out.println(ex);
            Message message = new Message("[ForumService] Error getUsers: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }



}
