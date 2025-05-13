package com.cmloopy.quizzi.models.question.puzzle;

import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionType;

import java.util.List;

public class PuzzleQuestion extends Question {
    public List<PuzzlePiece> puzzlePieces;

    public PuzzleQuestion(List<PuzzlePiece> puzzlePieces) {
        this.puzzlePieces = puzzlePieces;
    }

    public PuzzleQuestion(int id, int quizId, QuestionType questionType, String image, String audio, String content, int point, int timeLimit, String description, String createdAt, String updatedAt, List<PuzzlePiece> puzzlePieces) {
        super(id, quizId, questionType, image, audio, content, point, timeLimit, description, createdAt, updatedAt);
        this.puzzlePieces = puzzlePieces;
    }

    public List<PuzzlePiece> getPuzzlePieces() {
        return puzzlePieces;
    }

    public void setPuzzlePieces(List<PuzzlePiece> puzzlePieces) {
        this.puzzlePieces = puzzlePieces;
    }
}
