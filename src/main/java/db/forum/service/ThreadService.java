package db.forum.service;

import db.forum.My_Exceptions.NoPostException;
import db.forum.My_Exceptions.NoThreadException;
import db.forum.My_Exceptions.NoUserException;
import db.forum.model.*;
import db.forum.model.Thread;
import db.forum.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ThreadService {

    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;
    private final DateRepository dateRepository;
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final ForumRepository forumRepository;
    public static int counter = 1;

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.userRepository = new UserRepository(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.dateRepository = new DateRepository();
        this.postRepository = new PostRepository(jdbcTemplate);
        this.voteRepository = new VoteRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
    }
    
    public ResponseEntity<?> createPosts(String slug_or_id, ArrayList<Post> posts) {
        ArrayList<Post> resultArr = new ArrayList<>();
        HashMap<Integer, Integer> userWithForum = new HashMap<>();
        Timestamp created = null;
        try {
            created = Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime());
        } catch(Exception ex) {
        }

        if(posts.size() == 0) {
            Thread currentThread = null;
            ArrayList<Post> newArr = new ArrayList<>();
            try {
                currentThread = threadRepository.get_by_slug_or_id(slug_or_id);
            } catch(Exception ex) {
                Message message = new Message("Can't find post thread by id: " + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(Post.getJsonArray(newArr).toString(), HttpStatus.CREATED);
        }

        Thread thread = null;
        Forum forum = null;
        try {
            thread = threadRepository.get_by_slug_or_id(slug_or_id);
            forum = forumRepository.get_by_slug(thread.getForum());
        } catch (Exception ex) {
            Message message = new Message("Can't find thread or forumby: " + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        for (Post p : posts) {
            try {
                Post res = createOnePost(thread, forum, p, created, userWithForum);
                if(res != null) {
                    resultArr.add(res);
                }
            } catch(NoUserException ex) {
                Message message = new Message("Can't find post author by nickname: " + ex.getAuthor());
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch(NoPostException ex) {
                Message message = new Message("Parent post was created in another thread" + ex.getPostId());
                return new ResponseEntity<>(message, HttpStatus.CONFLICT);
            } catch (Exception ex) {
                //ignored
                Message message = new Message("[ThreadService] POST NOT CREATED database post exception: ");
                return new ResponseEntity<>(message, HttpStatus.BAD_GATEWAY);
            }
        }

        try {
            postRepository.executePosts(resultArr);
            forumRepository.addPostStat(resultArr.size(), forum.getForum_id());
        } catch(Exception e) {

        }
        try{
            userRepository.executeUsersWithForum(userWithForum);
        } catch(DuplicateKeyException ex) {
            //normal
        } catch(Exception ex) {
        }
        return new ResponseEntity<>(Post.getJsonArray(resultArr).toString(), HttpStatus.CREATED);
    }

    private Post createOnePost(Thread thread, Forum forum, Post post, Timestamp created, HashMap<Integer, Integer> userWithForum) throws NoUserException, NoThreadException, NoPostException {
        User user = null;
        try {
            user = userRepository.get_by_nickname(post.getAuthor());
        } catch (Exception ex) {
            throw new NoUserException(post.getAuthor());
        }

        Integer parent_id = 0;
        if(post.getParent() != null) {
            parent_id = post.getParent();
        }

        List<Integer> m_path = null;
        Integer next_post_id = postRepository.getNext();
        if (parent_id != 0) {
            Post parentPost = null;
            try {
                parentPost = postRepository.getById(parent_id);
            } catch (Exception ex) {
                throw new NoPostException(parent_id);
            }

            if(thread.getId() != null) {
                if (!parentPost.getThreadId().equals(thread.getId())) {
                    throw new NoPostException(parent_id);
                }
            }
            m_path = postRepository.get_m_path(parent_id, next_post_id);
        } else {
            m_path = postRepository.get_new_m_path(next_post_id);
        }

        userWithForum.put(user.getUser_id(), forum.getForum_id());

        return new Post(next_post_id, user.getNickname(), created, forum.getSlug(),
                                false, post.getMessage(), parent_id,
                                thread.getId(), m_path);
    }

    public ResponseEntity<?> vote(String slug_or_id, Vote vote) {
        Thread currentThread = null;
        User user = null;
        try {
            currentThread = threadRepository.get_by_slug_or_id(slug_or_id);
        } catch(Exception ex) {
            Message message = new Message("Can't find thread by slug: " + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try {
            //Если голоса нет то голосуем
            user = userRepository.get_by_nickname(vote.getNickname());
        } catch(Exception ex) {
            Message message = new Message("Can't find user by nickname: " + vote.getNickname());
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try{
            voteRepository.create(currentThread.getId(), user, vote.getVoice());
            Thread resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), false);
            //в resultThread уже лежит с обновленным рейтингом
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Vote exists_vote = voteRepository.get_exists_vote(user.getUser_id(), currentThread.getId());

            if (exists_vote != null) {
                //Если голос такой же как новый то голосование не делаем
                if ((int) exists_vote.getVoice() == (int) vote.getVoice()) {
                    return new ResponseEntity<>(currentThread.getJson().toString(), HttpStatus.OK);
                }

                voteRepository.updateVoteValue(exists_vote.getVote_id(), vote.getVoice());
                Thread resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), true);
                //в resultThread уже лежит с обновленным рейтингом
                return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);
            }
            return null;
        }
    }

    public ResponseEntity<?> getDetails(String slug_or_id) {
        try {
            Thread resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);
        } catch(Exception ex) {
            Message message = new Message("Can't find thread by slug: " + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getPosts(String slug_or_id, Integer limit, Integer since, String sort, Boolean desc) {
        Integer threadId = null;
        try{
            threadId = threadRepository.checkThread(slug_or_id);
            if(threadId == null) {
                Message message = new Message("CCan't find threadId by id: " + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
        } catch(Exception ex) {
            Message message = new Message("CCan't find threadId by id: " + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        if(sort == null) {
            try {
                List<Post> responsePosts = postRepository.getPostFlat(threadId, limit, since, desc);
                return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
            } catch(Exception ex) {
            }
        } else {
            if(sort.equals("flat")) {
                try {
                    List<Post> responsePosts = postRepository.getPostFlat(threadId, limit, since, desc);
                    return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
                } catch(NoThreadException ex) {
                    Message message = new Message("Can't find threadId by slug: " + ex.getSlugOrId());
                    return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
                } catch(Exception ex) {
                }
            }
            else if(sort.equals("tree")) {
                try {
                    List<Post> responsePosts = postRepository.getPostTree(threadId, limit, since, desc);
                    return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
                } catch(Exception ex) {
                }
            }
            else if(sort.equals("parent_tree")) {
                try {
                    List<Post> responsePosts = postRepository.getPostsParentTree(threadId, limit, since, desc);
                    return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
                } catch(Exception ex) {
                }
            } else {
                try {
                    List<Post> responsePosts = postRepository.getPostFlat(threadId, limit, since, desc);
                    return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }

    public ResponseEntity<?> update(String slug_or_id, Thread thread) {
        Integer thread_id = null;
        try{
            thread_id = threadRepository.checkThread(slug_or_id);
            if(thread_id == null) {
                Message message = new Message("CCan't find thread by id: %!d(string=" + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
        } catch(Exception ex) {
            Message message = new Message("CCan't find forum by id: %!d(string=" + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        if(thread.getMessage() != null && thread.getTitle() != null) {
            Thread resultThread = threadRepository.updateMessageTitle(thread_id, thread.getMessage(), thread.getTitle());
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);

        } else if(thread.getTitle() != null) {
            Thread resultThread = threadRepository.updateTitle(thread_id, thread.getTitle());
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);

        } else if(thread.getMessage() != null) {
            Thread resultThread = threadRepository.updateMessage(thread_id, thread.getMessage());
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);

        } else {
            Thread responseThread = threadRepository.get_by_id(thread_id);
            return new ResponseEntity<>(responseThread.getJson().toString(), HttpStatus.OK);
        }
    }

}
