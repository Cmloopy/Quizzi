package com.cmloopy.quizzi.models.QuizCreate.after;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Question implements Serializable {
    private String id;
    private int position;
//    private Quiz quiz;
    private QuestionType questionType;
    private String imageUrl;
    private String audioUrl;

    private String imageUri;
    private String audioUri;


    private String content;
    private int point;
    private int timeLimit;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public static final int TYPE_SLIDER = 1;
    public static final int TYPE_QUIZ = 2;
    public static final int TYPE_CHECKBOX = 3;
    public static final int TYPE_PUZZLE = 4;
    public static final int TYPE_TEXT = 5;
    public static final int TYPE_QUIZ_AUDIO = 6;
    public static final int TYPE_TRUE_FALSE = 7;
    public static final int TYPE_SAY_WORD = 8;

    public int getType() {
        Map<String, Integer> mp = new HashMap<>();
        mp.put("Slider", TYPE_SLIDER);
        mp.put("Quiz", TYPE_QUIZ);
        mp.put("Checkbox", TYPE_CHECKBOX);
        mp.put("Puzzle", TYPE_PUZZLE);
        mp.put("Text", TYPE_TEXT);
        mp.put("Quiz Audio", TYPE_QUIZ_AUDIO);
        mp.put("True/False", TYPE_TRUE_FALSE);
        mp.put("Say Word", TYPE_SAY_WORD);

        return mp.getOrDefault(questionType.getName(), -1);
    }


    // Constructors
    public Question() {
        this.id = "";
        this.content = "";
        this.imageUrl = "";
        this.audioUrl = "";
        this.point = 200;
        this.timeLimit = 10;
        this.description = "";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Question(
            int position,
            String id,
//                    Quiz quiz,
                    QuestionType questionType,
                    String content,
                    String imageUrl, String audioUrl, int point, int timeLimit,
                    String description) {
        this.position = position;
        this.id = id;
//        this.quiz = quiz;
        this.questionType = questionType;
        this.content = content;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.point = point;
        this.timeLimit = timeLimit;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }


    public Question(
            int position,
            String id,
//                    Quiz quiz,
//            QuestionType questionType,
            String content,
            String imageUrl, String audioUrl, int point, int timeLimit,
            String description) {
        this.position = position;
        this.id = id;
//        this.quiz = quiz;
//        this.questionType = questionType;
        this.content = content;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.point = point;
        this.timeLimit = timeLimit;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

//    public Quiz getQuiz() {
//        return quiz;
//    }
//
//    public void setQuiz(Quiz quiz) {
//        this.quiz = quiz;
//    }
//
//    public QuestionType getQuestionType() {
//        return questionType;
//    }
//
    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        this.id = question.getId();
        this.position = question.getPosition();
        this.questionType = question.getQuestionType();
        this.content = question.getContent();
        this.imageUrl = question.getImageUrl();
        this.audioUrl = question.getAudioUrl();
        this.point = question.getPoint();
        this.timeLimit = question.getTimeLimit();
        this.description = question.getDescription();
        this.createdAt = question.getCreatedAt();
        this.updatedAt = new Date();
    }

}
