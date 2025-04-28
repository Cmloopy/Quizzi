package com.cmloopy.quizzi.models;

public class UI_44_Question {
    private String questionText;
    private int imageResource;
    private String correctAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;

    public UI_44_Question(String questionText, int imageResource, String correctAnswer,
                          String wrongAnswer1, String wrongAnswer2, String wrongAnswer3) {
        this.questionText = questionText;
        this.imageResource = imageResource;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
    }

    // Getters
    public String getQuestionText() {
        return questionText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    // Setters
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }
}