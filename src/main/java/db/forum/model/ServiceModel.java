package db.forum.model;

import org.json.JSONObject;

public class ServiceModel {
    private Integer forum;
    private Integer post;
    private Integer thread;
    private Integer user;

    public ServiceModel() {}

    public ServiceModel(Integer forum, Integer post, Integer thread, Integer user) {
        this.forum = forum;
        this.post = post;
        this.thread = thread;
        this.user = user;
    }

    public Integer getForum() { return forum; }
    public Integer getPost() { return post; }
    public Integer getThread() { return thread; }
    public Integer getUser() { return user; }

    public void setForum(Integer forum) { this.forum = forum; }
    public void setPost(Integer post) { this.post = post; }
    public void setThread(Integer thread) { this.thread = thread; }
    public void setUser(Integer user) { this.user = user; }

    public JSONObject getJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("forum", forum);
        jsonObject.put("post", post);
        jsonObject.put("thread", thread);
        jsonObject.put("user", user);
        return jsonObject;
    }
}
