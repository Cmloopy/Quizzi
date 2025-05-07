package com.cmloopy.quizzi.models;

public class QuizzDetailsQuestion {

    private String questionNumber;
    private String questionText;
    private int questionImageResId;

    public QuizzDetailsQuestion(String questionNumber, String questionText, int questionImageResId) {
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.questionImageResId = questionImageResId;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public int getQuestionImageResId() {
        return questionImageResId;
    }
}
