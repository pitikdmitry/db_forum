package db.forum.service;

import db.forum.Mappers.ThreadMapper;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ThreadRepository threadRepository;
    private final PostRepository postRepository;

    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.postRepository = new PostRepository(jdbcTemplate);
    }

    public ResponseEntity<?> create(Forum forum) {
        User user = null;
        try {
            user = userRepository.get_by_nickname(forum.getUser());
        } catch(Exception ex) {
            Message message = new Message("Can't find user with nickname: " + forum.getUser());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            Forum responseForum = forumRepository.create(user, forum);
            return new ResponseEntity<>(responseForum.getJson().toString(), HttpStatus.CREATED);
        } catch (DuplicateKeyException ex) {
            System.out.println("[ForumService.DuplicateKeyException] " + ex);
            Forum responseForum = forumRepository.getByUserId(user.getUser_id());
            return new ResponseEntity<>(responseForum.getJson().toString(), HttpStatus.CONFLICT);
        } catch (Exception ex) {
            System.out.println("[FORUMCREATE: EXCEPTION]" + ex);
            Message message = new Message("[FORUMCREATE: EXCEPTION]");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> createThread(Thread thread, String forum_slug) {
        User user = null;
        try {
            user = userRepository.get_by_nickname(thread.getAuthor());
        } catch (Exception ex) {
            Message message = new Message("Can't find user with nickname: " + thread.getAuthor());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        Forum forum = null;
        try {
            forum = forumRepository.get_by_slug(forum_slug);
        } catch(Exception ex) {
            Message message = new Message("Can't find forum with forum_id: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            Thread responseThread = threadRepository.create(thread.getSlug(), user, thread, forum);

            forumRepository.incrementThreadStat(forum.getForum_id());
            try {
                userRepository.addThreadToUser(user, forum.getForum_id());
            } catch(DuplicateKeyException ex) {
                //normal
            } catch(Exception ex) {
                System.out.println("[createThread BIG TABLE EXCEPTION!!!] + ex");
            }

            return new ResponseEntity<>(responseThread.getJson().toString(), HttpStatus.CREATED);
        } catch (DuplicateKeyException dub) {
            Thread threadTemp = threadRepository.get_by_slug(thread.getSlug());
            return new ResponseEntity<>(threadTemp.getJson().toString(), HttpStatus.CONFLICT);
        } catch (Exception ex) {
            System.out.println("[OTHER EXCEPTION]: " + ex);
            Message message = new Message("Can't 42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getDetails(String slug) {
        try {
            Forum responseForum = forumRepository.get_by_slug(slug);
            return new ResponseEntity<>(responseForum.getJson().toString(), HttpStatus.OK);
        }
        catch (Exception ex) {
            System.out.println("[ForumService] get details exc: " + ex);
            Message message = new Message("Error getDetails FORUM: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getThreads(String slug, Integer limit, String since, Boolean desc) {
        Integer forum_id = null;
        try{
            forum_id = forumRepository.get_id_by_slug(slug);
        } catch(EmptyResultDataAccessException ex) {
            System.out.println("[GET THREADS] CANT FIND FORUM ID !!!!");
            Message message = new Message("Can't find forum by slug: " + slug);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            List<Thread> threads = forumRepository.getThreads(slug, limit, since, desc);
            return new ResponseEntity<>(Thread.getJsonArray(threads).toString(), HttpStatus.OK);
        } catch(Exception ex) {
            //ign
            System.out.println("[GET THREADS EXC] : " + ex);
            return null;
        }
    }

    public ResponseEntity<?> getUsers(String slug, Integer limit, String since, Boolean desc) {
        Integer forum_id = null;
        try {
            forum_id = forumRepository.get_id_by_slug(slug);
        } catch(Exception ex) {
            System.out.println(ex);
            Message message = new Message("Can't find forum by slug: " + slug);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            List<User> responseUsers = userRepository.getUsers(forum_id, limit, since, desc);
            return new ResponseEntity<>(User.getJsonArray(responseUsers).toString(), HttpStatus.OK);
        } catch(Exception ex) {
            //ign
            System.out.println(ex);
            Message message = new Message("[ForumService] Error getUsers: ");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }
}
