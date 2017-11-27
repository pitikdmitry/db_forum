package db.forum.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private Integer id;
    private String author;
    private Timestamp created;
    private String forum;
    private Boolean isEdited;
    private String message;
    private Integer parent;
    private Integer thread_id;
    private List<Integer> m_path;

    public Post() {
        this.isEdited = false;
    }

    public Post(Integer id, String author,
                Timestamp created, String forum,
                Boolean isEdited, String message, Integer parent,
                Integer thread_id) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread_id = thread_id;
    }

    public List<Integer> getM_path() {
        return m_path;
    }

    public Post(Integer id, String author,
                Timestamp created, String forum,
                Boolean isEdited, String message, Integer parent,
                Integer thread_id, List<Integer> m_path) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread_id = thread_id;
        this.m_path = new ArrayList<Integer>();
        this.m_path.addAll(m_path);
    }

    public String getAuthor() { return author; }
    public Timestamp getCreated() { return created; }
    public String getForum() { return forum; }
    public Integer getId() { return id; }
    public Boolean getEdited() { return isEdited; }
    public String getMessage() { return message; }
    public Integer getParent() { return parent; }
    public Integer getThreadId() { return thread_id; }


    public void setAuthor(String author) { this.author = author; }
    public void setCreated(Timestamp created) { this.created = created; }
    public void setForum(String forum) { this.forum = forum; }
    public void setId(Integer id) { this.id = id; }
    public void setEdited(Boolean edited) { isEdited = edited; }
    public void setMessage(String message) { this.message = message; }
    public void setParent(Integer parent) { this.parent = parent; }
    public void setThreadId(Integer thread_id) { this.thread_id = thread_id; }

    public void fill(Integer id, String author,
                     Timestamp created, String forum,
                     Boolean isEdited, String message, Integer parent,
                     Integer thread_id) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
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
