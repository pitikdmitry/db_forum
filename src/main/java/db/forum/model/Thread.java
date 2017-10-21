package db.forum.model;

public class Thread {
    private String author;//nickname
    private String created;
    private String forum;
    private Integer id;
    private String message;
    private String slug;
    private String title;
    private Integer votes;
    private Boolean is_loaded;

    public Thread() {
        is_loaded = false;
    }

    public Thread(Integer id, String author, String created,
                  String forum, String message, String slug,
                  String title, Integer votes) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
        this.is_loaded = true;
    }

    public void fill(Integer id, String author, String created,
                     String forum, String message, String slug,
                     String title, Integer votes) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
        this.is_loaded = true;
    }

    public Integer getId() { return id; }

    public String getAuthor() { return author; }

    public String getCreated() { return created; }
    public String getForum() { return forum; }
    public String getMessage() { return message; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public Integer getVotes() { return votes; }
    public void setId(Integer id) { this.id = id; }

    public void setAuthor(String author) { this.author = author; }
    public void setCreated(String created) { this.created = created;}
    public void setForum(String forum) { this.forum = forum; }
    public void setMessage(String message) { this.message = message; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
    public void setVotes(Integer votes) { this.votes = votes; }
}
