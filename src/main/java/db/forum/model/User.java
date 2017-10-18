package db.forum.model;

public class User {
    private Integer user_id;
    private String nickname;
    private String email;
    private String about;
    private String fullname;

    public User() {}

    public User(Integer user_id, String nickname, String email, String about, String fullname) {
        this.user_id = user_id;
        this.about = about;
        this.email = email;
        this.fullname = fullname;
        this.nickname = nickname;
    }

    public Integer getUser_id() { return user_id; }
    public String getAbout() { return about; }
    public String getEmail() { return email; }
    public String getFullname() { return fullname; }
    public String getNickname() { return nickname; }

    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public void setAbout(String about) { this.about = about; }
    public void setEmail(String email) { this.email = email; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
