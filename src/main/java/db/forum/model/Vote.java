package db.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Vote {
    @JsonIgnore
    private Integer vote_id;
    private String nickname;
    private Integer voice;
    private Integer thread_id;
    private Integer user_id;

    public Vote() {}

    public Vote(Integer vote_id, String nickname, Integer voice, Integer thread_id, Integer user_id) {
        this.vote_id = vote_id;
        this.nickname = nickname;
        this.voice = voice;
        this.thread_id = thread_id;
        this.user_id = user_id;
    }

    public Integer getVote_id() { return vote_id; }
    public String getNickname() { return nickname; }
    public Integer getVoice() { return voice; }
    public Integer getThread_id() { return thread_id; }
    public Integer getUser_id() { return user_id; }

    public void setVote_id(Integer vote_id) { this.vote_id = vote_id; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setVoice(Integer voice) { this.voice = voice; }
    public void setThread_id(Integer thread_id) { this.thread_id = thread_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public void fill(Integer vote_id, String nickname, Integer voice) {
        this.vote_id = vote_id;
        this.nickname = nickname;
        this.voice = voice;
    }
}
