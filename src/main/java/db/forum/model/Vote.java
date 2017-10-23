package db.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Vote {
    @JsonIgnore
    private Integer vote_id;
    private String nickname;
    private Integer voice;

    public Vote() {}

    public Vote(Integer vote_id, String nickname, Integer voice) {
        this.vote_id = vote_id;
        this.nickname = nickname;
        this.voice = voice;
    }

    public Integer getVote_id() { return vote_id; }
    public String getNickname() { return nickname; }
    public Integer getVoice() { return voice; }

    public void setVote_id(Integer vote_id) { this.vote_id = vote_id; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setVoice(Integer voice) { this.voice = voice; }

    public void fill(Integer vote_id, String nickname, Integer voice) {
        this.vote_id = vote_id;
        this.nickname = nickname;
        this.voice = voice;
    }
}
