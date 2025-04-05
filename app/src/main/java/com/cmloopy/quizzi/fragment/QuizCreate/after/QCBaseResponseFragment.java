package com.cmloopy.quizzi.fragment.QuizCreate.after;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.after.Question;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCGenericSelectionDialog;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCHelper;
import com.cmloopy.quizzi.views.QuizCreate.after.QuizCreateActivity;

import java.util.Arrays;
import java.util.List;

public abstract class QCBaseResponseFragment extends Fragment {
    protected FrameLayout coverImageContainer;
    protected Button timeLimitButton;
    protected Button pointButton;
    protected Button questionTypeButton;
    protected EditText questionEditText;

    protected int defaultTimeLimit;
    protected int defaultPoint;

    protected OnChangeListener listener;

    public interface OnChangeListener {
        void onUpdateQuestion(int position, Question question);
        void onDeleteQuestion(int position, Question question);
    }

    public void setListener(OnChangeListener listener) {
        this.listener = listener;
    }

    protected static Bundle createBaseBundle(com.cmloopy.quizzi.models.QuizCreate.before.Question question) {
        Bundle args = new Bundle();
        return args;
    }

    protected void setUpBaseView(Question question) {
        timeLimitButton.setText(String.format("%s sec", String.valueOf(question.getTimeLimit())));
        pointButton.setText(String.format("%s pt", String.valueOf(question.getPoint())));
        timeLimitButton.setAllCaps(false);
        pointButton.setAllCaps(false);
        questionEditText.setText(question.getContent());
    }

    protected final List<Integer> TIME_LIMITS = Arrays.asList(5, 10, 20, 30, 45, 60, 90, 120);
    protected final List<Integer> POINTS = Arrays.asList(50, 100, 200, 250, 500, 750, 1000, 2000);
    protected final int[] answerBackgroundColor = {
            R.drawable.ui_qc_bg_elevation_blue,
            R.drawable.ui_qc_bg_elevation_red,
            R.drawable.ui_qc_bg_elevation_orange,
            R.drawable.ui_qc_bg_elevation_green
    };
    public void onCreateBaseView(View view) {
        initializeViews(view);

        setupCommonListeners();

    }

    private void initializeViews(View view) {
        coverImageContainer = view.findViewById(R.id.cover_image_container);
        timeLimitButton = view.findViewById(R.id.btn_time_limit);
        pointButton = view.findViewById(R.id.btn_points);
        questionTypeButton = view.findViewById(R.id.btn_question_type);
        questionEditText = view.findViewById(R.id.question_edit_text);


    }

    protected void setupCommonListeners() {
        setupQuestionEditText();

        coverImageContainer.setOnClickListener(v -> onCoverImageClicked());

        setupTimeLimitButton();
        setupPointsButton();
        setupQuestionTypeButton();
    }

    private void setupQuestionEditText() {
        questionEditText.setOnFocusChangeListener((v, hasFocus) -> {
            int backgroundRes = hasFocus
                    ? R.drawable.ui_qc_bg_custom_edit_text_onfocus
                    : R.drawable.ui_qc_bg_custom_edit_text;
            questionEditText.setBackgroundResource(backgroundRes);
            updateQuestionTitle();
        });
    }

    private void setupQuestionTypeButton() {
        questionTypeButton.setOnClickListener(v -> {
            QCHelper.showQuestionTypeBottomSheet2(
                    getContext(),
                    getActivity().getSupportFragmentManager(),
                    questionType -> {

                        String name = questionType.getName();
                        List<Question> questions = ((QCQuestionBNVFragment) getParentFragmentManager()
                                .findFragmentById(R.id.bottom_navigation_frame_container))
                                .getQuestions();

                        Question newQuestion = QCHelper.QuestionTypeMapper.createQuestionInstance2(questions, name);
                        newQuestion.setQuestion(getCurrentQuestion());
                        newQuestion.setQuestionType(questionType);

                        if (listener != null) {
                            listener.onUpdateQuestion(getCurrentQuestion().getPosition(), newQuestion);

                            if (getActivity() instanceof QuizCreateActivity) {
                                QuizCreateActivity activity = (QuizCreateActivity) getActivity();
                                activity.onClickListener(newQuestion);
                            }
                        }

                        Log.d("QCBaseQuestionFragment", "Question type changed to: " + name + " at position: " + getCurrentQuestion().getPosition());

                    }
            );
        });
    }

    protected void setupTimeLimitButton() {
        timeLimitButton.setText(getCurrentQuestion().getTimeLimit() + " sec");

        timeLimitButton.setOnClickListener(v -> {
            new QCGenericSelectionDialog.Builder(getContext())
                    .setItems(TIME_LIMITS)
                    .setHeaderTitle("Time Limit")
                    .setUnit("sec")
                    .setDefaultSelectedValue(getCurrentQuestion().getTimeLimit())
                    .setOnItemSelectedListener(selectedTime -> {
                        timeLimitButton.setText(selectedTime + " sec");
                        updateQuestionTime();
                    })
                    .build()
                    .show();
        });
    }

    protected void setupPointsButton() {
        pointButton.setText(getCurrentQuestion().getPoint() + " pt");

        pointButton.setOnClickListener(v -> {
            new QCGenericSelectionDialog.Builder(getContext())
                    .setItems(POINTS)
                    .setHeaderTitle("Points")
                    .setUnit("pt")
                    .setDefaultSelectedValue(getCurrentQuestion().getPoint())
                    .setOnItemSelectedListener(selectedPoints -> {
                        pointButton.setText(selectedPoints + " pt");
                        updateQuestionPoint();
                    })
                    .build()
                    .show();
        });
    }

    protected void onTimeLimitSelected(int timeLimit) {
        Toast.makeText(getContext(), "Selected: " + timeLimit + " seconds", Toast.LENGTH_SHORT).show();
    }

    protected void onPointsSelected(int points) {
        Toast.makeText(getContext(), "Selected: " + points + " points", Toast.LENGTH_SHORT).show();
    }

    private String filterDigit(String source) {
        StringBuilder target = new StringBuilder();
        for(int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if(Character.isDigit(c)) {
                target.append(c);
            }
        }
        return  target.toString();
    }

    protected void updateQuestionTitle() {
        getCurrentQuestion().setContent(questionEditText.getText().toString());
        notifyQuestionUpdated();
    }

    protected void updateQuestionPoint() {
        getCurrentQuestion().setPoint(Integer.parseInt(filterDigit(pointButton.getText().toString())));
        notifyQuestionUpdated();
    }

    protected void updateQuestionTime() {
        getCurrentQuestion().setTimeLimit(Integer.parseInt(filterDigit(timeLimitButton.getText().toString())));
        notifyQuestionUpdated();
    }

    protected void notifyQuestionUpdated() {
        if (listener != null) {
            listener.onUpdateQuestion(getCurrentQuestion().getPosition(), getCurrentQuestion());
        }
    }

    protected abstract void onCoverImageClicked();
    public abstract Question getCurrentQuestion();
//    public abstract int getCurrentPosition();

}