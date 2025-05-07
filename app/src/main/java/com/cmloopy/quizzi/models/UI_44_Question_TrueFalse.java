package com.cmloopy.quizzi.models;

public class UI_44_Question_TrueFalse {
    private String questionText;
    private int imageResource;
    private boolean correctAnswerTrue;

    public UI_44_Question_TrueFalse(String questionText, int imageResource, boolean correctAnswerTrue) {
        this.questionText = questionText;
        this.imageResource = imageResource;
        this.correctAnswerTrue = correctAnswerTrue;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public boolean isCorrectAnswerTrue() {
        return correctAnswerTrue;
    }

    public void setCorrectAnswerTrue(boolean correctAnswerTrue) {
        this.correctAnswerTrue = correctAnswerTrue;
    }
}