package io.github.ceruleanotter.captionator.models;

import java.util.HashMap;

/**
 * Created by lyla on 12/27/16.
 */

public class Caption {
    private String caption;
    private long date;
    private String author;
    private int upvotes;
    private int downvotes;
    private HashMap<String, Boolean> voters;

    public Caption() {
    }

    public Caption(String caption, String author) {
        this.caption = caption;
        this.date = System.currentTimeMillis();
        this.author = author;
        this.upvotes = 0;
        this.downvotes = 0;
        this.voters = new HashMap<String, Boolean>();
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

    public HashMap<String, Boolean> getVoters() {
        return voters;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public void setVoters(HashMap<String, Boolean> voters) {
        this.voters = voters;
    }
}
