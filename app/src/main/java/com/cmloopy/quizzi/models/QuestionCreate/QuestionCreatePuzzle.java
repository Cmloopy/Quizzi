package com.cmloopy.quizzi.models.QuestionCreate;

import com.cmloopy.quizzi.models.QuestionCreate.Option.Option;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;

import java.io.Serializable;
import java.util.List;

public class QuestionCreatePuzzle extends QuestionCreate implements Serializable {
    private List<PuzzleOption> puzzlePieces;

    public QuestionCreatePuzzle() {
        super();
        puzzlePieces = Option.initializeDefaultOptions(Option.TYPE_PUZZLE, 4);
    }


    public QuestionCreatePuzzle(int position, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<PuzzleOption> puzzlePieces) {
        super(position, content, imageUrl, audioUrl, point, timeLimit, description);
        this.puzzlePieces = puzzlePieces;
    }


    public QuestionCreatePuzzle(int position, Long id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<PuzzleOption> puzzlePieces) {
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

