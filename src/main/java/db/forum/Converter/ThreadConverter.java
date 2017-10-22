package db.forum.Converter;

import db.forum.DTO.ForumDTO;
import db.forum.DTO.ThreadDTO;
import db.forum.DTO.UserDTO;
import db.forum.model.Forum;
import db.forum.model.User;
import db.forum.model.Thread;
import db.forum.repository.ForumRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThreadConverter {
    JdbcTemplate jdbcTemplate;
    UserRepository userRepository;
    ForumRepository forumRepository;

    @Autowired
    public ThreadConverter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
    }

    public Thread getModel(ThreadDTO threadDTO) {
        Thread thread = new Thread();

        int thread_id = threadDTO.getThread_id();

        User author = userRepository.get_by_id(threadDTO.getUser_id());
        String nickname = author.getNickname();

        String created = threadDTO.getCreated();

        Forum forum = forumRepository.get_by_id(threadDTO.getForum_id());
        String forumSlug = forum.getSlug();

        String message = threadDTO.getMessage();
        String slug = threadDTO.getSlug();
        String title = threadDTO.getTitle();
        Integer votes = null;//ПОКА НЕТУ

        thread.fill(thread_id, nickname, created, forumSlug, message, slug, title, 0);
        return thread;
    }

    public List<Thread> getModelList(List<ThreadDTO> threadDTOs) {
        List<Thread> threads = new ArrayList<>();

        for(ThreadDTO dto : threadDTOs) {
            Thread thread = getModel(dto);
            threads.add(thread);
        }
        return threads;
    }

}
