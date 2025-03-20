package com.cmloopy.quizzi.models;

public class UI41Follower {
    private String name;
    private String username;
    private int profileImage;
    private boolean isFollowing;

    public UI41Follower(String name, String username, int profileImage, boolean isFollowing) {
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.isFollowing = isFollowing;
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public int getProfileImage() { return profileImage; }
    public boolean isFollowing() { return isFollowing; }
    public void setFollowing(boolean following) { isFollowing = following; }
}
