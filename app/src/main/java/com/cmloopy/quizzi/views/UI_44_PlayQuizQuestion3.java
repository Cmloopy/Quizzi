package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.UI_44_WordAdapter;
import com.cmloopy.quizzi.models.UI_44_Word;
import com.cmloopy.quizzi.utils.UI_44_ItemTouchHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UI_44_PlayQuizQuestion3 extends AppCompatActivity implements UI_44_WordAdapter.OnStartDragListener {

    private ConstraintLayout mainContainer;
    private TextView tvQuestionCount, tvQuestion, tvQuizTitle, tvResultTitle, tvResultMessage;
    private ImageView ivQuestionImage, ivMoreOptions;
    private RecyclerView recyclerViewWords;
    private CardView cardResultMessage;
    private Button btnSubmit, btnNext;
    private ProgressBar progressBar;
    private FrameLayout resultOverlay;

    private UI_44_WordAdapter wordAdapter;
    private ItemTouchHelper touchHelper;

    // Store word list
    private List<UI_44_Word> wordList;

    private int score = 0;
    private boolean answered = false;

    // Colors for state changes
    private int colorNormal;
    private int colorCorrect;
    private int colorIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_play_quiz_question3);

        initViews();
        setupColors();
        setupPuzzleWords();
        setupRecyclerView();
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

        // Puzzle container
        recyclerViewWords = findViewById(R.id.recyclerViewWords);

        cardResultMessage = findViewById(R.id.cardResultMessage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);

        // Result overlay
        resultOverlay = findViewById(R.id.resultOverlay);
    }

    private void setupColors() {
        colorNormal = Color.WHITE;
        colorCorrect = getResources().getColor(R.color.correct_green);
        colorIncorrect = getResources().getColor(R.color.incorrect_red);
    }

    private void setupPuzzleWords() {
        // Initialize the word list with original positions to maintain colors
        wordList = new ArrayList<>();
        wordList.add(new UI_44_Word("This", 0));
        wordList.add(new UI_44_Word("Calendar", 1));
        wordList.add(new UI_44_Word("Is", 2));
        wordList.add(new UI_44_Word("A", 3));
    }

    private void setupRecyclerView() {
        recyclerViewWords.setLayoutManager(new LinearLayoutManager(this));

        // Create adapter and set it to RecyclerView
        wordAdapter = new UI_44_WordAdapter(wordList, this);
        recyclerViewWords.setAdapter(wordAdapter);

        // Set up ItemTouchHelper for drag and drop
        ItemTouchHelper.Callback callback = new UI_44_ItemTouchHelper(wordAdapter.getItemTouchHelperAdapter());
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewWords);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(view -> checkAnswer());
        btnNext.setOnClickListener(view -> {
            // Navigate to the next screen
            Intent intent = new Intent(UI_44_PlayQuizQuestion3.this, UI_44_ScoreboardActivity.class);
            startActivity(intent);
            finish();
        });
        ivMoreOptions.setOnClickListener(view -> showOptionsMenu());
    }

    private void showOptionsMenu() {
        // Show options menu (can be implemented later)
        Toast.makeText(this, "Options menu", Toast.LENGTH_SHORT).show();
    }

    private void checkAnswer() {
        answered = true;

        // Get current word order
        List<String> currentOrder = wordAdapter.getCurrentWordOrder();

        // Correct order should be: "This Is A Calendar"
        List<String> correctOrder = Arrays.asList("This", "Is", "A", "Calendar");

        // Check if the current order matches the correct order
        boolean isCorrect = currentOrder.equals(correctOrder);

        // Update score if correct
        if (isCorrect) {
            score += 945;
        }

        showResultState(isCorrect);
    }

    private void showResultState(boolean isCorrect) {
        // Show the result overlay that covers the entire top area
        resultOverlay.setBackgroundColor(isCorrect ? colorCorrect : colorIncorrect);
        resultOverlay.setVisibility(View.VISIBLE);

        // Set the result title and message
        tvResultTitle.setText(isCorrect ? "Correct!" : "Incorrect!");
        tvResultMessage.setTextColor(isCorrect ? colorCorrect : colorIncorrect);
        tvResultMessage.setText(isCorrect ? "+945" : "That was close");

        // Hide the submit button and show the next button
        btnSubmit.setVisibility(View.GONE);
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