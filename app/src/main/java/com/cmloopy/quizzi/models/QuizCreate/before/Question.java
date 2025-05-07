package com.cmloopy.quizzi.models.QuizCreate.before;

public abstract class Question {
    private int position;
    private String id;
    private String title;
    private String imageUrl;
    private int time;
    private int point;
    public Question(int position, String id, String title, String imageUrl, int time, int point) {
        this.position = position;
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.time = time;
        this.point = point;
    }

    public abstract int getType(); // To identify question type

    public static final int TYPE_SLIDER = 1;
    public static final int TYPE_QUIZ = 2;
    public static final int TYPE_CHECKBOX = 3;
    public static final int TYPE_PUZZLE = 4;
    public static final int TYPE_TEXT = 5;
    public static final int TYPE_QUIZ_AUDIO = 6;
    public static final int TYPE_TRUE_FALSE = 7;
    public static final int TYPE_SAY_WORD = 8;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }
    public int getTime() {
        return time;
    }
    public int getPoint() {
        return point;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public void setPoint(int point) { this.point = point; }


    protected static final int DEFAULT_POSITION = 0;

    protected static final String DEFAULT_ID = "";
    protected static final String DEFAULT_TITLE = "";
    protected static final String DEFAULT_IMAGE_URL = "";
    protected static final int DEFAULT_TIME = 10;
    protected static final int DEFAULT_POINT = 200;

    public Question() {
        this.id = DEFAULT_ID;
        this.title = DEFAULT_TITLE;
        this.imageUrl = DEFAULT_IMAGE_URL;
        this.time = DEFAULT_TIME;
        this.point = DEFAULT_POINT;
    }

    public boolean hasParentDefaultValues() {
        return
//                position == DEFAULT_POSITION &&
                (DEFAULT_ID.equals(id) || id == null) &&
                (DEFAULT_TITLE.equals(title) || title == null) &&
                (DEFAULT_IMAGE_URL.equals(imageUrl) || imageUrl == null) &&
                time == DEFAULT_TIME &&
                point == DEFAULT_POINT;
    }
}