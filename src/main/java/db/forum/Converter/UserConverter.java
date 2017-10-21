package db.forum.Converter;

import db.forum.DTO.UserDTO;
import db.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class UserConverter {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public UserConverter(JdbcTemplate jdbcTemplate) {
    }

    public User getModel(UserDTO userDTO) {
        User user = new User();

        int user_id = userDTO.getUser_id();
        String about = userDTO.getAbout();
        String email = userDTO.getEmail();
        String fullname = userDTO.getFullname();
        String nickname = userDTO.getNickname();
        user.fill(user_id, about, email, fullname, nickname);

        return  user;
    }

    public List<User> getModelList(List<UserDTO> userDTOs) {
        List<User> users = new ArrayList<>();

        for(UserDTO dto : userDTOs) {
            User user = getModel(dto);
            users.add(user);
        }
        return users;
    }
}
