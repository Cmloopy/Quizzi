package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI_44_Question_Checkbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UI_44_PlayQuizQuestion7 extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout mainContainer;
    private TextView tvQuestionCount, tvQuestion, tvQuizTitle, tvResultTitle, tvResultMessage;
    private ImageView ivQuestionImage, ivMoreOptions;
    private LinearLayout checkboxContainer;
    private Button btnSubmitAnswer, btnNext;
    private ProgressBar progressBar;
    private FrameLayout resultOverlay;

    private List<UI_44_Question_Checkbox> questions;
    private List<CheckBox> checkBoxes;
    private int score = 0;
    private boolean answered = false;

    // Colors for state changes
    private int colorNormal;
    private int colorCorrect;
    private int colorIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_play_quiz_question7);

        initViews();
        setupQuestions();
        setupColors();
        displayQuestion();
        setupClickListeners();
    }

    private void initViews() {
        mainContainer = findViewById(R.id.mainContainer);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultMessage = findViewById(R.id.tvResultMessage);

        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        ivMoreOptions = findViewById(R.id.ivMoreOptions);

        checkboxContainer = findViewById(R.id.checkboxContainer);
        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);
        btnNext = findViewById(R.id.btnNext);

        progressBar = findViewById(R.id.progressBar);
        resultOverlay = findViewById(R.id.resultOverlay);

        // Initialize checkbox list
        checkBoxes = new ArrayList<>();
    }

    private void setupColors() {
        colorNormal = Color.WHITE;
        colorCorrect = getResources().getColor(R.color.correct_green);
        colorIncorrect = getResources().getColor(R.color.incorrect_red);
    }

    private void setupQuestions() {
        questions = new ArrayList<>();

        // Add a checkbox question about objects in the image
        questions.add(new UI_44_Question_Checkbox(
                "What are the objects in the picture above?",
                R.drawable.img, // You'll need to add this image
                Arrays.asList("Book", "Ruler", "Laptop", "Scissors")
        ));
    }

    private void displayQuestion() {
        final UI_44_Question_Checkbox currentQuestion = questions.get(0);

        // Reset UI to initial state
        resetUI();

        tvQuestionCount.setText("7/10");
        tvQuestion.setText(currentQuestion.getQuestionText());
        ivQuestionImage.setImageResource(currentQuestion.getImageResource());

        // Dynamically create checkboxes
        createCheckboxes(currentQuestion.getOptions());

        progressBar.setProgress(70); // Set progress bar to 70% as in the image
    }

    private void createCheckboxes(List<String> options) {
        // Clear any existing checkboxes
        checkboxContainer.removeAllViews();
        checkBoxes.clear();

        // Create checkboxes dynamically
        for (String option : options) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(option);
            checkBox.setTextColor(Color.BLACK);
            checkBox.setButtonTintList(getColorStateList(R.color.checkbox_tint));

            // Add layout parameters
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 16);
            checkBox.setLayoutParams(params);

            // Add to container and list
            checkboxContainer.addView(checkBox);
            checkBoxes.add(checkBox);
        }
    }

    private void resetUI() {
        // Hide result views
        resultOverlay.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        // Reset background color
        mainContainer.setBackgroundColor(colorNormal);

        // Re-enable answer submission
        answered = false;
        btnSubmitAnswer.setEnabled(true);

        // Uncheck all checkboxes
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
        }
    }

    private void setupClickListeners() {
        btnSubmitAnswer.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivMoreOptions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (answered) {
            if (v.getId() == R.id.btnNext) {
                // In a real app, this would go to the next question or result screen
                // For this example, we'll just finish the activity
                finish();
            }
            return;
        }

        int viewId = v.getId();
        if (viewId == R.id.btnSubmitAnswer) {
            checkAnswer();
        } else if (viewId == R.id.ivMoreOptions) {
            showOptionsMenu();
        }
    }

    private void showOptionsMenu() {
        // Show options menu (can be implemented later)
    }

    private void checkAnswer() {
        answered = true;
        final UI_44_Question_Checkbox currentQuestion = questions.get(0);

        // Determine correct answers (in this case, all options are correct)
        final boolean isCorrect = checkAnswerLogic(currentQuestion);

        // Disable answer submission
        btnSubmitAnswer.setEnabled(false);
        // Update score if correct
        if (isCorrect) {
            // Set a fixed score for the demo
            score += 945;
        }

        // Show result state with a slight delay for better UX
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showResultState(isCorrect);
            }
        }, 300);
    }

    private boolean checkAnswerLogic(UI_44_Question_Checkbox currentQuestion) {
        List<String> correctOptions = currentQuestion.getOptions();

        for (CheckBox checkBox : checkBoxes) {
            boolean isChecked = checkBox.isChecked();
            boolean shouldBeChecked = correctOptions.contains(checkBox.getText().toString());

            if (isChecked != shouldBeChecked) {
                return false;
            }
        }

        return true;
    }

    private void showResultState(boolean isCorrect) {
        // Show the result overlay that covers the entire top area
        resultOverlay.setBackgroundColor(isCorrect ? colorCorrect : colorIncorrect);
        resultOverlay.setVisibility(View.VISIBLE);

        // Set the result title and message
        tvResultTitle.setText(isCorrect ? "Correct!" : "Incorrect!");
        tvResultMessage.setTextColor(isCorrect ? colorCorrect : colorIncorrect);
        tvResultMessage.setText(isCorrect ? "+945" : "That was close");

        // Show next button
        btnNext.setBackgroundResource(R.drawable.ui_44_button_rounded_purple);
        btnNext.setTextColor(Color.WHITE);
        btnNext.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // Ask confirmation before leaving quiz
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Quiz?")
                .setMessage("Your progress will be lost. Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}