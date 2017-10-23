package db.forum.DTO;

public class VoteDTO {
    private Integer vote_id;
    private Integer thread_id;
    private Integer user_id;
    private Integer vote_value;

    public VoteDTO() {}

    public VoteDTO(Integer vote_id, Integer thread_id, Integer user_id, Integer vote_value) {
        this.vote_id = vote_id;
        this.thread_id = thread_id;
        this.user_id = user_id;
        this.vote_value = vote_value;
    }

    public Integer getVote_id() { return vote_id; }
    public Integer getThread_id() { return thread_id; }
    public Integer getUser_id() { return user_id; }
    public Integer getVote_value() { return vote_value; }

    public void setVote_id(Integer vote_id) { this.vote_id = vote_id; }
    public void setThread_id(Integer thread_id) { this.thread_id = thread_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public void setVote_value(Integer vote_value) { this.vote_value = vote_value; }
}
