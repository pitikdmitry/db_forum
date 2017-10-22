package db.forum.model;

public class Post {
    private String author;
    private String created;
    private String forum;
    private Integer id;
    private Boolean isEdited;
    private String message;
    private Integer parent;
    private Integer thread;

    public Post() {}

    public Post(String author, String created, String forum,
                Integer id, Boolean isEdited, String message,
                Integer parent, Integer thread) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
    }

    public String getAuthor() { return author; }
    public String getCreated() { return created; }
    public String getForum() { return forum; }
    public Integer getId() { return id; }
    public Boolean getEdited() { return isEdited; }
    public String getMessage() { return message; }
    public Integer getParent() { return parent; }
    public Integer getThread() { return thread; }

    public void setAuthor(String author) { this.author = author; }
    public void setCreated(String created) { this.created = created; }
    public void setForum(String forum) { this.forum = forum; }
    public void setId(Integer id) { this.id = id; }
    public void setEdited(Boolean edited) { isEdited = edited; }
    public void setMessage(String message) { this.message = message; }
    public void setParent(Integer parent) { this.parent = parent; }
    public void setThread(Integer thread) { this.thread = thread; }

    public void fill(String author, String created, String forum,
                     Integer id, Boolean isEdited, String message,
                     Integer parent, Integer thread) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
    }
}
