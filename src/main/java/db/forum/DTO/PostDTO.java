package db.forum.DTO;

import java.sql.Timestamp;

public class PostDTO {
    private Integer post_id;
    private Integer thread_id;
    private Integer forum_id;
    private Integer user_id;
    private Integer parent_id;
    private String message;
    private Timestamp created;
    private Boolean is_edited;

    public PostDTO(Integer post_id, Integer thread_id, Integer forum_id,
                   Integer user_id, Integer parent_id, String message,
                   Timestamp created, Boolean is_edited) {
        this.post_id = post_id;
        this.thread_id = thread_id;
        this.forum_id = forum_id;
        this.user_id = user_id;
        this.parent_id = parent_id;
        this.message = message;
        this.created = created;
        this.is_edited = is_edited;
    }

    public PostDTO() { }

    public Integer getPost_id() { return post_id; }
    public Integer getThread_id() { return thread_id; }
    public Integer getForum_id() { return forum_id; }
    public Integer getUser_id() { return user_id; }
    public Integer getParent_id() { return parent_id; }
    public String getMessage() { return message; }
    public Timestamp getCreated() { return created; }
    public Boolean getIs_edited() { return is_edited; }

    public void setPost_id(Integer post_id) { this.post_id = post_id; }
    public void setThread_id(Integer thread_id) { this.thread_id = thread_id; }
    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public void setParent_id(Integer parent_id) { this.parent_id = parent_id; }
    public void setMessage(String message) { this.message = message; }
    public void setCreated(Timestamp created) { this.created = created; }
    public void setIs_edited(Boolean is_edited) { this.is_edited = is_edited; }
}
