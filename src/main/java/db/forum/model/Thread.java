package db.forum.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class Thread {
    private String author;//nickname
    private Timestamp created;
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

    @JsonCreator
    public Thread(@JsonProperty("id") int id,
                  @JsonProperty("title") String title,
                  @JsonProperty("author") String author,
                  @JsonProperty("slug") String slug,
                  @JsonProperty("message") String message,
                  @JsonProperty("forum") String forum,
                  @JsonProperty("votes") int votes,
                  @JsonProperty("created") Timestamp created) {
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

    public void fill(Integer id, String author, Timestamp created,
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

    public Timestamp getCreated() { return created; }
    public String getForum() { return forum; }
    public String getMessage() { return message; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public Integer getVotes() { return votes; }
    public void setId(Integer id) { this.id = id; }

    public void setAuthor(String author) { this.author = author; }
    public void setCreated(Timestamp created) { this.created = created;}
    public void setForum(String forum) { this.forum = forum; }
    public void setMessage(String message) { this.message = message; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
    public void setVotes(Integer votes) { this.votes = votes; }

    public JSONObject getJson(Boolean has_slug) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("author", author);
        if(created != null) {
            jsonObject.put("created", created.toInstant().toString());
        }
        jsonObject.put("forum", forum);
        jsonObject.put("id", id);
        jsonObject.put("message", message);
        if(has_slug != null && has_slug == true) {
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
            arr.put(p.getJson(true));
        }
        return arr;
    }
}
