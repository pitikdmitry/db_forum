package db.forum.repository;

import db.forum.Converter.ForumConverter;
import db.forum.DTO.ForumDTO;
import db.forum.Mappers.ForumDTOMapper;
import db.forum.model.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ForumRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ForumConverter forumConverter;

    @Autowired
    public ForumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.forumConverter = new ForumConverter(jdbcTemplate);
    }

    public Forum get_by_id(int forum_id) {
        ForumDTO resultForumDTO = null;
        Forum resultForum = null;
        String sql = "SELECT * FROM forums WHERE forum_id = ?;";
        try {
            Object[] args = new Object[]{forum_id};
            resultForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
            resultForum = forumConverter.getModel(resultForumDTO);
            return resultForum;
        }
        catch (Exception ex) {
            System.out.println("[ForumRepository.get_by_id] exc: " + ex);
            return null;
        }
    }

    public Forum get_by_slug(String slug) {
        ForumDTO resultForumDTO = null;
        Forum resultForum = null;
        String sql = "SELECT * FROM forums WHERE slug = ?::citext;";
        try {
            Object[] args = new Object[]{slug};
            resultForumDTO = jdbcTemplate.queryForObject(sql, args, new ForumDTOMapper());
            resultForum = forumConverter.getModel(resultForumDTO);
            return resultForum;
        }
        catch (Exception ex) {
            System.out.println("[ForumRepository.get_by_slug] exc: " + ex);
            return null;
        }
    }

}
