package com.cmloopy.quizzi.fragment.QuizCreate.after;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.after.Question;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionTrueFalse;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCHelper;

public class QCQuestionTrueFalseFragment extends QCBaseResponseFragment{

    private static final String ARG_RESPONSE_TRUE_FALSE = "response_true_false";
    private QuestionTrueFalse questionTrueFalse;
    private CardView trueAnswerButtonContainer;
    private CardView falseAnswerButtonContainer;
    private CardView trueCheckIconContainer;
    private CardView falseCheckIconContainer;
    private Button trueAnswerButton;
    private Button falseAnswerButton;
    private ImageView trueCheckIcon;
    private ImageView falseCheckIcon;
    private boolean trueAnswerSelected;
    private boolean falseAnswerSelected;

    public static QCQuestionTrueFalseFragment newInstance(QuestionTrueFalse responseChoice) {
        QCQuestionTrueFalseFragment fragment = new QCQuestionTrueFalseFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESPONSE_TRUE_FALSE, responseChoice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionTrueFalse = (QuestionTrueFalse) getArguments().getSerializable(ARG_RESPONSE_TRUE_FALSE);
            if (questionTrueFalse == null) {
                questionTrueFalse = new QuestionTrueFalse();
            }
        } else {
            questionTrueFalse = new QuestionTrueFalse();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_true_false, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(getCurrentQuestion());
        setupUI(view);
        setupListeners();
        return view;
    }

    private void setupUI(View view) {
        trueAnswerButtonContainer = view.findViewById(R.id.btn_true_answer_wrap);
        falseAnswerButtonContainer = view.findViewById(R.id.btn_false_answer_wrap);
        trueAnswerButton = view.findViewById(R.id.btn_true_answer);
        falseAnswerButton = view.findViewById(R.id.btn_false_answer);
        trueCheckIconContainer = trueAnswerButtonContainer.findViewById(R.id.check_icon_container_true);
        falseCheckIconContainer = falseAnswerButtonContainer.findViewById(R.id.check_icon_container_false);
        trueCheckIcon = trueCheckIconContainer.findViewById(R.id.check_icon_true);
        falseCheckIcon = falseCheckIconContainer.findViewById(R.id.check_icon_false);

        trueAnswerSelected = questionTrueFalse.isCorrectAnswer();
        falseAnswerSelected = !questionTrueFalse.isCorrectAnswer();
        updateAnswerButtonStates();
    }

    private void setupListeners() {
        trueAnswerButtonContainer.setBackgroundResource(R.drawable.ui_qc_bg_elevation_green);
        falseAnswerButtonContainer.setBackgroundResource(R.drawable.ui_qc_bg_elevation_red);

        QCHelper.applyLayerColorToIcon(getContext(), R.drawable.ui_qc_bg_elevation_green, trueCheckIcon);
        QCHelper.applyLayerColorToIcon(getContext(), R.drawable.ui_qc_bg_elevation_red, falseCheckIcon);

        trueAnswerButton.setOnClickListener(v -> {
            if (!trueAnswerSelected) {
                trueAnswerSelected = true;
                falseAnswerSelected = false;
                Toast.makeText(getContext(), "True selected", Toast.LENGTH_SHORT).show();
                updateAnswerButtonStates();
                questionTrueFalse.setCorrectAnswer(true);
                notifyQuestionUpdated2();
            }
        });



        falseAnswerButton.setOnClickListener(v -> {
            if (!falseAnswerSelected) {
                falseAnswerSelected = true;
                trueAnswerSelected = false;
                Toast.makeText(getContext(), "False selected", Toast.LENGTH_SHORT).show();
                updateAnswerButtonStates();
                questionTrueFalse.setCorrectAnswer(false);
                notifyQuestionUpdated2();
            }
        });
    }

    private void updateAnswerButtonStates() {
        trueCheckIconContainer.setVisibility(trueAnswerSelected ? View.VISIBLE : View.GONE);
        falseCheckIconContainer.setVisibility(falseAnswerSelected ? View.VISIBLE : View.GONE);
    }

    private void notifyQuestionUpdated2() {
        if (listener != null) {
            listener.onUpdateQuestion(questionTrueFalse.getPosition(), questionTrueFalse);
        }
    }

    @Override
    protected void onCoverImageClicked() {
        // Implement as needed
    }

    @Override
    public Question getCurrentQuestion() {
        return questionTrueFalse;
    }

}