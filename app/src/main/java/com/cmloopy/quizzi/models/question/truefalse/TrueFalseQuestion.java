package com.cmloopy.quizzi.models.question.truefalse;

import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionType;

public class TrueFalseQuestion extends Question {
    public boolean correctAnswer;

    public TrueFalseQuestion(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public TrueFalseQuestion(int id, int quizId, QuestionType questionType, String image, String audio, String content, int point, int timeLimit, String description, String createdAt, String updatedAt, boolean correctAnswer) {
        super(id, quizId, questionType, image, audio, content, point, timeLimit, description, createdAt, updatedAt);
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}

