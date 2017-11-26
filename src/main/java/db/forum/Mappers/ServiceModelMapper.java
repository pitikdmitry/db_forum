package db.forum.Mappers;

import db.forum.model.ServiceModel;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceModelMapper implements RowMapper<ServiceModel> {
    @Override
    public ServiceModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        final ServiceModel serviceModel = new ServiceModel();
        serviceModel.setForum(rs.getInt("forums_count"));
        serviceModel.setPost(rs.getInt("posts_count"));
        serviceModel.setThread(rs.getInt("threads_count"));
        serviceModel.setUser(rs.getInt("users_count"));

        return serviceModel;
    }
}