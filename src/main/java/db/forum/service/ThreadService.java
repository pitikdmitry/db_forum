package db.forum.service;

import db.forum.My_Exceptions.NoPostException;
import db.forum.My_Exceptions.NoThreadException;
import db.forum.My_Exceptions.NoUserException;
import db.forum.model.*;
import db.forum.model.Thread;
import db.forum.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;
    private final DateRepository dateRepository;
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final ForumRepository forumRepository;
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
        Timestamp created = null;
        try {
            created = Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime());
        } catch(Exception ex) {
            System.out.println(ex);
        }

        if(posts.size() == 0) {
            Thread currentThread = null;
            try {
                currentThread = threadRepository.checkThread(slug_or_id);
            } catch(Exception ex) {
                Message message = new Message("Can't find post thread by id: " + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
            if(currentThread == null) {
                Message message = new Message("Can't find post thread by id: " + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
        }
        for (Post p : posts) {
            try {
                Post res = createOnePost(slug_or_id, p, created);
                resultArr.add(res);
            } catch(NoUserException ex) {
                Message message = new Message("Can't find post author by nickname: " + ex.getAuthor());
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch(NoThreadException ex) {
                Message message = new Message("Can't find post thread by id: " + ex.getSlugOrId());
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch(NoPostException ex) {
                Message message = new Message("Parent post was created in another thread");
                return new ResponseEntity<>(message, HttpStatus.CONFLICT);
            } catch (EmptyResultDataAccessException ex) {
                //ignored
                Message message = new Message("Parent post was created in another thread");
                return new ResponseEntity<>(message, HttpStatus.CONFLICT);
            } catch (RuntimeException ex) {
                //ignored
                Message message = new Message("Parent post was created in another thread");
                return new ResponseEntity<>(message, HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(Post.getJsonArray(resultArr).toString(), HttpStatus.CREATED);
    }

    private Post createOnePost(String slug_or_id, Post post, Timestamp created) throws NoUserException, NoThreadException, NoPostException {
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
        try {
            thread = threadRepository.get_by_slug_or_id(slug_or_id);
        } catch (Exception ex) {
            System.out.println("[ThreadService] thread not found!");
            throw new NoThreadException(slug_or_id);
        }
        try {
            user = userRepository.get_by_nickname(post.getAuthor());
        } catch (Exception ex) {
            System.out.println("[ThreadService] User not found!");
            throw new NoUserException(post.getAuthor());
        }
            //check parent's thread and current post thread

            //getting parent post
        Post parents_post = null;
        if(parent_id != 0) {
            try {
                parents_post = postRepository.getById(parent_id);
            } catch (EmptyResultDataAccessException ex) {
                System.out.println("empty post");
                throw new NoPostException(parent_id);
//            throw new EmptyResultDataAccessException(0);
            }
        }
        if(parents_post != null) {
            if(!parents_post.getThread().equals(thread.getId())) {
                throw new NoPostException(parent_id);
            }
        } else {
            if(parent_id != 0) {
                List<Post> another_posts = postRepository.getAnotherPostWithSameParent(parent_id);
                if (check_another_posts(another_posts, thread.getId())) {
                    throw new NoPostException(parent_id);
                }
            }
        }

        try {
            forum_id = threadRepository.get_forum_id_by_thread_id(thread.getId());
        } catch (Exception ex) {
            System.out.println("[ThreadService] forum_id not found!");
        }
        try {
            return postRepository.createPost(thread.getId(), forum_id, user.getUser_id(), parent_id, post.getMessage(),
                    created, false);
        } catch (Exception ex) {
            System.out.println("[ThreadService] POST NOT CREATED database post exception: " + ex);
        }
        return null;
    }

    private Boolean check_another_posts(List<Post> posts, Integer thread_id) {
        for(int i = 0; i < posts.size(); ++i) {
            if(posts.get(i).getThread() != thread_id) {
                return true;
                //разные ветки
            }
        }
        return false;
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
            voteRepository.create(currentThread.getId(), user.getUser_id(), vote.getVoice());
            Thread resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), false);
            //в resultThread уже лежит с обновленным рейтингом
            return new ResponseEntity<>(resultThread.getJson(true).toString(), HttpStatus.OK);
        } catch (Exception ex) {
            Vote exists_vote = voteRepository.get_exists_vote(vote.getNickname(), slug_or_id);

            if (exists_vote != null) {
                //Если голос такой же как новый то голосование не делаем
                if ((int) exists_vote.getVoice() == (int) vote.getVoice()) {
                    Thread resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
                    return new ResponseEntity<>(resultThread.getJson(true).toString(), HttpStatus.OK);
                }

                voteRepository.updateVoteValue(exists_vote.getVote_id(), vote.getVoice());
                Thread resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), true);
                //в resultThread уже лежит с обновленным рейтингом
                return new ResponseEntity<>(resultThread.getJson(true).toString(), HttpStatus.OK);
            }
            return null;
        }
    }

    public ResponseEntity<?> getDetails(String slug_or_id) {
        Thread resultThread = null;
        try {
            resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
        } catch(Exception ex) {
            Message message = new Message("Can't find thread by slug: " + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        if (resultThread == null) {
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultThread.getJson(true).toString(), HttpStatus.OK);
    }

    public ResponseEntity<?> getPosts(String slug_or_id, Integer limit, Integer since, String sort, Boolean desc) {
        try{
            Thread thread = threadRepository.checkThread(slug_or_id);
            if(thread == null) {
                Message message = new Message("CCan't find forum by id: %!d(string=" + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
        } catch(Exception ex) {
            Message message = new Message("CCan't find forum by id: %!d(string=" + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        if(sort == null) {
            try {
                List<Post> responsePosts = postRepository.getPosts(slug_or_id, limit, since, desc);
                return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
            } catch(Exception ex) {
                System.out.println("[getPosts exc] no sort: ");
            }
        }
        else {
            if(sort.equals("flat")) {
                try {
                    List<Post> responsePosts = postRepository.getPostFlat(slug_or_id, limit, since, desc);
                    return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
                } catch(NoThreadException ex) {
                    Message message = new Message("Can't find thread by slug: " + ex.getSlugOrId());
                    return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
                } catch(Exception ex) {
                    System.out.println("[getPosts exc] falt sort: ");
                }
            }
            else if(sort.equals("tree")) {
                try {
                    List<Post> responsePosts = postRepository.getPostTree(slug_or_id, limit, since, desc);
                    return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
                } catch(Exception ex) {
                    System.out.println("[getPosts exc] tree sort: ");
                }
            }
            else {
                try {
                    List<Post> responsePosts = postRepository.getPostsParentTree(slug_or_id, limit, since, desc);
                    return new ResponseEntity<>(Post.getJsonArray(responsePosts).toString(), HttpStatus.OK);
                } catch(Exception ex) {
                    System.out.println("[getPosts exc] parenttree sort: ");
                }
            }
        }
        return null;
    }

    public ResponseEntity<?> update(String slug_or_id, Thread thread) {
        Integer thread_id = null;
        try {
            thread_id = threadRepository.get_id_from_slug_or_id(slug_or_id);
        } catch(Exception ex) {
            Message message = new Message("Can't find thread by slug: " + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        try{
            Thread threadres = threadRepository.checkThread(slug_or_id);
            if(thread == null) {
                Message message = new Message("CCan't find forum by id: %!d(string=" + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
        } catch(Exception ex) {
            Message message = new Message("CCan't find forum by id: %!d(string=" + slug_or_id);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        if(thread.getMessage() != null && thread.getTitle() != null) {
            Thread resultThread = threadRepository.updateMessageTitle(thread_id, thread.getMessage(), thread.getTitle());
            return new ResponseEntity<>(resultThread.getJson(true).toString(), HttpStatus.OK);
        } else if(thread.getTitle() != null) {
            Thread resultThread = threadRepository.updateTitle(thread_id, thread.getTitle());
            return new ResponseEntity<>(resultThread.getJson(true).toString(), HttpStatus.OK);
        } else if(thread.getMessage() != null) {
            Thread resultThread = threadRepository.updateMessage(thread_id, thread.getMessage());
            return new ResponseEntity<>(resultThread.getJson(true).toString(), HttpStatus.OK);
        }
        else{
            Thread responseThread = threadRepository.get_by_id(thread_id);
            return new ResponseEntity<>(responseThread.getJson(true).toString(), HttpStatus.OK);
        }
    }
}
