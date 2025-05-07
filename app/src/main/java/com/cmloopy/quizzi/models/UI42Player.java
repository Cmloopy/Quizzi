package com.cmloopy.quizzi.models;

public class UI42Player {
    private String name;
    private int avatar;

    public UI42Player(String name, int avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public int getAvatar() {
        return avatar;
    }
}

