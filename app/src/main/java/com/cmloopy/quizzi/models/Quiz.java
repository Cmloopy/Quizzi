package com.cmloopy.quizzi.models;

import com.cmloopy.quizzi.R;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private Long id;
    private int imageResource;
    private String title;
    private String date;
    private String plays;
    private String author;
    private int authorAvatarResource;
    private String description;
    private List<Question> questions;

    public Quiz(Long id, int imageResource, String title, String date, String plays, String author, int authorAvatarResource, String description) {
        this.id = id;
        this.imageResource = imageResource;
        this.title = title;
        this.date = date;
        this.plays = plays;
        this.author = author;
        this.authorAvatarResource = authorAvatarResource;
        this.description = description;
        this.questions = new ArrayList<>();
        questions.addAll(MultiChoiceQuestion.createSampleData());
        questions.addAll(TrueFalseQuestion.createSampleData());
//        questions.addAll(SingleChoiceQuestion.createSampleData());
    }


    @Override
    public String toString() {
        return "Quiz{" +
                "imageResource=" + imageResource +
                ", name='" + title + '\'' +
                ", date='" + date + '\'' +
                ", plays='" + plays + '\'' +
                ", author='" + author + '\'' +
                ", authorAvatarResource=" + authorAvatarResource +
                ", description='" + description + '\'' +
                ", questions=" + questions.size() +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlays() {
        return plays;
    }

    public void setPlays(String plays) {
        this.plays = plays;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorAvatarResource() {
        return authorAvatarResource;
    }

    public void setAuthorAvatarResource(int authorAvatarResource) {
        this.authorAvatarResource = authorAvatarResource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}