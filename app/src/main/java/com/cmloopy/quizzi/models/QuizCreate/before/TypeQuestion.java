package com.cmloopy.quizzi.models.QuizCreate.before;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TypeQuestion extends Question implements Serializable {
    private static final List<String> DEFAULT_CORRECT_ANSWER = new ArrayList<>();

    private List<String> correctAnswer;

    public TypeQuestion() {
        super(); 
        this.correctAnswer = new ArrayList<>(DEFAULT_CORRECT_ANSWER);
    }

    public TypeQuestion(int position, String id, String title, String imageUrl, int time, int point,
                        List<String> correctAnswer) {
        super(position, id, title, imageUrl, time, point);
        this.correctAnswer = correctAnswer != null ? new ArrayList<>(correctAnswer) : new ArrayList<>(DEFAULT_CORRECT_ANSWER);
    }

    @Override
    public int getType() {
        return TYPE_TEXT;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() && 
                correctAnswer.equals(DEFAULT_CORRECT_ANSWER);
    }

    public List<String> getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(List<String> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
