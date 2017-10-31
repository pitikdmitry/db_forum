package db.forum.My_Exceptions;

public class NoThreadException extends Exception {
    private String slug_or_id;
    public NoThreadException(String slug_or_id) {
        this.slug_or_id = slug_or_id;
    }

    public String getSlugOrId() {
        return slug_or_id;
    }
}
