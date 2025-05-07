package com.cmloopy.quizzi.models;

public class UI_44_Word {
    private String text;
    private int originalPosition;

    public UI_44_Word(String text, int originalPosition) {
        this.text = text;
        this.originalPosition = originalPosition;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(int originalPosition) {
        this.originalPosition = originalPosition;
    }
}