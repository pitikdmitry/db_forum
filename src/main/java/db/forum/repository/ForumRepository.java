package db.forum.repository;

import db.forum.Converter.ForumConverter;
import db.forum.DTO.ForumDTO;
import db.forum.Mappers.ForumDTOMapper;
import db.forum.model.Forum;
import db.forum.model.Message;
import db.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class ForumRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ForumConverter forumConverter;

    @Autowired
    public ForumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.forumConverter = new ForumConverter(jdbcTemplate);
    }

    public Forum get_by_id(int forum_id) {
        String sql = "SELECT * FROM forums WHERE forum_id = ?;";
        Object[] args = new Object[]{forum_id};

        ForumDTO resultForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
        return forumConverter.getModel(resultForumDTO);
    }

    public Forum get_by_slug(String slug) {
        String sql = "SELECT * FROM forums WHERE slug = ?::citext;";

        Object[] args = new Object[]{slug};
        ForumDTO resultForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
        return forumConverter.getModel(resultForumDTO);
    }

    public Forum create(Integer user_id, Forum forum) {
        String sql = "INSERT INTO forums (slug, user_id, title) VALUES (?, ?, ?)" +
                " RETURNING *;";
        Object[] args = new Object[]{forum.getSlug(), user_id, forum.getTitle()};
        ForumDTO resultForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
        return forumConverter.getModel(resultForumDTO);
    }

    public Forum getByUserId(Integer user_id) {
        String sql = "SELECT * FROM forums WHERE user_id = ?;";
        Object[] args = new Object[]{user_id};
        ForumDTO existsForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
        return forumConverter.getModel(existsForumDTO);

    }
}
