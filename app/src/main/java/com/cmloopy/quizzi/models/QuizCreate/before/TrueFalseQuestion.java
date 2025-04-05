package com.cmloopy.quizzi.models.QuizCreate.before;

public class TrueFalseQuestion extends Question {
    private static final boolean DEFAULT_CORRECT_ANSWER = true;

    private boolean correctAnswer;

    public TrueFalseQuestion() {
        super();
        this.correctAnswer = DEFAULT_CORRECT_ANSWER;
    }

    public TrueFalseQuestion(int position, String id, String title, String imageUrl, int time, int point,
                             boolean correctAnswer) {
        super(position, id, title, imageUrl, time, point);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public int getType() {
        return TYPE_TRUE_FALSE;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() && 
                correctAnswer == DEFAULT_CORRECT_ANSWER;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
