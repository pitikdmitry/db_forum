package db.forum.DTO;

public class ForumDTO {
    private Integer forum_id;
    private Integer posts;
    private String slug;
    private Integer threads;
    private String title;
    private Integer user_id;

    public ForumDTO() {}

    public ForumDTO(Integer forum_id, String slug, String user,  String title, Integer user_id) {
        this.forum_id = forum_id;
        this.slug = slug;
        this.title = title;
        this.user_id = user_id;
    }

    public Integer getForum_id() { return forum_id; }
    public Integer getPosts() { return posts; }
    public String getSlug() { return slug; }
    public Integer getThreads() { return threads; }
    public String getTitle() { return title; }
    public Integer getUser_id() { return user_id; }

    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setPosts(Integer posts) { this.posts = posts; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setThreads(Integer threads) { this.threads = threads; }
    public void setTitle(String title) { this.title = title; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
}
