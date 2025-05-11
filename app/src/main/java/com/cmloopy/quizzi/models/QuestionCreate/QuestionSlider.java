package com.cmloopy.quizzi.models.QuestionCreate;

import java.io.Serializable;

public class QuestionSlider extends Question implements Serializable {
    private int minValue;
    private int maxValue;
    private int defaultValue;
    private int correctAnswer;
    private String color;

    public QuestionSlider() {
        super();
        this.minValue = 0;
        this.maxValue = 100;
        this.defaultValue = 50;
        this.correctAnswer = 50;
        this.color = "Default";
    }


    public QuestionSlider(int position, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, int minValue, int maxValue, int defaultValue, int correctAnswer, String color) {
        super(position, content, imageUrl, audioUrl, point, timeLimit, description);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.correctAnswer = correctAnswer;
        this.color = color;
    }

    public QuestionSlider(int position, Long id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, int minValue, int maxValue, int defaultValue, int correctAnswer, String color) {
        super(position, id, content, imageUrl, audioUrl, point, timeLimit, description);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.correctAnswer = correctAnswer;
        this.color = color;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
