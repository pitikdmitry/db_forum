package db.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class User {
    @JsonIgnore
    private Integer user_id;

    private String about;
    private String email;
    private String fullname;
    private String nickname;

    public User() {}

    public User(Integer user_id, String about, String email, String fullname, String nickname) {
        this.user_id = user_id;
        this.about = about;
        this.email = email;
        this.fullname = fullname;
        this.nickname = nickname;
    }

    public void fill(Integer user_id, String about, String email, String fullname, String nickname) {
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

    public JSONObject getJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("about", about);
        jsonObject.put("email", email);
        jsonObject.put("fullname", fullname);
        jsonObject.put("nickname", nickname);
        return jsonObject;
    }

    public static JSONArray getJsonArray(List<User> users) {
        final JSONArray arr = new JSONArray();
        for (User p : users) {
            arr.put(p.getJson());
        }
        return arr;
    }
}
