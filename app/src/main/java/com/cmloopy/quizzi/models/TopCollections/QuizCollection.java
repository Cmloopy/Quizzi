package com.cmloopy.quizzi.models.TopCollections;

import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuizCollection {
    @SerializedName("id")
    private int id;

    @SerializedName("authorId")
    private int authorId;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("visibleTo")
    private boolean visibleTo;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("coverPhoto")
    private String coverPhoto;

    @SerializedName("quizzes")
    private List<QuizResponse> quizzes;

    // Getters and setters
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

    public List<QuizResponse> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizResponse> quizzes) {
        this.quizzes = quizzes;
    }
}