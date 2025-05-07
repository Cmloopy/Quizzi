package com.cmloopy.quizzi.models.QuizCreate.after;

import java.io.Serializable;

public class QuestionSlider extends Question implements Serializable {
    private int minValue;
    private int maxValue;
    private int defaultValue;
    private int correctAnswer;
    private String lambda;

    public QuestionSlider() {
        super();
        this.minValue = 0;
        this.maxValue = 100;
        this.defaultValue = 50;
        this.correctAnswer = 50;
        this.lambda = "Default";
    }

    public QuestionSlider(int position, String id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, int minValue, int maxValue, int defaultValue, int correctAnswer, String lambda) {
        super(position, id, content, imageUrl, audioUrl, point, timeLimit, description);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.correctAnswer = correctAnswer;
        this.lambda = lambda;
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

    public String getLambda() {
        return lambda;
    }

    public void setLambda(String lambda) {
        this.lambda = lambda;
    }
}
