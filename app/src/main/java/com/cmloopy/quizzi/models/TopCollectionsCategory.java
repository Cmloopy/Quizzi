package com.cmloopy.quizzi.models;

public class TopCollectionsCategory {
    private String name;
    private int imageResId;

    public TopCollectionsCategory(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
