package db.forum.model;


public class Thread {


    private Integer thread_id;
    private Integer author_id;
    private Integer forum_id;
    private String created;
    private String message;
    private String slug;
    private String title;
    private Integer votes;

    public Thread() {}

    public Thread(Integer thread_id, Integer author_id,
                  Integer forum_id, String created, String message,
                  String slug, String title, Integer votes) {
        this.thread_id = thread_id;
        this.author_id = author_id;
        this.forum_id = forum_id;
        this.created = created;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
    }

    public Integer getThread_id() { return thread_id; }
    public Integer getAuthor_id() { return author_id; }
    public Integer getForum_id() { return forum_id; }
    public String getCreated() { return created; }
    public String getMessage() { return message; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public Integer getVotes() { return votes; }

    public void setThread_id(Integer thread_id) { this.thread_id = thread_id; }
    public void setAuthor_id(Integer author_id) { this.author_id = author_id; }
    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setCreated(String created) { this.created = created; }
    public void setMessage(String message) { this.message = message; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
    public void setVotes(Integer votes) { this.votes = votes; }
}
