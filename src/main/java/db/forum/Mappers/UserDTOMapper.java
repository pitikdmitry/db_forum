package db.forum.Mappers;

import db.forum.DTO.UserDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDTOMapper implements RowMapper<UserDTO> {
    @Override
    public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final UserDTO userDTO = new UserDTO();
        userDTO.setUser_id(rs.getInt("user_id"));
        userDTO.setNickname(rs.getString("nickname"));
        userDTO.setEmail(rs.getString("email"));
        userDTO.setFullname(rs.getString("fullname"));
        userDTO.setAbout(rs.getString("about"));

        return userDTO;
    }
}