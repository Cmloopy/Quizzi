package com.cmloopy.quizzi.models.QuestionCreate;

import java.io.Serializable;

public class QuestionTrueFalse extends Question implements Serializable {
    private boolean correctAnswer;

    public QuestionTrueFalse() {
        super();
        this.correctAnswer = true;
    }

    public QuestionTrueFalse(int position, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, boolean correctAnswer) {
        super(position, content, imageUrl, audioUrl, point, timeLimit, description);
        this.correctAnswer = correctAnswer;
    }

    public QuestionTrueFalse(int position, Long id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, boolean correctAnswer) {
        super(position, id, content, imageUrl, audioUrl, point, timeLimit, description);
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}