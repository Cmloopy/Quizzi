package com.cmloopy.quizzi.models;

public class UI_44_Player {
    private int rank;
    private String name;
    private int score;
    private int avatarResource;

    public UI_44_Player(int rank, String name, int score, int avatarResource) {
        this.rank = rank;
        this.name = name;
        this.score = score;
        this.avatarResource = avatarResource;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAvatarResource() {
        return avatarResource;
    }

    public void setAvatarResource(int avatarResource) {
        this.avatarResource = avatarResource;
    }
}