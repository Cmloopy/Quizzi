package com.cmloopy.quizzi.fragment.QuestionCreate;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionChoice;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCChoiceOptionCreateDialog;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCChoiceOptionAdapter;

import java.util.ArrayList;
import java.util.List;

public class QCQuestionCheckboxFragment extends QCBaseQuestionFragment implements QCChoiceOptionAdapter.OnAnswerClickListener {

    private static final String ARG_RESPONSE_CHOICE = "response_choice";
    private QuestionChoice questionChoice;
    private RecyclerView answersRecyclerView;
    private QCChoiceOptionAdapter answerAdapter;
    private final List<ChoiceOption> tempAnswers = new ArrayList<>();
    private View coverImagePlaceholder;

    public static QCQuestionCheckboxFragment newInstance(QuestionChoice questionChoice) {
        QCQuestionCheckboxFragment fragment = new QCQuestionCheckboxFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESPONSE_CHOICE, questionChoice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionChoice = (QuestionChoice) getArguments().getSerializable(ARG_RESPONSE_CHOICE);
            if (questionChoice == null) {
                questionChoice = new QuestionChoice();
            }
            initializeTempAnswers();
        } else {
            questionChoice = new QuestionChoice();
            initializeTempAnswers();
        }
    }

    private void initializeTempAnswers() {
        tempAnswers.addAll(questionChoice.getChoiceOptions());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_checkbox, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(getCurrentQuestion());
        setupRecyclerView(view);

        // If we already have a cover image, hide the placeholder
        if (questionChoice.getImage() != null && !questionChoice.getImage().isEmpty()) {
            coverImagePlaceholder.setVisibility(View.GONE);
        }

        return view;
    }

    private void setupRecyclerView(View view) {
        answersRecyclerView = view.findViewById(R.id.answers_recycler_view);
        answerAdapter = new QCChoiceOptionAdapter(requireContext(), tempAnswers, this, R.layout.ui_qc_item_answer_checkbox_type);
        answersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        answersRecyclerView.setAdapter(answerAdapter);
        answersRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = 20;
                int position = answersRecyclerView.getChildAdapterPosition(view);
                if(position > 0) {
                    outRect.top = spacing;
                }
            }
        });
    }

    @Override
    public void onAnswerClick(int position) {
        new QCChoiceOptionCreateDialog.Builder(getContext())
                .setHeaderTitle("Create Answer")
                .setMultipleChoiceQuestion(true)
                .setListener(new QCChoiceOptionCreateDialog.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position, ChoiceOption answer) {
                        // Update the temporary answer for the adapter
                        tempAnswers.set(position, answer);
                        answerAdapter.notifyItemChanged(position);

                        ChoiceOption option = questionChoice.getChoiceOptions().get(position);
                        option.setText(answer.getText());
                        option.setCorrect(answer.isCorrect());
//                        option.setBackground(answer.getBackgroundColor());

                        notifyQuestionUpdated();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onItemSelectedUpdateAll(List<ChoiceOption> updatedAnswers) {
                        for (int i = 0; i < tempAnswers.size(); i++) {
                            ChoiceOption answer = updatedAnswers.get(i);
                            ChoiceOption option = questionChoice.getChoiceOptions().get(i);

                            // Update both temp answers for adapter and actual ChoiceOptions
                            tempAnswers.get(i).setCorrect(answer.isCorrect());
                            option.setCorrect(answer.isCorrect());
                        }

                        notifyQuestionUpdated();
                        answerAdapter.notifyDataSetChanged();
                    }
                })
                .setAnswer(tempAnswers.get(position))
                .setPosition(position)
                .setAnswers(tempAnswers)
                .build()
                .show();
    }

    @Override
    public void onAnswerLongClick(int position) {
        Toast.makeText(getContext(), "Long click on Answer " + (position + 1), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCoverImageClicked() {
    }


    @Override
    protected void onMediaClicked() {

    }

    @Override
    protected void playAudio() {

    }

    @Override
    public Question getCurrentQuestion() {
        return questionChoice;
    }
}