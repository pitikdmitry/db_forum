package db.forum.service;

import db.forum.model.*;
import db.forum.model.Thread;
import db.forum.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;
    private final DateRepository dateRepository;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.userRepository = new UserRepository(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.dateRepository = new DateRepository();
        this.postRepository = new PostRepository(jdbcTemplate);
        this.voteRepository = new VoteRepository(jdbcTemplate);
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
                //ignored
            }
        }
        return new ResponseEntity<>(resultArr, HttpStatus.CREATED);
    }

    private Post createOnePost(String slug_or_id, Post post, String created) {
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
        if(created == null) {
            created = dateRepository.getCurrentDate();
        }
        try {
            user = userRepository.get_by_nickname(post.getAuthor());
        } catch (Exception ex) {
            System.out.println("[ThreadService] User not found!");
        }
        try {
            thread = threadRepository.get_by_slug_or_id(slug_or_id);
        } catch (Exception ex) {
            System.out.println("[ThreadService] thread not found!");
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

    public ResponseEntity<?> vote(String slug_or_id, Vote vote) {
        Thread currentThread = null;
        currentThread = threadRepository.get_by_slug_or_id(slug_or_id);

        try {
            //Если голоса нет то голосуем
            User user = userRepository.get_by_nickname(vote.getNickname());

            voteRepository.create(currentThread.getId(), user.getUser_id(), vote.getVoice());
            Thread resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), false);
            //в resultThread уже лежит с обновленным рейтингом
            return new ResponseEntity<>(resultThread, HttpStatus.OK);
        } catch (Exception ex) {
            Vote exists_vote = voteRepository.get_exists_vote(vote.getNickname(), slug_or_id);

            if (exists_vote != null) {
                //Если голос такой же как новый то голосование не делаем
                if ((int) exists_vote.getVoice() == (int) vote.getVoice()) {
                    Thread resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
                    return new ResponseEntity<>(resultThread, HttpStatus.OK);
                }

                voteRepository.updateVoteValue(exists_vote.getVote_id(), vote.getVoice());
                Thread resultThread = threadRepository.increment_vote_rating(currentThread, vote.getVoice(), true);
                //в resultThread уже лежит с обновленным рейтингом
                return new ResponseEntity<>(resultThread, HttpStatus.OK);
            }
            return null;
        }
    }

    public ResponseEntity<?> getDetails(String slug_or_id) {
        Thread resultThread = threadRepository.get_by_slug_or_id(slug_or_id);
        if (resultThread == null) {
            Message message = new Message("Can't find user with id #42");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultThread, HttpStatus.OK);
    }

    public ResponseEntity<?> getPosts(String slug_or_id, Integer limit, Integer since, String sort, Boolean desc) {
        if(sort == null) {
            try {
                List<Post> responsePosts = postRepository.getPosts(slug_or_id, limit, since, desc);
                return new ResponseEntity<>(responsePosts, HttpStatus.OK);
            } catch(Exception ex) {
                System.out.println("[getPosts exc] no sort: ");
            }
        }
        else {
            if(sort.equals("flat")) {
                try {
                    List<Post> responsePosts = postRepository.getPostFlat(slug_or_id, limit, since, desc);
                    return new ResponseEntity<>(responsePosts, HttpStatus.OK);
                } catch(Exception ex) {
                    System.out.println("[getPosts exc] falt sort: ");
                }
            }
            else if(sort.equals("tree")) {
                try {
                    List<Post> responsePosts = postRepository.getPostTree(slug_or_id, limit, since, desc);
                    return new ResponseEntity<>(responsePosts, HttpStatus.OK);
                } catch(Exception ex) {
                    System.out.println("[getPosts exc] tree sort: ");
                }
            }
            else {
                try {
                    List<Post> responsePosts = postRepository.getPostsParentTree(slug_or_id, limit, since, desc);
                    return new ResponseEntity<>(responsePosts, HttpStatus.OK);
                } catch(Exception ex) {
                    System.out.println("[getPosts exc] parenttree sort: ");
                }
            }
        }
        return null;
    }
}
