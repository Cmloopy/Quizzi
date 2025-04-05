package com.cmloopy.quizzi.models.QuizCreate.before;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestion extends Question {
    private static final List<String> DEFAULT_OPTIONS = new ArrayList<>();
    private static final int DEFAULT_CORRECT_OPTION_INDEX = 0;

    private List<String> options;
    private int correctOptionIndex;

    public QuizQuestion() {
        super();
        this.options = new ArrayList<>(DEFAULT_OPTIONS);
        this.correctOptionIndex = DEFAULT_CORRECT_OPTION_INDEX;
    }

    public QuizQuestion(int position, String id, String title, String imageUrl, int time, int point,
                        List<String> options, int correctOptionIndex) {
        super(position, id, title, imageUrl, time, point);
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>(DEFAULT_OPTIONS);
        this.correctOptionIndex = correctOptionIndex;
    }

    @Override
    public int getType() {
        return TYPE_QUIZ;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() &&
                options.equals(DEFAULT_OPTIONS) &&
                correctOptionIndex == DEFAULT_CORRECT_OPTION_INDEX;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }
}
