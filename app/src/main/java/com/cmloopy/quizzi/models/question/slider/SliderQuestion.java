package com.cmloopy.quizzi.models.question.slider;

import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionType;

public class SliderQuestion extends Question {
    public int minValue;
    public int maxValue;
    public int defaultValue;
    public int correctAnswer;
    public int lambda;

    public SliderQuestion(int minValue) {
        this.minValue = minValue;
    }

    public SliderQuestion(int id, int quizId, QuestionType questionType, String image, String audio, String content, int point, int timeLimit, String description, String createdAt, String updatedAt, int minValue) {
        super(id, quizId, questionType, image, audio, content, point, timeLimit, description, createdAt, updatedAt);
        this.minValue = minValue;
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

    public int getLambda() {
        return lambda;
    }

    public void setLambda(int lambda) {
        this.lambda = lambda;
    }
}

