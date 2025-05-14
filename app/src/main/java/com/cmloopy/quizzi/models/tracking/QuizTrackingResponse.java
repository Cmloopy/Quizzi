package com.cmloopy.quizzi.models.tracking;

import java.util.List;

public class QuizTrackingResponse {
    private int id;
    private long quizId;
    private int userId;
    private int totalPoints;
    private int rank;
    private int currentStreak;
    private int bestStreak;

    public QuizTrackingResponse(int id, long quizId, int userId, int totalPoints, int rank, int currentStreak, int bestStreak) {
        this.id = id;
        this.quizId = quizId;
        this.userId = userId;
        this.totalPoints = totalPoints;
        this.rank = rank;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
    }

    public QuizTrackingResponse(long quizId, int userId, int totalPoints, int rank, int currentStreak, int bestStreak) {
        this.quizId = quizId;
        this.userId = userId;
        this.totalPoints = totalPoints;
        this.rank = rank;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }
}