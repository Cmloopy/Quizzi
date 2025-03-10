package com.cmloopy.quizzi.models;

public class DetailTopCollectionItem { private int imageResId;
    private String title;
    private String author;
    private String timeAgo;
    private String playCount;

    public DetailTopCollectionItem(int imageResId, String title, String author, String timeAgo, String playCount) {
        this.imageResId = imageResId;
        this.title = title;
        this.author = author;
        this.timeAgo = timeAgo;
        this.playCount = playCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getPlayCount() {
        return playCount;
    }
}