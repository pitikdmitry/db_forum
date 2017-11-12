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
            ArrayList<Post> newArr = new ArrayList<>();
            try {
                currentThread = threadRepository.checkThread(slug_or_id);
            } catch(Exception ex) {
                Message message = new Message("Can't find post thread by id: " + slug_or_id);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(Post.getJsonArray(newArr).toString(), HttpStatus.CREATED);
        }

        for (Post p : posts) {
            try {
                Post res = createOnePost(slug_or_id, p, created);
                if(res != null) { ;
                    resultArr.add(res);
                }
            } catch(NoUserException ex) {
                Message message = new Message("Can't find post author by nickname: " + ex.getAuthor());
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch(NoThreadException ex) {
                Message message = new Message("Can't find post thread by id: " + ex.getSlugOrId());
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch(NoPostException ex) {
                Message message = new Message("Parent post was created in another thread");
                return new ResponseEntity<>(message, HttpStatus.CONFLICT);
            } catch (Exception ex) {
                //ignored
                Message message = new Message("RUNTIME EROOR CREATING POST");
                return new ResponseEntity<>(message, HttpStatus.CONFLICT);
            }
        }
        try {
            postRepository.executePosts(resultArr);
            for(Post p : resultArr) {
                postRepository.updateMpath(p.getParent(), p.getId());
            }
        } catch(Exception e) {
            System.out.println(e);
        }
        return new ResponseEntity<>(Post.getJsonArray(resultArr).toString(), HttpStatus.CREATED);
    }

    private Post createOnePost(String slug_or_id_thread, Post post, Timestamp created) throws NoUserException, NoThreadException, NoPostException {
        Thread thread = null;
        Forum forum = null;
        try {
            thread = threadRepository.get_by_slug_or_id(slug_or_id_thread);
            forum = forumRepository.get_by_slug(thread.getForum());
        } catch (Exception ex) {
            System.out.println("[ThreadService] thread not found! or forum not found!");
            throw new NoThreadException(slug_or_id_thread);
        }
        User user = null;
        try {
            user = userRepository.get_by_nickname(post.getAuthor());
        } catch (Exception ex) {
            System.out.println("[ThreadService] User not found!");
            throw new NoUserException(post.getAuthor());
        }

        Integer parent_id = null;
        if (post.getParent() == null) {
            parent_id = 0;
        }
        else {
            parent_id = post.getParent();
            Post parentPost = null;
            try {
                parentPost = postRepository.getById(parent_id);
            } catch (Exception ex) {
                throw new NoPostException(parent_id);
            }
            if(parentPost == null) {
                throw new NoPostException(parent_id);
            } else {
                if(!parentPost.getThread().equals(thread.getSlug())) {
                    throw new NoPostException(parent_id);
                }
            }
        }
        try {
//            Post resultPost = postRepository.createPost(thread, forum, user, parent_id, post.getMessage(),
//                    created, false);
            Post newPost = new Post(post.getId(), user.getNickname(), user.getUser_id(), created, forum.getSlug(),
                                    forum.getForum_id(), false, post.getMessage(), parent_id,
                                    thread.getSlug(), thread.getId());
            newPost.setId(postRepository.getNext());

            if(forum.getPosts() != null) {
                forumRepository.incrementPostStat(forum.getPosts(), forum.getForum_id());
            } else {
                forumRepository.incrementPostStat(0, forum.getForum_id());
            }
            return newPost;
        } catch (Exception ex) {
            System.out.println("[ThreadService] POST NOT CREATED database post exception: " + ex);
        }
        return null;
    }

//    private Boolean check_another_posts(Post post, Integer thread_id) {
//        List<Post> postsWithSpostRepository.getPostsWithByParent(post.getParent(), thread_id);
//        return false;
//    }

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
            Vote exists_vote = voteRepository.get_exists_vote(vote.getNickname(), slug_or_id);

            if (exists_vote != null) {
                //Если голос такой же как новый то голосование не делаем
                if ((int) exists_vote.getVoice() == (int) vote.getVoice()) {
                    Thread resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
                    return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);
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
        try{
            thread_id = threadRepository.checkThreadGetId(slug_or_id);
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
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);
        } else if(thread.getTitle() != null) {
            Thread resultThread = threadRepository.updateTitle(thread_id, thread.getTitle());
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);
        } else if(thread.getMessage() != null) {
            Thread resultThread = threadRepository.updateMessage(thread_id, thread.getMessage());
            return new ResponseEntity<>(resultThread.getJson().toString(), HttpStatus.OK);
        }
        else{
            Thread responseThread = threadRepository.get_by_id(thread_id);
            return new ResponseEntity<>(responseThread.getJson().toString(), HttpStatus.OK);
        }
    }

}
