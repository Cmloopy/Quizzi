package com.cmloopy.quizzi.models;

import java.util.List;

public class CollectionModel {
    private int id;
    private int authorId;
    private String description;
    private String category;
    private boolean visibleTo;
    private String timestamp;
    private String coverPhoto;
    private List<Object> quizzes;

    // Constructor mặc định
    public CollectionModel() {
    }

    // Constructor đầy đủ tham số
    public CollectionModel(int id, int authorId, String description, String category,
                           boolean visibleTo, String timestamp, String coverPhoto,
                           List<Object> quizzes) {
        this.id = id;
        this.authorId = authorId;
        this.description = description;
        this.category = category;
        this.visibleTo = visibleTo;
        this.timestamp = timestamp;
        this.coverPhoto = coverPhoto;
        this.quizzes = quizzes;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isVisibleTo() {
        return visibleTo;
    }

    public void setVisibleTo(boolean visibleTo) {
        this.visibleTo = visibleTo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public List<Object> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Object> quizzes) {
        this.quizzes = quizzes;
    }

    @Override
    public String toString() {
        return "CollectionModel{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", visibleTo=" + visibleTo +
                '}';
    }
}