package com.cmloopy.quizzi.adapter.QuizCreate;

public class QCMenuItem {
    private String title;
    private int icon;
    private boolean isDestructive;

    public QCMenuItem(String title, int icon, boolean isDestructive) {
        this.title = title;
        this.icon = icon;
        this.isDestructive = isDestructive;
    }

    public String getTitle() { return title; }
    public int getIcon() { return icon; }
    public boolean isDestructive() { return isDestructive; }
}
