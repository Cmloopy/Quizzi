package com.cmloopy.quizzi.views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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
import com.cmloopy.quizzi.models.UI_44_Question;

import java.util.ArrayList;
import java.util.List;

public class UI_44_PlayQuizQuestion1 extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout mainContainer;
    private TextView tvQuestionCount, tvQuestion, tvQuizTitle, tvResultTitle, tvResultMessage;
    private ImageView ivQuestionImage, ivMoreOptions;
    private CardView cardHow, cardWhat, cardWhich, cardWhere, cardResultMessage;
    private Button btnNext;
    private ProgressBar progressBar;
    private ImageView ivHowStatus, ivWhatStatus, ivWhichStatus, ivWhereStatus;
    private FrameLayout resultOverlay;

    private List<UI_44_Question> questions;
    private int score = 0;
    private boolean answered = false;

    // Colors for state changes
    private int colorNormal;
    private int colorCorrect;
    private int colorIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_play_quiz_question1);

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
        cardHow = findViewById(R.id.cardHow);
        cardWhat = findViewById(R.id.cardWhat);
        cardWhich = findViewById(R.id.cardWhich);
        cardWhere = findViewById(R.id.cardWhere);
        cardResultMessage = findViewById(R.id.cardResultMessage);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);

        // Result overlay
        resultOverlay = findViewById(R.id.resultOverlay);

        // Status icons
        ivHowStatus = findViewById(R.id.ivHowStatus);
        ivWhatStatus = findViewById(R.id.ivWhatStatus);
        ivWhichStatus = findViewById(R.id.ivWhichStatus);
        ivWhereStatus = findViewById(R.id.ivWhereStatus);
    }

    private void setupColors() {
        colorNormal = Color.WHITE;
        colorCorrect = getResources().getColor(R.color.correct_green);      // #00CC66 (bright green)
        colorIncorrect = getResources().getColor(R.color.incorrect_red);    // #FF3B30 (bright red)
    }

    private void setupQuestions() {
        questions = new ArrayList<>();

        // Add a single question about school bus
        questions.add(new UI_44_Question(
                "....... do you get to school?\nby bus?",
                R.drawable.img,
                "How", // correct answer
                "What",
                "Which",
                "Where"
        ));
    }

    private void displayQuestion() {
        UI_44_Question currentQuestion = questions.get(0); // Always show the first question

        // Reset UI to initial state
        resetUI();

        tvQuestionCount.setText("1/10");
        tvQuestion.setText(currentQuestion.getQuestionText());
        ivQuestionImage.setImageResource(currentQuestion.getImageResource());

        progressBar.setProgress(10); // Set progress bar to 10% as in the image
    }

    private void resetUI() {
        // Hide result views
        resultOverlay.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        // Reset background color
        mainContainer.setBackgroundColor(colorNormal);

        // Reset option cards - use the colors from your images
        cardHow.setCardBackgroundColor(getResources().getColor(R.color.option_blue));    // #4080FF
        cardWhat.setCardBackgroundColor(getResources().getColor(R.color.option_red));    // #FF5252
        cardWhich.setCardBackgroundColor(getResources().getColor(R.color.option_orange)); // #FFA500
        cardWhere.setCardBackgroundColor(getResources().getColor(R.color.option_green)); // #00CC66

        // Hide all status icons
        ivHowStatus.setVisibility(View.GONE);
        ivWhatStatus.setVisibility(View.GONE);
        ivWhichStatus.setVisibility(View.GONE);
        ivWhereStatus.setVisibility(View.GONE);

        // Reset text colors
        tvQuestionCount.setTextColor(Color.BLACK);
        tvQuestion.setTextColor(Color.BLACK);
        tvQuizTitle.setTextColor(Color.BLACK);

        // Re-enable option clicks
        answered = false;

        // Re-enable clickable state of option cards
        cardHow.setClickable(true);
        cardWhat.setClickable(true);
        cardWhich.setClickable(true);
        cardWhere.setClickable(true);
    }

    private void setupClickListeners() {
        cardHow.setOnClickListener(this);
        cardWhat.setOnClickListener(this);
        cardWhich.setOnClickListener(this);
        cardWhere.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivMoreOptions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (answered) {
            if (v.getId() == R.id.btnNext) {
                // Go directly to scoreboard
                showScoreboard();
            }
            return;
        }

        // Handle answer selection
        String selectedAnswer = "";
        int viewId = v.getId();

        if (viewId == R.id.cardHow) {
            selectedAnswer = "How";
        } else if (viewId == R.id.cardWhat) {
            selectedAnswer = "What";
        } else if (viewId == R.id.cardWhich) {
            selectedAnswer = "Which";
        } else if (viewId == R.id.cardWhere) {
            selectedAnswer = "Where";
        } else if (viewId == R.id.ivMoreOptions) {
            // Handle more options button
            showOptionsMenu();
            return;
        }

        if (!selectedAnswer.isEmpty()) {
            checkAnswer(selectedAnswer);
        }
    }

    private void showOptionsMenu() {
        // Show options menu (can be implemented later)
    }

    private void checkAnswer(String selectedAnswer) {
        answered = true;
        UI_44_Question currentQuestion = questions.get(0);
        boolean isCorrect = selectedAnswer.equals(currentQuestion.getCorrectAnswer());

        // Disable clicking on other options after an answer is selected
        cardHow.setClickable(false);
        cardWhat.setClickable(false);
        cardWhich.setClickable(false);
        cardWhere.setClickable(false);

        // Update score if correct
        if (isCorrect) {
            // Set a fixed score for the demo, like +945 as in the image
            score += 945;
        }

        // Show result state with a slight delay for better UX
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showResultState(isCorrect, selectedAnswer);
        }, 300);
    }

    private void showResultState(boolean isCorrect, String selectedAnswer) {
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

    private void showCorrectAnswerUI(String selectedAnswer) {
        // In the image, all options are red except the correct one which is green
        // First set all non-selected cards to red with X icons

        // Update all cards to red
        cardHow.setCardBackgroundColor(getResources().getColor(R.color.option_red));
        cardWhat.setCardBackgroundColor(getResources().getColor(R.color.option_red));
        cardWhich.setCardBackgroundColor(getResources().getColor(R.color.option_red));
        cardWhere.setCardBackgroundColor(getResources().getColor(R.color.option_red));

        // Show X icons on all cards except the selected one (which is correct)
        if (!selectedAnswer.equals("How")) {
            ivHowStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivHowStatus.setVisibility(View.VISIBLE);
        }

        if (!selectedAnswer.equals("What")) {
            ivWhatStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivWhatStatus.setVisibility(View.VISIBLE);
        }

        if (!selectedAnswer.equals("Which")) {
            ivWhichStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivWhichStatus.setVisibility(View.VISIBLE);
        }

        if (!selectedAnswer.equals("Where")) {
            ivWhereStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivWhereStatus.setVisibility(View.VISIBLE);
        }

        // Set the correct answer card (selected card) to green with a checkmark
        CardView selectedCard = getCardByAnswer(selectedAnswer);
        ImageView statusIcon = getStatusIconByAnswer(selectedAnswer);

        if (selectedCard != null && statusIcon != null) {
            selectedCard.setCardBackgroundColor(getResources().getColor(R.color.correct_green));
            statusIcon.setImageResource(R.drawable.ui_44_ic_check);
            statusIcon.setVisibility(View.VISIBLE);
        }
    }

    private void showIncorrectAnswerUI(String selectedAnswer) {
        UI_44_Question currentQuestion = questions.get(0);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        // In the image, all options are red except the correct one which is green
        // First set all cards to red
        cardHow.setCardBackgroundColor(getResources().getColor(R.color.option_red));
        cardWhat.setCardBackgroundColor(getResources().getColor(R.color.option_red));
        cardWhich.setCardBackgroundColor(getResources().getColor(R.color.option_red));
        cardWhere.setCardBackgroundColor(getResources().getColor(R.color.option_red));

        // Display X icons on all cards except the correct one
        if (!correctAnswer.equals("How")) {
            ivHowStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivHowStatus.setVisibility(View.VISIBLE);
        }

        if (!correctAnswer.equals("What")) {
            ivWhatStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivWhatStatus.setVisibility(View.VISIBLE);
        }

        if (!correctAnswer.equals("Which")) {
            ivWhichStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivWhichStatus.setVisibility(View.VISIBLE);
        }

        if (!correctAnswer.equals("Where")) {
            ivWhereStatus.setImageResource(R.drawable.ui_44_ic_cross);
            ivWhereStatus.setVisibility(View.VISIBLE);
        }

        // Set the correct answer card to green with a checkmark
        CardView correctCard = getCardByAnswer(correctAnswer);
        ImageView correctIcon = getStatusIconByAnswer(correctAnswer);

        if (correctCard != null && correctIcon != null) {
            correctCard.setCardBackgroundColor(getResources().getColor(R.color.correct_green));
            correctIcon.setImageResource(R.drawable.ui_44_ic_check);
            correctIcon.setVisibility(View.VISIBLE);
        }
    }

    private CardView getCardByAnswer(String answer) {
        switch (answer) {
            case "How":
                return cardHow;
            case "What":
                return cardWhat;
            case "Which":
                return cardWhich;
            case "Where":
                return cardWhere;
            default:
                return null;
        }
    }

    private ImageView getStatusIconByAnswer(String answer) {
        switch (answer) {
            case "How":
                return ivHowStatus;
            case "What":
                return ivWhatStatus;
            case "Which":
                return ivWhichStatus;
            case "Where":
                return ivWhereStatus;
            default:
                return null;
        }
    }

    private void showScoreboard() {
        // Navigate to scoreboard activity and pass the user's score
        Intent intent = new Intent(UI_44_PlayQuizQuestion1.this, UI_44_ScoreboardActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
        finish(); // Close this activity
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