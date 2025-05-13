package com.cmloopy.quizzi.models.question.puzzle;

public class PuzzlePiece {
    public int id;
    public String text;
    public String image;
    public String audio;
    public String createdAt;
    public String updatedAt;
    public int correctPosition;

    public PuzzlePiece(int id, String text, String image, String audio, String createdAt, String updatedAt, int correctPosition) {
        this.id = id;
        this.text = text;
        this.image = image;
        this.audio = audio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.correctPosition = correctPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getCorrectPosition() {
        return correctPosition;
    }

    public void setCorrectPosition(int correctPosition) {
        this.correctPosition = correctPosition;
    }
}
