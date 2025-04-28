package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.UI_44_ScoreboardAdapter;
import com.cmloopy.quizzi.models.UI_44_Player;

import java.util.ArrayList;
import java.util.List;

public class UI_44_ScoreboardActivity extends AppCompatActivity {

    private ImageView ivClose;
    private TextView tvScoreboardTitle;
    private RecyclerView recyclerScoreboard;
    private Button btnNext;

    private int userScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_score_board);

        // Get user score from intent
        userScore = getIntent().getIntExtra("SCORE", 945); // Default to 945 for the demo

        initViews();
        setupClickListeners();
        setupScoreboard();
    }

    private void initViews() {
        ivClose = findViewById(R.id.ivClose);
        tvScoreboardTitle = findViewById(R.id.tvScoreboardTitle);
        recyclerScoreboard = findViewById(R.id.recyclerScoreboard);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupClickListeners() {
        ivClose.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> {
            // Navigate to the next activity or finish this one
            // For demo purpose, we'll just finish this activity
            finish();
        });
    }

    private void setupScoreboard() {
        // Create mock data for the scoreboard
        List<UI_44_Player> players = createMockPlayers();

        // Setup RecyclerView
        recyclerScoreboard.setLayoutManager(new LinearLayoutManager(this));
        UI_44_ScoreboardAdapter adapter = new UI_44_ScoreboardAdapter(players, userScore);
        recyclerScoreboard.setAdapter(adapter);
    }

    private List<UI_44_Player> createMockPlayers() {
        List<UI_44_Player> players = new ArrayList<>();

        // Create sample players with scores
        players.add(new UI_44_Player(1, "Pedro", 984, R.drawable.avt));
        players.add(new UI_44_Player(2, "Andrew", 945, R.drawable.avt)); // This is our player with score 945
        players.add(new UI_44_Player(3, "Freida", 897, R.drawable.avt));
        players.add(new UI_44_Player(4, "Clinton", 863, R.drawable.avt));
        players.add(new UI_44_Player(5, "Leif", 829, R.drawable.avt));
        players.add(new UI_44_Player(6, "Jamel", 786, R.drawable.avt));
        players.add(new UI_44_Player(7, "Tyra", 772, R.drawable.avt));
        players.add(new UI_44_Player(8, "Theresa", 755, R.drawable.avt));

        return players;
    }
}