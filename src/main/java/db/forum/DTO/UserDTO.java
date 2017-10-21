package db.forum.DTO;

public class UserDTO {
    private Integer user_id;

    private String about;
    private String email;
    private String fullname;
    private String nickname;

    public UserDTO() {}

    public UserDTO(Integer user_id, String nickname, String email, String about, String fullname) {
        this.user_id = user_id;
        this.about = about;
        this.email = email;
        this.fullname = fullname;
        this.nickname = nickname;
    }

    public UserDTO(String nickname, String email, String about, String fullname) {
        this.nickname = nickname;
        this.email = email;
        this.about = about;
        this.fullname = fullname;
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
