package com.cmloopy.quizzi.models.QuizCreate.after;

import com.cmloopy.quizzi.models.QuizCreate.after.Option.Option;
import com.cmloopy.quizzi.models.QuizCreate.after.Option.PuzzleOption;

import java.io.Serializable;
import java.util.List;

public class QuestionPuzzle extends Question implements Serializable {
    private List<PuzzleOption> puzzlePieces;

    public QuestionPuzzle() {
        super();
        puzzlePieces = Option.initializeDefaultOptions(Option.TYPE_PUZZLE, 4);
    }

    public QuestionPuzzle(int position, String id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<PuzzleOption> puzzlePieces) {
        super(position, id, content, imageUrl, audioUrl, point, timeLimit, description);
        this.puzzlePieces = puzzlePieces;
    }

    public List<PuzzleOption> getPuzzlePieces() {
        return puzzlePieces;
    }

    public void setPuzzlePieces(List<PuzzleOption> puzzlePieces) {
        this.puzzlePieces = puzzlePieces;
    }
}

