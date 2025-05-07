package com.cmloopy.quizzi.models;

public class UI_44_Question_Slider {
    private String questionText;
    private int imageResource;
    private int correctAnswer;
    private int maxSliderValue;

    public UI_44_Question_Slider(String questionText, int imageResource, int correctAnswer, int maxSliderValue) {
        this.questionText = questionText;
        this.imageResource = imageResource;
        this.correctAnswer = correctAnswer;
        this.maxSliderValue = maxSliderValue;
    }

    public String getQuestionText() {
        return questionText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public int getMaxSliderValue() {
        return maxSliderValue;
    }
}