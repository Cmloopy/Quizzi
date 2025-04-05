package com.cmloopy.quizzi.models.QuizCreate.after;

import java.io.Serializable;

public class QuestionType implements Serializable {
    private String id;
    private String name;
    private int iconResource;

    public QuestionType() {
        this.id = "";
        this.name = "";
    }

    public QuestionType(String id, String name, int iconResource) {
        this.id = id;
        this.name = name;
        this.iconResource = iconResource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

