package db.forum.My_Exceptions;

public class NoPostException extends Exception {
    private Integer post_id;

    public NoPostException(Integer post_id) {
        this.post_id = post_id;
    }

    public Integer getPostId() {
        return post_id;
    }

}
