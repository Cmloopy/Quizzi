package com.cmloopy.quizzi.fragment.QuestionCreate;

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
import com.cmloopy.quizzi.adapter.QuestionCreate.QCPuzzleOptionAdapter;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionPuzzle;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCPuzzleOptionCreateDialog;

import java.util.ArrayList;
import java.util.List;

public class QCQuestionPuzzleFragment extends QCBaseQuestionFragment implements QCPuzzleOptionAdapter.OnPuzzlePieceClickListener {

    private static final String ARG_RESPONSE_PUZZLE = "response_puzzle";
    private QuestionPuzzle questionPuzzle;
    private RecyclerView answersRecyclerView;
    private QCPuzzleOptionAdapter answerAdapter;
    private ItemTouchHelper itemTouchHelper;
    private final List<PuzzleOption> tempAnswers = new ArrayList<>();

    public static QCQuestionPuzzleFragment newInstance(QuestionPuzzle questionPuzzle) {
        QCQuestionPuzzleFragment fragment = new QCQuestionPuzzleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESPONSE_PUZZLE, questionPuzzle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionPuzzle = (QuestionPuzzle) getArguments().getSerializable(ARG_RESPONSE_PUZZLE);
            if (questionPuzzle == null) {
                questionPuzzle = new QuestionPuzzle();
            }
            initializeTempAnswers();
        } else {
            questionPuzzle = new QuestionPuzzle();
            initializeTempAnswers();
        }
    }

    private void initializeTempAnswers() {
        tempAnswers.addAll(questionPuzzle.getPuzzlePieces());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_puzzle, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(getCurrentQuestion());
        setupRecyclerView(view);
        return view;
    }


    private void setupRecyclerView(View view) {
        answersRecyclerView = view.findViewById(R.id.answers_recycler_view);
        answerAdapter = new QCPuzzleOptionAdapter(requireContext(), tempAnswers, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        answersRecyclerView.setLayoutManager(linearLayoutManager);

        answersRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
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
        new QCPuzzleOptionCreateDialog.Builder(getContext())
                .setHeaderTitle("Edit Puzzle Piece")
                .setMultipleChoiceQuestion(false)
                .setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                .setListener(new QCPuzzleOptionCreateDialog.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position, PuzzleOption piece) {
                        // Update the temporary answer for the adapter
                        tempAnswers.set(position, piece);

                        PuzzleOption option = questionPuzzle.getPuzzlePieces().get(position);
                        option.setText(piece.getText());
                        // option.setBackground(answer.getBackgroundColor());

                        notifyQuestionUpdated();
                        answerAdapter.notifyItemChanged(position);
                    }

                })
                .setAnswer(tempAnswers.get(position))
                .setPosition(position)
                .setAnswers(tempAnswers)
                .build()
                .show();
    }

    @Override
    public void onPuzzlePieceLongClick(int position) {
        Toast.makeText(getContext(), "Long click on Puzzle Piece " + (position + 1), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangePuzzlePiecePosition(List<PuzzleOption> oldAnswers, List<PuzzleOption> newAnswers) {
        for (int i = 0; i < tempAnswers.size(); i++) {
            tempAnswers.get(i).setOption(newAnswers.get(i));
        }

        List<PuzzleOption> updatedOptions = new ArrayList<>();
        for(int i = 0; i < tempAnswers.size(); i++) {
            PuzzleOption option = new PuzzleOption();
            option.setPosition(i);
            option.setText(tempAnswers.get(i).getText());
            option.setCorrectPosition(i + 1);
            updatedOptions.add(option);
        }

        questionPuzzle.setPuzzlePieces(updatedOptions);
        notifyQuestionUpdated();
        answerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCoverImageClicked() {
        // Implement as needed
    }

    @Override
    public Question getCurrentQuestion() {
        return questionPuzzle;
    }

}
