package com.cmloopy.quizzi.models;

public class Quiz {
    private int imageResource;
    private String title;
    private String date;
    private String plays;
    private String author;
    private int authorAvatarResource;
    private String questions;

    public Quiz(int imageResource, String title, String date, String plays,
                String author, int authorAvatarResource, String questions) {
        this.imageResource = imageResource;
        this.title = title;
        this.date = date;
        this.plays = plays;
        this.author = author;
        this.authorAvatarResource = authorAvatarResource;
        this.questions = questions;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPlays() {
        return plays;
    }

    public String getAuthor() {
        return author;
    }

    public int getAuthorAvatarResource() {
        return authorAvatarResource;
    }

    public String getQuestions() {
        return questions;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlays(String plays) {
        this.plays = plays;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorAvatarResource(int authorAvatarResource) {
        this.authorAvatarResource = authorAvatarResource;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "imageResource=" + imageResource +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", plays='" + plays + '\'' +
                ", author='" + author + '\'' +
                ", authorAvatarResource=" + authorAvatarResource +
                ", questions='" + questions + '\'' +
                '}';
    }
}
