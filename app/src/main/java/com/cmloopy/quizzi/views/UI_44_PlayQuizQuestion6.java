package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI_44_Question_Slider;

import java.util.ArrayList;
import java.util.List;

public class UI_44_PlayQuizQuestion6 extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout mainContainer;
    private TextView tvQuestionCount, tvQuestion, tvQuizTitle, tvResultTitle, tvResultMessage, tvSliderValue;
    private ImageView ivQuestionImage, ivMoreOptions;
    private SeekBar sliderAnswer;
    private Button btnSubmitAnswer, btnNext;
    private ProgressBar progressBar;
    private FrameLayout resultOverlay;

    private List<UI_44_Question_Slider> questions;
    private int score = 0;
    private boolean answered = false;

    // Colors for state changes
    private int colorNormal;
    private int colorCorrect;
    private int colorIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_play_quiz_question6);

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
        tvSliderValue = findViewById(R.id.tvSliderValue);

        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        ivMoreOptions = findViewById(R.id.ivMoreOptions);

        sliderAnswer = findViewById(R.id.sliderAnswer);
        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);
        btnNext = findViewById(R.id.btnNext);

        progressBar = findViewById(R.id.progressBar);
        resultOverlay = findViewById(R.id.resultOverlay);
    }

    private void setupColors() {
        colorNormal = Color.WHITE;
        colorCorrect = getResources().getColor(R.color.correct_green);
        colorIncorrect = getResources().getColor(R.color.incorrect_red);
    }

    private void setupQuestions() {
        questions = new ArrayList<>();

        // Add a slider question about the pen price
        questions.add(new UI_44_Question_Slider(
                "What is the price of the pen above?",
                R.drawable.img, // You'll need to add this image
                5, // Correct answer
                10 // Max slider value
        ));
    }

    private void displayQuestion() {
        UI_44_Question_Slider currentQuestion = questions.get(0);

        // Reset UI to initial state
        resetUI();

        tvQuestionCount.setText("6/10");
        tvQuestion.setText(currentQuestion.getQuestionText());
        ivQuestionImage.setImageResource(currentQuestion.getImageResource());

        // Setup slider
        sliderAnswer.setMax(currentQuestion.getMaxSliderValue());
        sliderAnswer.setProgress(0);
        tvSliderValue.setText("0");

        progressBar.setProgress(60); // Set progress bar to 60% as in the image
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
    }

    private void setupClickListeners() {
        btnSubmitAnswer.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivMoreOptions.setOnClickListener(this);

        // Update slider value display
        sliderAnswer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSliderValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onClick(View v) {
        if (answered) {
            if (v.getId() == R.id.btnNext) {
                // Move to the next question or screen
                Intent intent = new Intent(UI_44_PlayQuizQuestion6.this, UI_44_PlayQuizQuestion7.class);
                startActivity(intent);
                finish(); // Close this activity
            }
            return;
        }

        int viewId = v.getId();
        if (viewId == R.id.btnSubmitAnswer) {
            checkAnswer(sliderAnswer.getProgress());
        } else if (viewId == R.id.ivMoreOptions) {
            showOptionsMenu();
        }
    }

    private void showOptionsMenu() {
        // Show options menu (can be implemented later)
    }

    private void checkAnswer(int selectedValue) {
        answered = true;
        UI_44_Question_Slider currentQuestion = questions.get(0);
        boolean isCorrect = selectedValue == currentQuestion.getCorrectAnswer();

        // Disable answer submission
        btnSubmitAnswer.setEnabled(false);

        // Update score if correct
        if (isCorrect) {
            // Set a fixed score for the demo
            score += 1125;
        }

        // Show result state with a slight delay for better UX
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showResultState(isCorrect, selectedValue);
        }, 300);
    }

    private void showResultState(boolean isCorrect, int selectedValue) {
        // Show the result overlay that covers the entire top area
        resultOverlay.setBackgroundColor(isCorrect ? colorCorrect : colorIncorrect);
        resultOverlay.setVisibility(View.VISIBLE);

        // Set the result title and message
        tvResultTitle.setText(isCorrect ? "Correct!" : "Incorrect!");
        tvResultMessage.setTextColor(isCorrect ? colorCorrect : colorIncorrect);
        tvResultMessage.setText(isCorrect ? "+1125" : "That was close");

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