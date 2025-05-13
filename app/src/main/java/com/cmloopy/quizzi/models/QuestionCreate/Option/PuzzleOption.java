package com.cmloopy.quizzi.models.QuestionCreate.Option;

import java.io.Serializable;

public class PuzzleOption extends Option implements Serializable {
    private int correctPosition;

    public PuzzleOption() {
        super();
        this.correctPosition = 0;
    }

    public PuzzleOption(int position, String text, String imageUrl, String audioUrl, int correctPosition) {
        super(position, text, imageUrl, audioUrl);
        this.correctPosition = correctPosition;
    }

    public PuzzleOption(int position, Long id, String text, String imageUrl, String audioUrl, int correctPosition) {
        super(position, id, text, imageUrl, audioUrl);
        this.correctPosition = correctPosition;
    }

    public int getCorrectPosition() {
        return correctPosition;
    }

    public void setCorrectPosition(int correctPosition) {
        this.correctPosition = correctPosition;
    }

    public void setOption(PuzzleOption puzzleOption) {
        super.setOption(puzzleOption);
        this.correctPosition = puzzleOption.getCorrectPosition();
    }
}
