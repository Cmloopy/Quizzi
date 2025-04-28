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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI_44_Question_TrueFalse;

import java.util.ArrayList;
import java.util.List;

public class UI_44_PlayQuizQuestion2 extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout mainContainer;
    private TextView tvQuestionCount, tvQuestion, tvQuizTitle, tvResultTitle, tvResultMessage;
    private ImageView ivQuestionImage, ivMoreOptions;
    private CardView cardTrue, cardFalse, cardResultMessage;
    private Button btnNext;
    private ProgressBar progressBar;
    private ImageView ivTrueStatus, ivFalseStatus;
    private FrameLayout resultOverlay;

    private List<UI_44_Question_TrueFalse> questions;
    private int score = 0;
    private boolean answered = false;

    // Colors for state changes
    private int colorNormal;
    private int colorCorrect;
    private int colorIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_play_quiz_question2);

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
        cardTrue = findViewById(R.id.cardTrue);
        cardFalse = findViewById(R.id.cardFalse);
        cardResultMessage = findViewById(R.id.cardResultMessage);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);

        // Result overlay
        resultOverlay = findViewById(R.id.resultOverlay);

        // Status icons
        ivTrueStatus = findViewById(R.id.ivTrueStatus);
        ivFalseStatus = findViewById(R.id.ivFalseStatus);
    }

    private void setupColors() {
        colorNormal = Color.WHITE;
        colorCorrect = getResources().getColor(R.color.correct_green);
        colorIncorrect = getResources().getColor(R.color.incorrect_red);
    }

    private void setupQuestions() {
        questions = new ArrayList<>();

        // Add a single question about the book
        questions.add(new UI_44_Question_TrueFalse(
                "This is a book?\nTrue or False?",
                R.drawable.img,
                true // true answer
        ));
    }

    private void displayQuestion() {
        UI_44_Question_TrueFalse currentQuestion = questions.get(0); // Always show the first question

        // Reset UI to initial state
        resetUI();

        tvQuestionCount.setText("2/10");
        tvQuestion.setText(currentQuestion.getQuestionText());
        ivQuestionImage.setImageResource(currentQuestion.getImageResource());

        progressBar.setProgress(20); // Set progress bar to 20% as in the image
    }

    private void resetUI() {
        // Hide result views
        resultOverlay.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        // Reset background color
        mainContainer.setBackgroundColor(colorNormal);

        // Reset option cards
        cardTrue.setCardBackgroundColor(getResources().getColor(R.color.correct_green));
        cardFalse.setCardBackgroundColor(getResources().getColor(R.color.incorrect_red));

        // Hide all status icons
        ivTrueStatus.setVisibility(View.GONE);
        ivFalseStatus.setVisibility(View.GONE);

        // Reset text colors
        tvQuestionCount.setTextColor(Color.BLACK);
        tvQuestion.setTextColor(Color.BLACK);
        tvQuizTitle.setTextColor(Color.BLACK);

        // Re-enable option clicks
        answered = false;

        // Re-enable clickable state of option cards
        cardTrue.setClickable(true);
        cardFalse.setClickable(true);
    }

    private void setupClickListeners() {
        cardTrue.setOnClickListener(this);
        cardFalse.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivMoreOptions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (answered) {
            if (v.getId() == R.id.btnNext) {
                // Move to the next question or screen
                Intent intent = new Intent(UI_44_PlayQuizQuestion2.this, UI_44_PlayQuizQuestion3.class);
                startActivity(intent);
                finish(); // Close this activity
            }
            return;
        }

        // Handle answer selection
        boolean selectedAnswer = false;
        int viewId = v.getId();

        if (viewId == R.id.cardTrue) {
            selectedAnswer = true;
        } else if (viewId == R.id.cardFalse) {
            selectedAnswer = false;
        } else if (viewId == R.id.ivMoreOptions) {
            // Handle more options button
            showOptionsMenu();
            return;
        }

        checkAnswer(selectedAnswer);
    }

    private void showOptionsMenu() {
        // Show options menu (can be implemented later)
    }

    private void checkAnswer(boolean selectedAnswer) {
        answered = true;
        UI_44_Question_TrueFalse currentQuestion = questions.get(0);
        boolean isCorrect = selectedAnswer == currentQuestion.isCorrectAnswerTrue();

        // Disable clicking on other options after an answer is selected
        cardTrue.setClickable(false);
        cardFalse.setClickable(false);

        // Update score if correct
        if (isCorrect) {
            // Set a fixed score for the demo
            score += 945;
        }

        // Show result state with a slight delay for better UX
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showResultState(isCorrect, selectedAnswer);
        }, 300);
    }

    private void showResultState(boolean isCorrect, boolean selectedAnswer) {
        // Show the result overlay that covers the entire top area
        resultOverlay.setBackgroundColor(isCorrect ? colorCorrect : colorIncorrect);
        resultOverlay.setVisibility(View.VISIBLE);

        // Set the result title and message
        tvResultTitle.setText(isCorrect ? "Correct!" : "Incorrect!");
        tvResultMessage.setTextColor(isCorrect ? colorCorrect : colorIncorrect);
        tvResultMessage.setText(isCorrect ? "+945" : "That was close");

        // Show correct/incorrect UI for answer options
        if (isCorrect) {
            showCorrectAnswerUI(selectedAnswer);
        } else {
            showIncorrectAnswerUI(selectedAnswer);
        }

        // Show next button
        btnNext.setBackgroundResource(R.drawable.ui_44_button_rounded_purple);
        btnNext.setTextColor(Color.WHITE);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void showCorrectAnswerUI(boolean selectedAnswer) {
        // For True/False questions, highlight the selected answer (which is correct)
        if (selectedAnswer) { // True was selected (and is correct)
            cardTrue.setCardBackgroundColor(getResources().getColor(R.color.correct_green));
            ivTrueStatus.setImageResource(R.drawable.ui_44_ic_check);
            ivTrueStatus.setVisibility(View.VISIBLE);

            cardFalse.setCardBackgroundColor(getResources().getColor(R.color.incorrect_red));
            ivFalseStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivFalseStatus.setVisibility(View.VISIBLE);
        } else { // False was selected (and is correct)
            cardFalse.setCardBackgroundColor(getResources().getColor(R.color.correct_green));
            ivFalseStatus.setImageResource(R.drawable.ui_44_ic_check);
            ivFalseStatus.setVisibility(View.VISIBLE);

            cardTrue.setCardBackgroundColor(getResources().getColor(R.color.incorrect_red));
            ivTrueStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivTrueStatus.setVisibility(View.VISIBLE);
        }
    }

    private void showIncorrectAnswerUI(boolean selectedAnswer) {
        UI_44_Question_TrueFalse currentQuestion = questions.get(0);
        boolean correctAnswer = currentQuestion.isCorrectAnswerTrue();

        if (correctAnswer) { // True is the correct answer
            cardTrue.setCardBackgroundColor(getResources().getColor(R.color.correct_green));
            ivTrueStatus.setImageResource(R.drawable.ui_44_ic_check);
            ivTrueStatus.setVisibility(View.VISIBLE);

            cardFalse.setCardBackgroundColor(getResources().getColor(R.color.incorrect_red));
            ivFalseStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivFalseStatus.setVisibility(View.VISIBLE);
        } else { // False is the correct answer
            cardFalse.setCardBackgroundColor(getResources().getColor(R.color.correct_green));
            ivFalseStatus.setImageResource(R.drawable.ui_44_ic_check);
            ivFalseStatus.setVisibility(View.VISIBLE);

            cardTrue.setCardBackgroundColor(getResources().getColor(R.color.incorrect_red));
            ivTrueStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivTrueStatus.setVisibility(View.VISIBLE);
        }
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