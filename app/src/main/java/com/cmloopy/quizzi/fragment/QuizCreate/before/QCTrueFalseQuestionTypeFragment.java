package com.cmloopy.quizzi.fragment.QuizCreate.before;

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
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.TrueFalseQuestion;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCHelper;

public class QCTrueFalseQuestionTypeFragment extends QCBaseQuestionFragment {
    private static final String ARG_CORRECT_ANSWER = "correct_answer";

    private TrueFalseQuestion trueFalseQuestion;
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

    public static QCTrueFalseQuestionTypeFragment newInstance(TrueFalseQuestion question) {
        QCTrueFalseQuestionTypeFragment fragment = new QCTrueFalseQuestionTypeFragment();
        Bundle args = createBaseBundle(question);
        args.putBoolean(ARG_CORRECT_ANSWER, question.isCorrectAnswer());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trueFalseQuestion = new TrueFalseQuestion();
        if (getArguments() != null) {
            initializeTrueFalseQuestion(getArguments());
            if(trueFalseQuestion.isDefaultInstance()) {
                setDefaultTrueFalseQuestion();
            }
        } else {
            setDefaultTrueFalseQuestion();
        }
    }

    private void initializeTrueFalseQuestion(Bundle args) {
        super.initializeBaseQuestion(trueFalseQuestion, args);
        trueFalseQuestion.setCorrectAnswer(args.getBoolean(ARG_CORRECT_ANSWER, true));

        trueAnswerSelected = trueFalseQuestion.isCorrectAnswer();
        falseAnswerSelected = !trueAnswerSelected;
    }

    private void setDefaultTrueFalseQuestion() {
        super.setDefaultBaseQuestion(trueFalseQuestion);
        trueFalseQuestion.setCorrectAnswer(true);

        trueAnswerSelected = true;
        falseAnswerSelected = false;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_true_false, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(trueFalseQuestion);
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
                trueFalseQuestion.setCorrectAnswer(true);
                notifyQuestionUpdated2();
            }
        });



        falseAnswerButton.setOnClickListener(v -> {
            if (!falseAnswerSelected) {
                falseAnswerSelected = true;
                trueAnswerSelected = false;
                Toast.makeText(getContext(), "False selected", Toast.LENGTH_SHORT).show();
                updateAnswerButtonStates();
                trueFalseQuestion.setCorrectAnswer(false);
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
            listener.onUpdateQuestion(trueFalseQuestion.getPosition(), trueFalseQuestion);
        }
    }

    @Override
    protected void onCoverImageClicked() {
        // Implement as needed
    }

    @Override
    public Question getCurrentQuestion() {
        return trueFalseQuestion;
    }
}
