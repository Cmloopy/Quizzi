package com.cmloopy.quizzi.models.QuizCreate.before;

import java.util.ArrayList;
import java.util.List;

public class CheckboxQuestion extends Question {
    private static final List<String> DEFAULT_OPTIONS = new ArrayList<>();
    private static final List<Boolean> DEFAULT_CORRECT_ANSWERS = new ArrayList<>();

    private List<String> options;
    private List<Boolean> correctAnswers;

    public CheckboxQuestion() {
        super();
        this.options = new ArrayList<>(DEFAULT_OPTIONS);
        this.correctAnswers = new ArrayList<>(DEFAULT_CORRECT_ANSWERS);
    }

    public CheckboxQuestion(int position, String id, String title, String imageUrl, int time, int point, List<String> options, List<Boolean> correctAnswers) {
        super(position, id, title, imageUrl, time, point);
        this.options = options != null ? options : new ArrayList<>(DEFAULT_OPTIONS);
        this.correctAnswers = correctAnswers != null ? correctAnswers : new ArrayList<>(DEFAULT_CORRECT_ANSWERS);
    }

    @Override
    public int getType() {
        return TYPE_CHECKBOX;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() &&
                options.equals(DEFAULT_OPTIONS) &&
                correctAnswers.equals(DEFAULT_CORRECT_ANSWERS);
    }

    // Getters and setters
    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<Boolean> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<Boolean> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
