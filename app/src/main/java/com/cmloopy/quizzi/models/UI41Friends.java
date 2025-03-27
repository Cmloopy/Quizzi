package com.cmloopy.quizzi.models;

public class UI41Friends {
    private String name;
    private int avatar;
    private boolean isSelected;

    public UI41Friends(String name, int avatar) {
        this.name = name;
        this.avatar = avatar;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public int getAvatar() {
        return avatar;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
