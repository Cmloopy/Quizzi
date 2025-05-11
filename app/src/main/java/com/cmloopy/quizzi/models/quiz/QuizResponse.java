package com.cmloopy.quizzi.models.quiz;

import java.util.List;

public class QuizResponse {
    private Long id;
    private int userId;
    private String title;
    private String description;
    private String keyword;
    private Integer score;
    private String coverPhoto;
    private String createdAt;
    private String updatedAt;
    private boolean visible;
    private boolean visibleQuizQuestion;
    private boolean shuffle;
    private List<Object> quizGames; // tùy vào kiểu dữ liệu thật, có thể tạo class riêng

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisibleQuizQuestion() {
        return visibleQuizQuestion;
    }

    public void setVisibleQuizQuestion(boolean visibleQuizQuestion) {
        this.visibleQuizQuestion = visibleQuizQuestion;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public List<Object> getQuizGames() {
        return quizGames;
    }

    public void setQuizGames(List<Object> quizGames) {
        this.quizGames = quizGames;
    }
}

