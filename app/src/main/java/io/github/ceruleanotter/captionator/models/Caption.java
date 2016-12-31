package io.github.ceruleanotter.captionator.models;

/**
 * Created by lyla on 12/27/16.
 */

public class Caption {
    private String caption;
    private long date;
    private String author;
    private int upvotes;
    private int downvotes;

    public Caption() {
    }

    public Caption(String caption, String author) {
        this.caption = caption;
        this.date = System.currentTimeMillis();
        this.author = author;
        this.upvotes = 0;
        this.downvotes = 0;
    }

    public String getCaption() {
        return caption;
    }

    public long getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }
}
