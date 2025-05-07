package com.cmloopy.quizzi.models.QuizCreate.before;

public class SliderQuestion extends Question {
    private int minValue;
    private int maxValue;
    private int correctValue;
    private String lambda; // Default, Small, Medium, Large,

    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_CORRECT_VALUE = 50;
    private static final String DEFAULT_LAMBDA = "Default";

    public SliderQuestion() {
        super();
        minValue = DEFAULT_MIN_VALUE;
        maxValue = DEFAULT_MAX_VALUE;
        correctValue = DEFAULT_CORRECT_VALUE;
        lambda = DEFAULT_LAMBDA;
    }

    public SliderQuestion(int position, String id, String title, String imageUrl, int time, int point, int minValue, int maxValue, int correctValue, String lambda) {
        super(position, id, title, imageUrl, time, point);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.correctValue = correctValue;
        this.lambda = lambda;
    }

    @Override
    public int getType() {
        return TYPE_SLIDER;
    }

    // Getters and setters
    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }
    public String getLambda() {
        return lambda;
    }


    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getCorrectValue() {
        return correctValue;
    }

    public void setCorrectValue(int correctValue) {
        this.correctValue = correctValue;
    }
    public void setLambda(String lambda) {
        this.lambda = lambda;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() &&
                minValue == DEFAULT_MIN_VALUE &&
                maxValue == DEFAULT_MAX_VALUE &&
                correctValue == DEFAULT_CORRECT_VALUE &&
                DEFAULT_LAMBDA.equals(lambda);
    }

}