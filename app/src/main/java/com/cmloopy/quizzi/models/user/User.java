package com.cmloopy.quizzi.models.user;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("email")
    private String email;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("country")
    private String country;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("age")
    private int age;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("resetPasswordCode")
    private String resetPasswordCode;

    @SerializedName("totalQuizs")
    private int totalQuizs;

    @SerializedName("totalCollections")
    private int totalCollections;

    @SerializedName("totalPlays")
    private int totalPlays;

    @SerializedName("totalFollowers")
    private int totalFollowers;

    @SerializedName("totalFollowees")
    private int totalFollowees;

    @SerializedName("totalPlayers")
    private int totalPlayers;

    @SerializedName("userMusicEffect")
    private UserMusicEffect userMusicEffect;

    @SerializedName("following")
    private List<Object> following;

    @SerializedName("followers")
    private List<Object> followers;

    // Getters and setters (có thể tạo bằng IDE)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getResetPasswordCode() { return resetPasswordCode; }
    public void setResetPasswordCode(String resetPasswordCode) { this.resetPasswordCode = resetPasswordCode; }

    public int getTotalQuizs() { return totalQuizs; }
    public void setTotalQuizs(int totalQuizs) { this.totalQuizs = totalQuizs; }

    public int getTotalCollections() { return totalCollections; }
    public void setTotalCollections(int totalCollections) { this.totalCollections = totalCollections; }

    public int getTotalPlays() { return totalPlays; }
    public void setTotalPlays(int totalPlays) { this.totalPlays = totalPlays; }

    public int getTotalFollowers() { return totalFollowers; }
    public void setTotalFollowers(int totalFollowers) { this.totalFollowers = totalFollowers; }

    public int getTotalFollowees() { return totalFollowees; }
    public void setTotalFollowees(int totalFollowees) { this.totalFollowees = totalFollowees; }

    public int getTotalPlayers() { return totalPlayers; }
    public void setTotalPlayers(int totalPlayers) { this.totalPlayers = totalPlayers; }

    public UserMusicEffect getUserMusicEffect() { return userMusicEffect; }
    public void setUserMusicEffect(UserMusicEffect userMusicEffect) { this.userMusicEffect = userMusicEffect; }

    public List<Object> getFollowing() { return following; }
    public void setFollowing(List<Object> following) { this.following = following; }

    public List<Object> getFollowers() { return followers; }
    public void setFollowers(List<Object> followers) { this.followers = followers; }
}
