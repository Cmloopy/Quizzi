// File: com/cmloopy/quizzi/models/DetailTopCollectionItem.java
package com.cmloopy.quizzi.models;

public class DetailTopCollectionItem {
    private int imageResId;
    private String title;
    private String author;
    private String timeAgo;
    private String playCount;
    private int quizId = -1;

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

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
}