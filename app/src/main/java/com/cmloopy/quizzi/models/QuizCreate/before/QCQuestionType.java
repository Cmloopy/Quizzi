package com.cmloopy.quizzi.models.QuizCreate.before;

import androidx.annotation.DrawableRes;

public class QCQuestionType {
    private final String name;
    private final int iconResourceId;
    private final int id;

    public QCQuestionType(String name, @DrawableRes int iconResourceId, int id) {
        this.name = name;
        this.iconResourceId = iconResourceId;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public int getId() {
        return id;
    }
}