package db.forum.Converter;

import db.forum.DTO.ForumDTO;
import db.forum.DTO.ThreadDTO;
import db.forum.DTO.UserDTO;
import db.forum.model.Forum;
import db.forum.model.User;
import db.forum.model.Thread;
import db.forum.repository.DateRepository;
import db.forum.repository.ForumRepository;
import db.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThreadConverter {
    JdbcTemplate jdbcTemplate;
    UserRepository userRepository;
    ForumRepository forumRepository;
    DateRepository dateRepository;

    @Autowired
    public ThreadConverter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = new UserRepository(jdbcTemplate);
        this.forumRepository = new ForumRepository(jdbcTemplate);
        this.dateRepository = new DateRepository();
    }

    public Thread getModel(ThreadDTO threadDTO) {
        Thread thread = new Thread();

        int thread_id = threadDTO.getThread_id();

        User author = userRepository.get_by_id(threadDTO.getUser_id());
        String nickname = author.getNickname();
        Timestamp created = null;
        if(threadDTO.getCreated() != null) {
            created = threadDTO.getCreated();
        }
//        try {
//            if (created != null) {
//                created = dateRepository.changeDateFormat(created);
//            }
//        }
//        catch(Exception eee) {
//            System.out.println("BAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD");
//        }
        Forum forum = forumRepository.get_by_id(threadDTO.getForum_id());
        String forumSlug = forum.getSlug();

        String message = threadDTO.getMessage();
        String slug = threadDTO.getSlug();
        String title = threadDTO.getTitle();
        Integer votes = null;
        try {
             votes = threadDTO.getVotes();//ПОКА НЕТУ
        }
        catch(Exception e) {
            System.out.println("VOOOOOOOOOOOOOOOOOOTE FUCK");
        }
        thread.fill(thread_id, nickname, created, forumSlug, message, slug, title, votes);
        return thread;
    }

    public List<Thread> getModelList(List<ThreadDTO> threadDTOs) {
        List<Thread> threads = new ArrayList<>();
        try{
            for(ThreadDTO dto : threadDTOs) {
                Thread thread = getModel(dto);
                threads.add(thread);
            }
        }
        catch(Exception ex) {
            System.out.println("JFILSUHDJKNSDKLF");
        }
        return threads;
    }


}
