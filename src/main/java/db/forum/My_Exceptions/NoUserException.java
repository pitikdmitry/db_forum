package db.forum.My_Exceptions;

public class NoUserException extends Exception {
    private String author;
    public NoUserException(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }
}
