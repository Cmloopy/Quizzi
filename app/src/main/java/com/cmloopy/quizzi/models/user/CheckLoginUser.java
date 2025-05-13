package com.cmloopy.quizzi.models.user;

public class CheckLoginUser {

    private String usernameOrEmail;
    private String password;

    public CheckLoginUser(String username, String password) {
        this.usernameOrEmail = username;
        this.password = password;
    }

    // Getters & Setters

    public String getUsername() { return usernameOrEmail; }
    public void setUsername(String username) { this.usernameOrEmail = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
