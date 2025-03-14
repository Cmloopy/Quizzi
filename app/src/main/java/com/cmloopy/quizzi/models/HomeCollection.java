package com.cmloopy.quizzi.models;

public class HomeCollection {
    private int imageCollection;
    private String nameCollection;

    public HomeCollection(int img_resource, String name_collection) {
        this.imageCollection = img_resource;
        this.nameCollection = name_collection;
    }

    public int getImageCollection() {
        return imageCollection;
    }

    public void setImageCollection(int imageCollection) {
        this.imageCollection = imageCollection;
    }

    public String getNameCollection() {
        return nameCollection;
    }

    public void setNameCollection(String nameCollection) {
        this.nameCollection = nameCollection;
    }
}
