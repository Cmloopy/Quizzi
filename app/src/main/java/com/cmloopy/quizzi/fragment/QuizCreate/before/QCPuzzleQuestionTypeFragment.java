package com.cmloopy.quizzi.fragment.QuizCreate.before;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.before.QCPuzzleAnswerAdapter;
import com.cmloopy.quizzi.models.QuizCreate.before.QCAnswer;
import com.cmloopy.quizzi.models.QuizCreate.before.PuzzleQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.before.QCAnswerCreateDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QCPuzzleQuestionTypeFragment extends QCBaseQuestionFragment implements QCPuzzleAnswerAdapter.OnPuzzlePieceClickListener {

    private static final String ARG_PUZZLE_PIECES = "puzzle_pieces";
    private static final String ARG_SOLUTION = "solution";

    private PuzzleQuestion puzzleQuestion;
    private RecyclerView answersRecyclerView;
    private QCPuzzleAnswerAdapter answerAdapter;
    private ItemTouchHelper itemTouchHelper;
    private final List<QCAnswer> answers = new ArrayList<>();

    public static QCPuzzleQuestionTypeFragment newInstance(PuzzleQuestion question) {
        QCPuzzleQuestionTypeFragment fragment = new QCPuzzleQuestionTypeFragment();
        Bundle args = createBaseBundle(question);
        args.putStringArrayList(ARG_PUZZLE_PIECES, new ArrayList<>(question.getPuzzlePieces()));
        args.putString(ARG_SOLUTION, question.getSolution());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        puzzleQuestion = new PuzzleQuestion();
        if (getArguments() != null) {
            initializePuzzleQuestion(getArguments());
            if (puzzleQuestion.isDefaultInstance()) {
                setDefaultPuzzleQuestion();
            }
        } else {
            setDefaultPuzzleQuestion();
        }
    }

    private void initializePuzzleQuestion(Bundle args) {
        super.initializeBaseQuestion(puzzleQuestion, args);
        puzzleQuestion.setPuzzlePieces(args.getStringArrayList(ARG_PUZZLE_PIECES));
        puzzleQuestion.setSolution(args.getString(ARG_SOLUTION, ""));

        List<String> pieces = puzzleQuestion.getPuzzlePieces() != null ? puzzleQuestion.getPuzzlePieces() : new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            answers.add(new QCAnswer(pieces.get(i), false, answerBackgroundColor[i % 4]));
        }
    }

    private void setDefaultPuzzleQuestion() {
        super.setDefaultBaseQuestion(puzzleQuestion);
        puzzleQuestion.setPuzzlePieces(new ArrayList<>());
        puzzleQuestion.setSolution("");

        for (int color : answerBackgroundColor) {
            QCAnswer _answer = new QCAnswer("", false, color);
            answers.add(_answer);
        }
        answers.get(0).setCorrect(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_puzzle, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(puzzleQuestion);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        answersRecyclerView = view.findViewById(R.id.answers_recycler_view);
        answerAdapter = new QCPuzzleAnswerAdapter(requireContext(), answers, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        answersRecyclerView.setLayoutManager(linearLayoutManager);

        answersRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int spacing = 30;
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = spacing;
                }
            }
        });

        answersRecyclerView.setAdapter(answerAdapter);
        answersRecyclerView.setNestedScrollingEnabled(false);
        itemTouchHelper = answerAdapter.createItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(answersRecyclerView);

    }

    @Override
    public void onPuzzlePieceClick(int position) {
        new QCAnswerCreateDialog.Builder(getContext())
                .setHeaderTitle("Edit Puzzle Piece")
                .setMultipleChoiceQuestion(false)
                .setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                .setListener(new QCAnswerCreateDialog.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position, QCAnswer answer) {
                        answers.set(position, answer);
                        List<String> options = answers.stream()
                                .map(QCAnswer::getText)
                                .collect(Collectors.toList());

                        puzzleQuestion.setPuzzlePieces(options);
                        if(listener != null) {
                            listener.onUpdateQuestion(puzzleQuestion.getPosition(), puzzleQuestion);
                        }
                        answerAdapter.notifyItemChanged(position);
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onItemSelectedUpdateAll(List<QCAnswer> updatedAnswers) {
                        for (int i = 0; i < answers.size(); i++) {
                            answers.get(i).setCorrect(updatedAnswers.get(i).isCorrect());
                        }
                        answerAdapter.notifyDataSetChanged();
                    }
                })
                .setAnswer(answers.get(position))
                .setPosition(position)
                .setAnswers(answers)
                .build()
                .show();
    }

    @Override
    public void onPuzzlePieceLongClick(int position) {
        Toast.makeText(getContext(), "Long click on Puzzle Piece " + (position + 1), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangePuzzlePiecePosition(List<QCAnswer> oldAnswers, List<QCAnswer> newAnswers) {
        for (int i = 0; i < answers.size(); i++) {
            answers.get(i).setAnswer(newAnswers.get(i));
        }
        List<String> options = answers.stream()
                .map(QCAnswer::getText)
                .collect(Collectors.toList());

        puzzleQuestion.setPuzzlePieces(options);
        if(listener != null) {
            listener.onUpdateQuestion(puzzleQuestion.getPosition(), puzzleQuestion);
        }

        answerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCoverImageClicked() {

    }

    @Override
    public Question getCurrentQuestion() {
        return puzzleQuestion;
    }
}
