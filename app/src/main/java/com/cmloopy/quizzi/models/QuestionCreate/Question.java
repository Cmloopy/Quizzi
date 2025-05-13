package com.cmloopy.quizzi.models.QuestionCreate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.http.Multipart;

public class Question implements Serializable {
    private Long id;
    private int position;
    private int quizId;
    private QuestionType questionType;
    private String image;
    private String audio;

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
        mp.put("SLIDER", TYPE_SLIDER);
        mp.put("SINGLE_CHOICE", TYPE_QUIZ);
        mp.put("MULTI_CHOICE", TYPE_CHECKBOX);
        mp.put("PUZZLE", TYPE_PUZZLE);
        mp.put("TEXT", TYPE_TEXT);
        mp.put("AUDIO_SINGLE_CHOICE", TYPE_QUIZ_AUDIO);
        mp.put("TRUE_FALSE", TYPE_TRUE_FALSE);
        mp.put("SAY_WORD", TYPE_SAY_WORD);

        return mp.getOrDefault(questionType.getName(), -1);
    }


    // Constructors
    public Question() {
        this.content = "";
        this.image = "";
        this.audio = "";
        this.point = 200;
        this.timeLimit = 10;
        this.description = "";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Question(
            int position,
            Long id,
//                    Quiz quiz,
            QuestionType questionType,
            String content,
            String image, String audio, int point, int timeLimit,
            String description) {
        this.position = position;
        this.id = id;
//        this.quiz = quiz;
        this.questionType = questionType;
        this.content = content;
        this.image = image;
        this.audio = audio;
        this.point = point;
        this.timeLimit = timeLimit;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Question(
            int position,
//                    Quiz quiz,
            String content,
            String image, String audio, int point, int timeLimit,
            String description) {
        this.position = position;
//        this.quiz = quiz;
        this.content = content;
        this.image = image;
        this.audio = audio;
        this.point = point;
        this.timeLimit = timeLimit;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Question(
            int position,
//                    Quiz quiz,
            QuestionType questionType,
            String content,
            String image, String audio, int point, int timeLimit,
            String description) {
        this.position = position;
//        this.quiz = quiz;
        this.questionType = questionType;
        this.content = content;
        this.image = image;
        this.audio = audio;
        this.point = point;
        this.timeLimit = timeLimit;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }


    public Question(
            int position,
            Long id,
//                    Quiz quiz,
//            QuestionType questionType,
            String content,
            String image, String audio, int point, int timeLimit,
            String description) {
        this.position = position;
        this.id = id;
//        this.quiz = quiz;
//        this.questionType = questionType;
        this.content = content;
        this.image = image;
        this.audio = audio;
        this.point = point;
        this.timeLimit = timeLimit;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
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
        this.image = question.getImage();
        this.audio = question.getAudio();
        this.point = question.getPoint();
        this.timeLimit = question.getTimeLimit();
        this.description = question.getDescription();
        this.createdAt = question.getCreatedAt();
        this.updatedAt = new Date();
    }

}
