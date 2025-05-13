package com.cmloopy.quizzi.models.QuestionCreate;

import java.io.Serializable;

public class QuestionType implements Serializable {
    private Long id;
    private String name;
    private int iconResource;

    public QuestionType() {
        this.name = "";
    }

    public QuestionType(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
    }

    public QuestionType(Long id, String name, int iconResource) {
        this.id = id;
        this.name = name;
        this.iconResource = iconResource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }
}

