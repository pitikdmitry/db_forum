package db.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Forum {
    @JsonIgnore
    private Integer forum_id;
    private Integer posts;
    private String slug;
    private Integer threads;
    private String title;
    @JsonIgnore
    private Integer user_id;
    private String user;

    public Forum() {}

    public Forum(Integer forum_id, Integer posts, String slug, Integer threads, String title, Integer user_id, String user) {

        this.forum_id = forum_id;
        this.posts = posts;
        this.slug = slug;
        this.threads = threads;
        this.title = title;
        this.user_id = user_id;
        this.user = user;
    }

    public Integer getForum_id() { return forum_id; }
    public Integer getPosts() { return posts; }
    public String getSlug() { return slug; }
    public Integer getThreads() { return threads; }
    public String getTitle() { return title; }
    public String getUser() { return user; }
    public Integer getUser_id() { return user_id; }

    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setPosts(Integer posts) { this.posts = posts; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setThreads(Integer threads) { this.threads = threads; }
    public void setTitle(String title) { this.title = title; }
    public void setUser(String user) { this.user = user; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
        public void fill(Integer forum_id, Integer posts, String slug, Integer threads, String title, String user) {

        this.forum_id = forum_id;
        this.posts = posts;
        this.slug = slug;
        this.threads = threads;
        this.title = title;
        this.user = user;
    }

    public JSONObject getJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("posts", posts);
        jsonObject.put("slug", slug);
        jsonObject.put("threads", threads);
        jsonObject.put("title", title);
        jsonObject.put("user", user);
        return jsonObject;
    }

    public static JSONArray getJsonArray(List<Forum> forums) {
        final JSONArray arr = new JSONArray();
        for (Forum p : forums) {
            arr.put(p.getJson());
        }
        return arr;
    }
}
