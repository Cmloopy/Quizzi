package com.cmloopy.quizzi.models;

public class RecommendUser {
    private String name;
    private String username;
    private int profileImageResource;
    private boolean isFollowing;

    public RecommendUser(String name, int profileImageResource) {
        this.name = name;
        this.username = "";
        this.profileImageResource = profileImageResource;
        this.isFollowing = false;
    }

    public RecommendUser(String name, String username, int profileImageResource) {
        this.name = name;
        this.username = username;
        this.profileImageResource = profileImageResource;
        this.isFollowing = Math.random()<0.5;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public int getProfileImageResource() {
        return profileImageResource;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}