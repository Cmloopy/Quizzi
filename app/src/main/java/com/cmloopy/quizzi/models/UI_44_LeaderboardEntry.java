package com.cmloopy.quizzi.models;

public class UI_44_LeaderboardEntry {
    private int rank;
    private String playerName;
    private int score;
    private int avatarResourceId;

    public UI_44_LeaderboardEntry(int rank, String playerName, int score, int avatarResourceId) {
        this.rank = rank;
        this.playerName = playerName;
        this.score = score;
        this.avatarResourceId = avatarResourceId;
    }

    public int getRank() {
        return rank;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getAvatarResourceId() {
        return avatarResourceId;
    }
}