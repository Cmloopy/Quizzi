package com.cmloopy.quizzi.models.question;

public abstract class Question {
    public int id;
    public int quizId;
    public QuestionType questionType;
    public String image;
    public String audio;
    public String content;
    public int point;
    public int timeLimit;
    public String description;
    public String createdAt;
    public String updatedAt;

    public Question() {
    }

    public Question(int id, int quizId, QuestionType questionType, String image, String audio, String content, int point, int timeLimit, String description, String createdAt, String updatedAt) {
        this.id = id;
        this.quizId = quizId;
        this.questionType = questionType;
        this.image = image;
        this.audio = audio;
        this.content = content;
        this.point = point;
        this.timeLimit = timeLimit;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}

