package db.forum.Converter;

import db.forum.DTO.PostDTO;
import db.forum.DTO.ThreadDTO;
import db.forum.model.Forum;
import db.forum.model.Post;
import db.forum.model.User;
import db.forum.model.Thread;
import db.forum.repository.DateRepository;
import db.forum.repository.ForumRepository;
import db.forum.repository.ThreadRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PostConverter {
    JdbcTemplate jdbcTemplate;
    UserRepository userRepository;
    ForumRepository forumRepository;
    DateRepository dateRepository;
    ThreadRepository threadRepository;

    @Autowired
    public PostConverter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
        this.threadRepository = new ThreadRepository(jdbcTemplate);
        this.dateRepository = new DateRepository();
    }

    public Post getModel(PostDTO postDTO) {
        Post post = new Post();

        int post_id = postDTO.getPost_id();

        int thread_id = postDTO.getThread_id();
//        Thread thread = threadRepository.get_by_id(thread_id);


        int forum_id = postDTO.getForum_id();
        Forum forum = forumRepository.get_by_id(postDTO.getForum_id());
        String forumSlug = forum.getSlug();

        int user_id = postDTO.getUser_id();
        User user = userRepository.get_by_id(user_id);
        String author = user.getNickname();

        int parent_id = postDTO.getParent_id();
        String message = postDTO.getMessage();
        Timestamp created = null;
        if(postDTO.getCreated() != null) {
            created = postDTO.getCreated();
        }
//        if(created != null) {
//            created = dateRepository.changeDateFormat(created);
//        }
        Boolean is_edited = postDTO.getIs_edited();

        post.fill(author, created, forumSlug, post_id, is_edited,
                message, parent_id, thread_id);

        return post;
    }

    public List<Post> getModelList(List<PostDTO> postDTOs) {
        List<Post> posts = new ArrayList<>();
        try{
            for(PostDTO dto : postDTOs) {
                Post post = getModel(dto);
                posts.add(post);
            }
        }
        catch(Exception ex) {
            System.out.println("JFILSUHDJKNSDKLF");
        }
        return posts;
    }
}
