    package com.cmloopy.quizzi.models.HomeLibrary.MyQuizzo;

    import com.google.gson.annotations.SerializedName;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    public class QuizResponse {
        @SerializedName("id")
        private int id;

        @SerializedName("userId")
        private int userId;

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("keyword")
        private String keyword;  // This appears to be a string representation of a list

        @SerializedName("score")
        private int score;

        @SerializedName("coverPhoto")
        private String coverPhoto;  // Using String as it appears to be nullable

        @SerializedName("createdAt")
        private String createdAt;  // Using String for date representation

        @SerializedName("updatedAt")
        private String updatedAt;  // Using String for date representation

        @SerializedName("visible")
        private boolean visible;

        @SerializedName("visibleQuizQuestion")
        private boolean visibleQuizQuestion;

        @SerializedName("shuffle")
        private boolean shuffle;

        @SerializedName("quizGames")
        private List<Object> quizGames;  // Using Object as placeholder since array is empty

        // Default constructor
        public QuizResponse() {
            quizGames = new ArrayList<>();
        }

        // Getters and setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getCoverPhoto() {
            return coverPhoto;
        }

        public void setCoverPhoto(String coverPhoto) {
            this.coverPhoto = coverPhoto;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisibleQuizQuestion() {
            return visibleQuizQuestion;
        }

        public void setVisibleQuizQuestion(boolean visibleQuizQuestion) {
            this.visibleQuizQuestion = visibleQuizQuestion;
        }

        public boolean isShuffle() {
            return shuffle;
        }

        public void setShuffle(boolean shuffle) {
            this.shuffle = shuffle;
        }

        public List<Object> getQuizGames() {
            return quizGames;
        }

        public void setQuizGames(List<Object> quizGames) {
            this.quizGames = quizGames;
        }
    }
