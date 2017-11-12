package db.forum.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class Thread {
    private Integer id;
    private String author;//nickname
    @JsonIgnore
    private Integer user_id;
    private Timestamp created;
    private String forum;
    @JsonIgnore
    private Integer forum_id;
    private String message;
    private String slug;
    private String title;
    private Integer votes;

    public Thread() {}

    public Thread(Integer id, String author, Integer user_id,
                  Timestamp created, String forum, Integer forum_id,
                  String message, String slug, String title, Integer votes) {
        this.id = id;
        this.author = author;
        this.user_id = user_id;
        this.created = created;
        this.forum = forum;
        this.forum_id = forum_id;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
    }

    public void fill(Integer id, String author, Integer user_id,
                     Timestamp created, String forum, Integer forum_id,
                     String message, String slug, String title, Integer votes) {
        this.id = id;
        this.author = author;
        this.user_id = user_id;
        this.created = created;
        this.forum = forum;
        this.forum_id = forum_id;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
    }

    public Integer getId() { return id; }

    public String getAuthor() { return author; }

    public Timestamp getCreated() { return created; }
    public String getForum() { return forum; }
    public String getMessage() { return message; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public Integer getVotes() { return votes; }
    public Integer getUser_id() { return user_id; }
    public Integer getForum_id() { return forum_id; }

    public void setId(Integer id) { this.id = id; }
    public void setAuthor(String author) { this.author = author; }
    public void setCreated(Timestamp created) { this.created = created;}
    public void setForum(String forum) { this.forum = forum; }
    public void setMessage(String message) { this.message = message; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
    public void setVotes(Integer votes) { this.votes = votes; }

    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }

    public JSONObject getJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("author", author);
        if(created != null) {
            jsonObject.put("created", created.toInstant().toString());
        }
        jsonObject.put("forum", forum);
        jsonObject.put("id", id);
        jsonObject.put("message", message);
        if(slug != null) {
            jsonObject.put("slug", slug);
        }
        jsonObject.put("title", title);
        if (votes != 0) {
            jsonObject.put("votes", votes);
        }
        return jsonObject;
    }
    public static JSONArray getJsonArray(List<Thread> threads) {
        final JSONArray arr = new JSONArray();
        for (Thread p : threads) {
            arr.put(p.getJson());
        }
        return arr;
    }
}
