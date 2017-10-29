package db.forum.DTO;

import java.sql.Timestamp;

public class ThreadDTO {
    private Integer thread_id;
    private String slug;
    private Integer forum_id;
    private Integer user_id;
    private Timestamp created;
    private String message;
    private String title;
    private Integer votes;

    public ThreadDTO() {}

    public ThreadDTO(Integer thread_id, String slug, Integer forum_id,
                     Integer user_id, Timestamp created, String message, String title) {

        this.thread_id = thread_id;
        this.slug = slug;
        this.forum_id = forum_id;
        this.user_id = user_id;
        this.created = created;
        this.message = message;
        this.title = title;
    }

    public Integer getThread_id() { return thread_id; }
    public String getSlug() { return slug; }
    public Integer getForum_id() { return forum_id; }
    public Integer getUser_id() { return user_id; }
    public Timestamp getCreated() { return created; }
    public String getMessage() { return message; }
    public String getTitle() { return title; }
    public Integer getVotes() { return votes; }

    public void setThread_id(Integer thread_id) { this.thread_id = thread_id; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public void setCreated(Timestamp created) { this.created = created; }
    public void setMessage(String message) { this.message = message; }
    public void setTitle(String title) { this.title = title; }
    public void setVotes(Integer votes) { this.votes = votes; }
}
