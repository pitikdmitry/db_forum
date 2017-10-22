package db.forum.repository;

import db.forum.Converter.ForumConverter;
import db.forum.Converter.ThreadConverter;
import db.forum.DTO.ThreadDTO;
import db.forum.model.Thread;
import db.forum.Mappers.ThreadDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ThreadRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ThreadConverter threadConverter;

    @Autowired
    public ThreadRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.threadConverter = new ThreadConverter(jdbcTemplate);
    }

    public Thread get_by_id(int thread_id) {
        ThreadDTO resultThreadDTO = null;
        Thread resultThread = null;
        String sql = "SELECT * FROM threads WHERE thread_id = ?;";
        try {
            Object[] args = new Object[]{thread_id};
            resultThreadDTO = jdbcTemplate.queryForObject(sql, args, new ThreadDTOMapper());
            resultThread = threadConverter.getModel(resultThreadDTO);
            return resultThread;
        }
        catch (Exception ex) {
            System.out.println("[ThreadRepository.get_by_id] exc: " + ex);
            return null;
        }
    }
}

