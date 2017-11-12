package db.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class Post {
    private Integer id;
    private String author;
    private Integer user_id;
    private Timestamp created;
    private String forum;
    private Integer forum_id;
    private Boolean isEdited;
    private String message;
    private Integer parent;
    private String thread;
    private Integer thread_id;

    public Post() {
        this.isEdited = false;
    }

    public Post(Integer id, String author, Integer user_id,
                Timestamp created, String forum, Integer forum_id,
                Boolean isEdited, String message, Integer parent,
                String thread, Integer thread_id) {
        this.id = id;
        this.author = author;
        this.user_id = user_id;
        this.created = created;
        this.forum = forum;
        this.forum_id = forum_id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.thread_id = thread_id;
    }

    public Post(String author, Integer user_id,
                Timestamp created, String forum, Integer forum_id,
                Boolean isEdited, String message, Integer parent,
                String thread, Integer thread_id) {
        this.author = author;
        this.user_id = user_id;
        this.created = created;
        this.forum = forum;
        this.forum_id = forum_id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.thread_id = thread_id;
    }

    public String getAuthor() { return author; }
    public Timestamp getCreated() { return created; }
    public String getForum() { return forum; }
    public Integer getId() { return id; }
    public Boolean getEdited() { return isEdited; }
    public String getMessage() { return message; }
    public Integer getParent() { return parent; }
    public String getThread() { return thread; }

    public Integer getUser_id() { return user_id; }
    public Integer getForum_id() { return forum_id; }
    public Integer getThread_id() { return thread_id; }

    public void setAuthor(String author) { this.author = author; }
    public void setCreated(Timestamp created) { this.created = created; }
    public void setForum(String forum) { this.forum = forum; }
    public void setId(Integer id) { this.id = id; }
    public void setEdited(Boolean edited) { isEdited = edited; }
    public void setMessage(String message) { this.message = message; }
    public void setParent(Integer parent) { this.parent = parent; }
    public void setThread(String thread) { this.thread = thread; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setThread_id(Integer thread_id) { this.thread_id = thread_id; }

    public void fill(Integer id, String author, Integer user_id,
                     Timestamp created, String forum, Integer forum_id,
                     Boolean isEdited, String message, Integer parent,
                     String thread, Integer thread_id) {
        this.id = id;
        this.author = author;
        this.user_id = user_id;
        this.created = created;
        this.forum = forum;
        this.forum_id = forum_id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.thread_id = thread_id;
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
        if(parent != null && parent != 0) {
            jsonObject.put("parent", parent);
        }
        jsonObject.put("thread", thread_id);
        return jsonObject;
    }

    public static JSONArray getJsonArray(List<Post> posts) {
        final JSONArray arr = new JSONArray();
        for (Post p : posts) {
            JSONObject obj = p.getJson();
            arr.put(obj);
        }
        return arr;
    }

    public static JSONObject getJsonObjects(User user, Forum forum, Post post, Thread thread) {
        final JSONObject bigObject = new JSONObject();
        if(user != null) {
            bigObject.put("author", user.getJson());
        }
        if(forum != null) {
            bigObject.put("forum", forum.getJson());
        }
        if(post != null) {
            bigObject.put("post", post.getJson());
        }
        if(thread != null) {
            bigObject.put("thread", thread.getJson());
        }
        return bigObject;
    }

}
