package com.cmloopy.quizzi.fragment.QuizCreate.before;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCGenericSelectionDialog;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCHelper;
import com.cmloopy.quizzi.views.QuizCreate.before.QuizCreateActivity;

import java.util.Arrays;
import java.util.List;

public abstract class QCBaseQuestionFragment extends Fragment {
    protected FrameLayout coverImageContainer;
    protected Button timeLimitButton;
    protected Button pointButton;
    protected Button questionTypeButton;
    protected EditText questionEditText;

    protected int defaultTimeLimit;
    protected int defaultPoint;

    protected static final String ARG_POSITION = "position";
    protected static final String ARG_ID = "id";
    protected static final String ARG_TITLE = "title";
    protected static final String ARG_IMAGE_URL = "image_url";
    protected static final String ARG_TIME = "time";
    protected static final String ARG_POINT = "point";

    protected  OnChangeListener listener;

    public interface OnChangeListener {
        void onUpdateQuestion(int position, Question question);
        void onDeleteQuestion(int position, Question question);
    }

    public void setListener(OnChangeListener listener) {
        this.listener = listener;
    }

    protected static Bundle createBaseBundle(Question question) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, question.getPosition());
        args.putString(ARG_ID, question.getId());
        args.putString(ARG_TITLE, question.getTitle());
        args.putString(ARG_IMAGE_URL, question.getImageUrl());
        args.putInt(ARG_TIME, question.getTime());
        args.putInt(ARG_POINT, question.getPoint());
        return args;
    }

    protected void initializeBaseQuestion(Question question, Bundle args) {
        question.setPosition(args.getInt(ARG_POSITION, 0));
        question.setId(args.getString(ARG_ID, ""));
        question.setTitle(args.getString(ARG_TITLE, ""));
        question.setImageUrl(args.getString(ARG_IMAGE_URL, ""));
        question.setTime(args.getInt(ARG_TIME, 10));
        question.setPoint(args.getInt(ARG_POINT, 200));
    }

    protected void setDefaultBaseQuestion(Question question) {
//        question.setPosition(0);
//        question.setId("");
//        question.setTitle("");
//        question.setImageUrl("");
//        question.setTime(10);
//        question.setPoint(200);
    }

    protected void setUpBaseView(Question question) {
        timeLimitButton.setText(String.format("%s sec", String.valueOf(question.getTime())));
        pointButton.setText(String.format("%s pt", String.valueOf(question.getPoint())));
        timeLimitButton.setAllCaps(false);
        pointButton.setAllCaps(false);
        questionEditText.setText(question.getTitle());
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
            QCHelper.showQuestionTypeBottomSheet(
                    getContext(),
                    getActivity().getSupportFragmentManager(),
                    questionType -> {
//                        QCHelper.navigateToQuestionCreation(
//                                getActivity().getSupportFragmentManager(),
//                                questionType
//                        )

                        Question currentQuestion = getCurrentQuestion();
                        int position = currentQuestion.getPosition();
                        String title = currentQuestion.getTitle();
                        String imageUrl = currentQuestion.getImageUrl();
                        int time = currentQuestion.getTime();
                        int point = currentQuestion.getPoint();

                        // Create new question of selected type
                        String name = questionType.getName();
                        List<Question> questions = ((QCQuestionBNVFragment) getParentFragmentManager()
                                .findFragmentById(R.id.bottom_navigation_frame_container))
                                .getQuestions();

                        // Create new question instance with the selected type
                        Question newQuestion = QCHelper.QuestionTypeMapper.createQuestionInstance(questions, name);

                        // Preserve existing question data
                        newQuestion.setPosition(position);
                        newQuestion.setTitle(title);
                        newQuestion.setImageUrl(imageUrl);
                        newQuestion.setTime(time);
                        newQuestion.setPoint(point);

                        // Update question in the list
                        if (listener != null) {
                            // Let the parent fragment know about the update
                            listener.onUpdateQuestion(position, newQuestion);

                            // Find the activity and navigate to the proper fragment
                            if (getActivity() instanceof QuizCreateActivity) {
                                QuizCreateActivity activity = (QuizCreateActivity) getActivity();
                                // This will trigger onClickListener in the activity
                                activity.onClickListener(newQuestion);
                            }
                        }

                        Log.d("QCBaseQuestionFragment", "Question type changed to: " + name + " at position: " + position);

                    }
            );
        });
    }

    protected void setupTimeLimitButton() {
        timeLimitButton.setText(getCurrentQuestion().getTime() + " sec");

        timeLimitButton.setOnClickListener(v -> {
            new QCGenericSelectionDialog.Builder(getContext())
                    .setItems(TIME_LIMITS)
                    .setHeaderTitle("Time Limit")
                    .setUnit("sec")
                    .setDefaultSelectedValue(getCurrentQuestion().getTime())
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
        getCurrentQuestion().setTitle(questionEditText.getText().toString());
        notifyQuestionUpdated();
    }

    protected void updateQuestionPoint() {
        getCurrentQuestion().setPoint(Integer.parseInt(filterDigit(pointButton.getText().toString())));
        notifyQuestionUpdated();
    }

    protected void updateQuestionTime() {
        getCurrentQuestion().setTime(Integer.parseInt(filterDigit(timeLimitButton.getText().toString())));
        notifyQuestionUpdated();
    }

    protected void notifyQuestionUpdated() {
        if (listener != null) {
            listener.onUpdateQuestion(getCurrentQuestion().getPosition(), getCurrentQuestion());
        }
    }

    protected abstract void onCoverImageClicked();
    public abstract Question getCurrentQuestion();


}