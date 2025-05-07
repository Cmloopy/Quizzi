package com.cmloopy.quizzi.models;

import com.cmloopy.quizzi.R;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private int imageResource;
    private String title;
    private String date;
    private String plays;
    private String author;
    private int authorAvatarResource;
    private String description;
    private List<Question> questions;

    public Quiz(int imageResource, String title, String date, String plays, String author, int authorAvatarResource, String description) {
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

    public static List<Quiz> CreateSampleData() {
        List<Quiz> items = new ArrayList<>();

        items.add(new Quiz(R.drawable.ic_launcher_background, "Get Smarter with Prod...",
                "2 months ago", "5.5K plays", "Titus Kitamura", R.drawable.ic_launcher_background,
                "A quiz designed to boost your productivity and efficiency."));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Great Ideas Come from...",
                "6 months ago", "10.3K plays", "Alfonzo Schuessler", R.drawable.ic_launcher_background,
                "Explore the origins of great ideas and how creativity shapes innovation."));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Having Fun & Always S...",
                "2 years ago", "18.5K plays", "Daryl Nehls", R.drawable.ic_launcher_background,
                "A fun and engaging quiz that keeps you entertained while learning."));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Can You Imagine, Worl...",
                "3 months ago", "4.9K plays", "Edgar Torrey", R.drawable.ic_launcher_background,
                "Test your imagination and creativity with thought-provoking questions."));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Back to School, Get Sm...",
                "1 year ago", "12.4K plays", "Darcel Ballentine", R.drawable.ic_launcher_background,
                "A back-to-school quiz to refresh your knowledge and challenge your mind."));

        items.add(new Quiz(R.drawable.ic_launcher_background, "What is Your Favorite ...",
                "5 months ago", "6.2K plays", "Elmer Laverty", R.drawable.ic_launcher_background,
                "Discover what your preferences say about you with this fun quiz."));

        return items;
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
