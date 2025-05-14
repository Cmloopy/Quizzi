package com.cmloopy.quizzi.models.question.text;

import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionType;

import java.util.List;

public class TextQuestion extends Question {
    public List<AcceptAnswer> acceptedAnswers;
    public boolean caseSensitive;

    public TextQuestion(List<AcceptAnswer> acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers;
    }

    public TextQuestion(int id, int quizId, QuestionType questionType, String image, String audio, String content, int point, int timeLimit, String description, String createdAt, String updatedAt, List<AcceptAnswer> acceptedAnswers) {
        super(id, quizId, questionType, image, audio, content, point, timeLimit, description, createdAt, updatedAt);
        this.acceptedAnswers = acceptedAnswers;
    }

    public List<AcceptAnswer> getAcceptedAnswers() {
        return acceptedAnswers;
    }

    public void setAcceptedAnswers(List<AcceptAnswer> acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}
