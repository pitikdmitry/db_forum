package db.forum.model;

public class Forum {
    private Integer forum_id;
    private Integer responsible_user_id;
    private String slug;

    public Forum() {}

    public Forum(Integer forum_id, Integer responsible_user_id, String slug, String title) {

        this.forum_id = forum_id;
        this.responsible_user_id = responsible_user_id;
        this.slug = slug;
        this.title = title;
    }

    private String title;

    public Integer getForum_id() { return forum_id; }
    public Integer getResponsible_user_id() { return responsible_user_id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }

    public void setForum_id(Integer forum_id) { this.forum_id = forum_id; }
    public void setResponsible_user_id(Integer responsible_user_id) { this.responsible_user_id = responsible_user_id; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
}
