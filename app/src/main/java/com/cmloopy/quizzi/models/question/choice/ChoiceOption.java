package com.cmloopy.quizzi.models.question.choice;

public class ChoiceOption {
    public int id;
    public String text;
    public String image;
    public String audio;
    public String createdAt;
    public String updatedAt;
    public boolean isCorrect;
    public boolean isSelected = false;
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public ChoiceOption(int id, String text, String image, String audio, String createdAt, String updatedAt, boolean isCorrect) {
        this.id = id;
        this.text = text;
        this.image = image;
        this.audio = audio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isCorrect = isCorrect;
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

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
