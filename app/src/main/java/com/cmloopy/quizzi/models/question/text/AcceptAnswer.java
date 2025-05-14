package com.cmloopy.quizzi.models.question.text;

public class AcceptAnswer {
    public int id;
    public String text;
    public String image;
    public String audio;
    public String createdAt;
    public String updatedAt;

    public AcceptAnswer(int id, String text, String image, String audio, String createdAt, String updatedAt) {
        this.id = id;
        this.text = text;
        this.image = image;
        this.audio = audio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
}
