package db.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Forum {
    @JsonIgnore
    private Integer forum_id;
    private Integer posts;
    private String slug;
    private Integer threads;
    private String title;
    private String user;//nickname
    private boolean is_loaded;

    public Forum() {
        is_loaded = false;
    }

    public Forum(Integer forum_id, Integer posts, String slug, Integer threads, String title, String user) {

        this.forum_id = forum_id;
        this.posts = posts;
        this.slug = slug;
        this.threads = threads;
        this.title = title;
        this.user = user;
        this.is_loaded = true;
    }

    public Integer getForum_id() { return forum_id; }
    public Integer getPosts() { return posts; }
    public String getSlug() { return slug; }
    public Integer getThreads() { return threads; }
    public String getTitle() { return title; }
    public String getUser() { return user; }

    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setPosts(Integer posts) { this.posts = posts; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setThreads(Integer threads) { this.threads = threads; }
    public void setTitle(String title) { this.title = title; }
    public void setUser(String user) { this.user = user; }

//    public void fill(Integer forum_id, Integer posts, String slug, Integer threads, String title, String user) {
    public void fill(Integer forum_id, String slug, String title, String user) {

        this.forum_id = forum_id;
//        this.posts = posts;
        this.slug = slug;
//        this.threads = threads;
        this.title = title;
        this.user = user;
        this.is_loaded = true;
    }
}
