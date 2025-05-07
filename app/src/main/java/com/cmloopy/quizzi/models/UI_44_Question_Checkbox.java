package com.cmloopy.quizzi.models;

import java.util.List;

public class UI_44_Question_Checkbox {
    private String questionText;
    private int imageResource;
    private List<String> options;

    public UI_44_Question_Checkbox(String questionText, int imageResource, List<String> options) {
        this.questionText = questionText;
        this.imageResource = imageResource;
        this.options = options;
    }

    public String getQuestionText() {
        return questionText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public List<String> getOptions() {
        return options;
    }
}