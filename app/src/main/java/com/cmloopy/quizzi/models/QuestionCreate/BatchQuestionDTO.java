package com.cmloopy.quizzi.models.QuestionCreate;

import java.util.Map;

public class BatchQuestionDTO {
    private String questionType;
    private String content;
    private String description;
    private Integer timeLimit;
    private Integer point;
    private String imageFileName;
    private String audioFileName;
    private Map<String, Object> data;
    private Integer position;

    public BatchQuestionDTO() {
    }

    public BatchQuestionDTO(String questionType, String content, String description, Integer timeLimit, Integer point, String imageFileName, String audioFileName, Map<String, Object> data, Integer position) {
        this.questionType = questionType;
        this.content = content;
        this.description = description;
        this.timeLimit = timeLimit;
        this.point = point;
        this.imageFileName = imageFileName;
        this.audioFileName = audioFileName;
        this.data = data;
        this.position = position;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public Integer getPoint() {
        return point;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Integer getPosition() {
        return position;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}