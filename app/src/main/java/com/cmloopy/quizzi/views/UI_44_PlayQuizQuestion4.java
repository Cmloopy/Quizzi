package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cmloopy.quizzi.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UI_44_PlayQuizQuestion4 extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout quizContainer, resultContainer;
    private TextView tvQuestionCount, tvQuestion, tvQuizTitle;
    private TextView tvYouAnswered, tvUserAnswer, tvCorrect, tvScoreResult;
    private TextView tvResultQuestion;
    private EditText etAnswer;
    private ImageView ivQuestionImage, ivMoreOptions, ivResultQuestionImage;
    private CardView cardAnswer, cardScoreResult;
    private Button btnSubmit, btnNext;
    private ProgressBar progressBar;

    private List<String> correctAnswers;

    private int score = 0;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_play_quiz_question4);

        initViews();
        setupCorrectAnswers();
        setupClickListeners();
        setupTextWatcher();
    }

    private void initViews() {
        // Quiz container views
        quizContainer = findViewById(R.id.quizContainer);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        etAnswer = findViewById(R.id.etAnswer);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        ivMoreOptions = findViewById(R.id.ivMoreOptions);
        cardAnswer = findViewById(R.id.cardAnswer);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        // Result container views
        resultContainer = findViewById(R.id.resultContainer);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvScoreResult = findViewById(R.id.tvScoreResult);
        ivResultQuestionImage = findViewById(R.id.ivResultQuestionImage);
        tvResultQuestion = findViewById(R.id.tvResultQuestion);
        tvYouAnswered = findViewById(R.id.tvYouAnswered);
        tvUserAnswer = findViewById(R.id.tvUserAnswer);
        cardScoreResult = findViewById(R.id.cardScoreResult);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupCorrectAnswers() {
        // Define the list of acceptable answers
        correctAnswers = new ArrayList<>(Arrays.asList(
                "saving", "savings", "save", "money", "piggy bank", "piggybank",
                "coin", "coins", "cash", "piggy", "pig"
        ));
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivMoreOptions.setOnClickListener(this);
    }

    private void setupTextWatcher() {
        // Add text watcher to make the input field more responsive
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show submit button whenever there's text
                if (s.length() > 0 && !answered) {
                    btnSubmit.setEnabled(true);
                } else {
                    btnSubmit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.btnSubmit) {
            checkAnswer();
        } else if (viewId == R.id.btnNext) {
            // Move to the next question or screen
            moveToNextQuestion();
        } else if (viewId == R.id.ivMoreOptions) {
            // Handle more options button
            showOptionsMenu();
        }
    }

    private void showOptionsMenu() {
        // Show options menu (can be implemented later)
    }

    private void checkAnswer() {
        if (answered) return;

        answered = true;

        // Get user's answer
        String userAnswer = etAnswer.getText().toString().trim();

        // Convert user answer to lowercase for case-insensitive comparison
        String normalizedAnswer = userAnswer.toLowerCase();

        // Check if the user's answer is in the list of correct answers
        boolean isCorrect = false;
        for (String answer : correctAnswers) {
            if (normalizedAnswer.equals(answer.toLowerCase())) {
                isCorrect = true;
                break;
            }
        }

        // Show result based on correctness
        if (isCorrect) {
            score += 2549;
            showCorrectResult(userAnswer);
        } else {
            showIncorrectResult(userAnswer);
        }
    }

    private void showIncorrectResult(String userAnswer) {
        // Hide quiz view and show result view
        quizContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.VISIBLE);

        // Set result content for incorrect answer
        tvCorrect.setText("Incorrect!");
        tvScoreResult.setText("0");
        tvUserAnswer.setText(userAnswer);
        tvCorrect.setTextColor(Color.RED);
    }

    private void showCorrectResult(String userAnswer) {
        // Hide quiz view and show result view
        quizContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.VISIBLE);

        // Set result content
        tvScoreResult.setText("+2549");
        tvUserAnswer.setText(userAnswer);
    }

    private void moveToNextQuestion() {
        // Navigate to the next question or finish the quiz
        Intent intent = new Intent(UI_44_PlayQuizQuestion4.this, UI_44_ScoreboardActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
        finish();
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