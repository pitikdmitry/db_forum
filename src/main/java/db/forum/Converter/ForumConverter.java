package db.forum.Converter;

import db.forum.DTO.ForumDTO;
import db.forum.model.Forum;
import db.forum.model.User;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ForumConverter {
    JdbcTemplate jdbcTemplate;
    UserRepository userRepository;

    @Autowired
    public ForumConverter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
    }

    public Forum getModel(ForumDTO forumDTO) {
        Forum forum = new Forum();

        int forum_id = forumDTO.getForum_id();
        String slug = forumDTO.getSlug();

        User user = userRepository.get_by_id(forumDTO.getUser_id());
        String nickname = user.getNickname();

        String title = forumDTO.getTitle();

        forum.fill(forum_id, slug, title, nickname);
        return forum;
    }

}
