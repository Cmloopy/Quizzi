package com.cmloopy.quizzi.models.QuestionCreate;

import java.io.Serializable;

public class QuestionCreateSayWord extends QuestionCreate implements Serializable {
    private String correctAnswer;
    private String word;
    private String pronunciation;

    public QuestionCreateSayWord() {
        super();
        this.correctAnswer = "";
        this.word = "";
        this.pronunciation = "";
    }

    public QuestionCreateSayWord(int position, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, String correctAnswer, String word, String pronunciation) {
        super(position, content, imageUrl, audioUrl, point, timeLimit, description);
        this.correctAnswer = correctAnswer;
        this.word = word;
        this.pronunciation = pronunciation;
    }


    public QuestionCreateSayWord(int position, Long id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, String correctAnswer, String word, String pronunciation) {
        super(position, id, content, imageUrl, audioUrl, point, timeLimit, description);
        this.correctAnswer = correctAnswer;
        this.word = word;
        this.pronunciation = pronunciation;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
}
