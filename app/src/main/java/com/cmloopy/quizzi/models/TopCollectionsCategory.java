// File: com/cmloopy/quizzi/models/TopCollectionsCategory.java
package com.cmloopy.quizzi.models;

public class TopCollectionsCategory {
    private String name;
    private int imageResource;
    private int collectionId;

    public TopCollectionsCategory(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
        this.collectionId = -1;
    }

    public TopCollectionsCategory(String name, int imageResource, int collectionId) {
        this.name = name;
        this.imageResource = imageResource;
        this.collectionId = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

}