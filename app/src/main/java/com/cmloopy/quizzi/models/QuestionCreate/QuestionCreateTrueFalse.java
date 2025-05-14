package com.cmloopy.quizzi.models.QuestionCreate;

import java.io.Serializable;

public class QuestionCreateTrueFalse extends QuestionCreate implements Serializable {
    private boolean correctAnswer;

    public QuestionCreateTrueFalse() {
        super();
        this.correctAnswer = true;
    }

    public QuestionCreateTrueFalse(int position, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, boolean correctAnswer) {
        super(position, content, imageUrl, audioUrl, point, timeLimit, description);
        this.correctAnswer = correctAnswer;
    }

    public QuestionCreateTrueFalse(int position, Long id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, boolean correctAnswer) {
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