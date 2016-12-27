package io.github.ceruleanotter.captionator.models;

/**
 * Created by lyla on 12/26/16.
 */

public class CaptionatorImage {
    private String url;
    private String author;
    private long date;


    public CaptionatorImage() {
    }

    public CaptionatorImage(String url, String author, long date) {
        this.url = url;
        this.author = author;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public long getDate() {
        return date;
    }
}
