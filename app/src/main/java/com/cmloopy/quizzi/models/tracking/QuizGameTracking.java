package com.cmloopy.quizzi.models.tracking;

public class QuizGameTracking {
    private int id;
    private int quizId;
    private int userId;
    private int[] participants;
    private int totalPoints;
    private int rank;
    private int currentStreak;
    private int bestStreak;
    private String[] questionTrackings;

    public QuizGameTracking(int id, int quizId, int userId, int[] participants, int totalPoints, int rank, int currentStreak, int bestStreak, String[] questionTrackings) {
        this.id = id;
        this.quizId = quizId;
        this.userId = userId;
        this.participants = participants;
        this.totalPoints = totalPoints;
        this.rank = rank;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.questionTrackings = questionTrackings;
    }

    public int[] getParticipants() {
        return participants;
    }

    public void setParticipants(int[] participants) {
        this.participants = participants;
    }

    public String[] getQuestionTrackings() {
        return questionTrackings;
    }

    public void setQuestionTrackings(String[] questionTrackings) {
        this.questionTrackings = questionTrackings;
    }

    public QuizGameTracking(int id, int quizId, int userId, int totalPoints, int rank, int currentStreak, int bestStreak) {
        this.id = id;
        this.quizId = quizId;
        this.userId = userId;
        this.totalPoints = totalPoints;
        this.rank = rank;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
    }

    public QuizGameTracking(int quizId, int userId, int totalPoints, int rank, int currentStreak, int bestStreak) {
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

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
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
