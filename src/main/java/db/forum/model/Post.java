package db.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class Post {
    private String author;
    private Timestamp created;
    private String forum;
    private Integer id;
    private Boolean isEdited;
    private String message;
    private Integer parent;
    private Integer thread;

    public Post() {
        this.isEdited = false;
    }

    public Post(String author, Timestamp created, String forum,
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
    public Timestamp getCreated() { return created; }
    public String getForum() { return forum; }
    public Integer getId() { return id; }
    public Boolean getEdited() { return isEdited; }
    public String getMessage() { return message; }
    public Integer getParent() { return parent; }
    public Integer getThread() { return thread; }

    public void setAuthor(String author) { this.author = author; }
    public void setCreated(Timestamp created) { this.created = created; }
    public void setForum(String forum) { this.forum = forum; }
    public void setId(Integer id) { this.id = id; }
    public void setEdited(Boolean edited) { isEdited = edited; }
    public void setMessage(String message) { this.message = message; }
    public void setParent(Integer parent) { this.parent = parent; }
    public void setThread(Integer thread) { this.thread = thread; }

    public void fill(String author, Timestamp created, String forum,
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

    public JSONObject getJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("author", author);
        if(created != null) {
            jsonObject.put("created", created.toInstant().toString());
        }
        jsonObject.put("forum", forum);
        jsonObject.put("id", id);
        jsonObject.put("isEdited", isEdited);
        jsonObject.put("message", message);
        jsonObject.put("parent", parent);
        jsonObject.put("thread", thread);
        return jsonObject;
    }
    public static JSONArray getJsonArray(List<Post> posts) {
        final JSONArray arr = new JSONArray();
        for (Post p : posts) {
            arr.put(p.getJson());
        }
        return arr;
    }
    public static JSONObject getJsonObjects(User user, Forum forum, Post post, Thread thread) {
        final JSONObject bigObject = new JSONObject();
        if(user != null) {
            bigObject.put("user", user.getJson());
        }
        if(forum != null) {
            bigObject.put("forum", forum.getJson());
        }
        if(post != null) {
            bigObject.put("post", post.getJson());
        }
        if(thread != null) {
            bigObject.put("thread", thread.getJson(true));
        }
        return bigObject;
    }

}
