package com.cmloopy.quizzi.models;

import java.io.Serializable;
import java.util.List;

public class Question<T> implements Serializable {
    private String type; // "multiple_choice", "true_false", "fill_in_blank", "matching"
    private String question;
    private int points;
    private int time_limit;
    private T correctAnswer;

    public Question( String question, int points, int time_limit, T correctAnswer) {
        this.question = question;
        this.points = points;
        this.time_limit = time_limit;
        this.correctAnswer = correctAnswer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTime_limit() {
        return time_limit;
    }

    public void setTime_limit(int time_limit) {
        this.time_limit = time_limit;
    }

    public T getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(T correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}