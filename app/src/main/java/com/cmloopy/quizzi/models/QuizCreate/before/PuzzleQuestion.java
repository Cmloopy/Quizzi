package com.cmloopy.quizzi.models.QuizCreate.before;

import java.util.ArrayList;
import java.util.List;

public class PuzzleQuestion extends Question {
    private static final List<String> DEFAULT_PUZZLE_PIECE = new ArrayList<>();
    private static final String DEFAULT_SOLUTION = "";

    private List<String> puzzlePiece;
    private String solution;

    public PuzzleQuestion() {
        super();
        this.puzzlePiece = new ArrayList<>(DEFAULT_PUZZLE_PIECE);
        this.solution = DEFAULT_SOLUTION;
    }

    public PuzzleQuestion(int position, String id, String title, String imageUrl, int time, int point, List<String> puzzlePiece, String solution) {
        super(position, id, title, imageUrl, time, point);
        this.puzzlePiece = puzzlePiece != null ? puzzlePiece : new ArrayList<>(DEFAULT_PUZZLE_PIECE);
        this.solution = solution != null ? solution : DEFAULT_SOLUTION;
    }

    @Override
    public int getType() {
        return TYPE_PUZZLE;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() &&
                puzzlePiece.equals(DEFAULT_PUZZLE_PIECE) &&
                solution.equals(DEFAULT_SOLUTION);
    }

    public List<String> getPuzzlePieces() {
        return puzzlePiece;
    }

    public void setPuzzlePieces(List<String> puzzlePiece) {
        this.puzzlePiece = puzzlePiece;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
