package com.cmloopy.quizzi.models.QuizCreate.before;

import java.util.ArrayList;
import java.util.List;

public class QuizAudioQuestion extends Question {
    private static final String DEFAULT_AUDIO_URL = "";
    private static final List<String> DEFAULT_OPTIONS = new ArrayList<>();
    private static final int DEFAULT_CORRECT_OPTION_INDEX = 0;

    private String audioUrl;
    private List<String> options;
    private int correctOptionIndex;

    public QuizAudioQuestion() {
        super();
        this.audioUrl = DEFAULT_AUDIO_URL;
        this.options = new ArrayList<>(DEFAULT_OPTIONS);
        this.correctOptionIndex = DEFAULT_CORRECT_OPTION_INDEX;
    }

    public QuizAudioQuestion(int position, String id, String title, String imageUrl, int time, int point,
                             String audioUrl, List<String> options, int correctOptionIndex) {
        super(position, id, title, imageUrl, time, point);
        this.audioUrl = audioUrl != null ? audioUrl : DEFAULT_AUDIO_URL;
        this.options = options != null ? options : new ArrayList<>(DEFAULT_OPTIONS);
        this.correctOptionIndex = correctOptionIndex;
    }

    @Override
    public int getType() {
        return TYPE_QUIZ_AUDIO;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() &&
                DEFAULT_AUDIO_URL.equals(audioUrl) &&
                options.equals(DEFAULT_OPTIONS) &&
                correctOptionIndex == DEFAULT_CORRECT_OPTION_INDEX;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
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
