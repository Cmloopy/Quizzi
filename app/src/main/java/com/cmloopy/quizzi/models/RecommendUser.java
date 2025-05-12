package com.cmloopy.quizzi.models;

public class RecommendUser {
    private int id; // Thêm id để có thể liên kết với User từ API
    private String name;
    private String username;
    private int profileImageResource;
    private boolean isFollowing;
    private String avatarUrl; // Thêm URL avatar từ API

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

    // Constructor mới cho dữ liệu từ API
    public RecommendUser(int id, String name, String username, String avatarUrl, int defaultImageResource) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.profileImageResource = defaultImageResource;
        this.isFollowing = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getProfileImageResource() {
        return profileImageResource;
    }

    public void setProfileImageResource(int profileImageResource) {
        this.profileImageResource = profileImageResource;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}