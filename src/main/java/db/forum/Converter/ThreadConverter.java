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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        if(created != null) {
            created = changeDateFormat(created);
        }
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

    private String changeDateFormat(String str) {
//        System.out.println("LENGTHHHH: " + str.length());
//        System.out.println(str);
        String OLD_FORMAT = null;
        String NEW_FORMAT = null;
        int lengt = str.length();
        String str2 = null;

        if(str.length() == 25) {
            OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SS";
            NEW_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";
        }
        else if(str.length() == 26) {
            OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
            NEW_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }
        else if(str.length() == 29) {
            str2 = str.substring(0, 23);
            str2 += str.substring(26, 29);
            OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
            NEW_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            str = str2;
        }

        String oldDateString = str;
        String newDateString = null;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);

        Date d = null;
        int offset = 0;
        try {
            d = sdf.parse(oldDateString);
            offset = d.getTimezoneOffset();
//            d.setMinutes(d.getMinutes() + offset);
            d.setHours(d.getHours() + offset / 60);
        }
        catch (ParseException e) {
            System.out.println("[EXc format date]");
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);
        return newDateString;
    }

}
