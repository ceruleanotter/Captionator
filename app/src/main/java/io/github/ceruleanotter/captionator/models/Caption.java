package io.github.ceruleanotter.captionator.models;

import java.util.HashMap;

/**
 * Created by lyla on 12/27/16.
 */

public class Caption {
    private String caption;
    private long date;
    private String author;
    private String authorUID;
    private int votes;
    private HashMap<String, Boolean> voters;

    public Caption() {
    }

    public Caption(String caption, String author, String authorUID) {
        this.caption = caption;
        this.date = System.currentTimeMillis();
        this.author = author;
        this.votes = 0;
        this.voters = new HashMap<String, Boolean>();
        this.authorUID = authorUID;
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

    public int getVotes() {
        return votes;
    }

    public HashMap<String, Boolean> getVoters() {
        return voters;
    }

    public String getAuthorUID() { return authorUID; }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void setVoters(HashMap<String, Boolean> voters) {
        this.voters = voters;
    }
}
