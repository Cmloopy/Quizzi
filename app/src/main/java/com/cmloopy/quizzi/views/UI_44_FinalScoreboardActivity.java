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
import com.cmloopy.quizzi.adapter.UI_44_LeaderboardAdapter;
import com.cmloopy.quizzi.models.UI_44_LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

public class UI_44_FinalScoreboardActivity extends AppCompatActivity {
    private RecyclerView rvLeaderboard;
    private UI_44_LeaderboardAdapter leaderboardAdapter; // Changed from UI_44_LeaderboardEntry
    private List<UI_44_LeaderboardEntry> leaderboardEntries;

    private ImageView ivBack;
    private Button btnSave, btnShare;

    // Top 3 players views
    private TextView tvFirstPlaceName, tvFirstPlaceScore,
            tvSecondPlaceName, tvSecondPlaceScore,
            tvThirdPlaceName, tvThirdPlaceScore;
    private ImageView ivFirstPlaceAvatar, ivSecondPlaceAvatar, ivThirdPlaceAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_44_final_scoreboard);

        initViews();
        setupTopPlayers();
        setupLeaderboard();
        setupClickListeners();
    }

    private void initViews() {
        // Leaderboard RecyclerView
        rvLeaderboard = findViewById(R.id.rvLeaderboard);

        // Back button
        ivBack = findViewById(R.id.ivBack);

        // Save and Share buttons
        btnSave = findViewById(R.id.btnSave);
        btnShare = findViewById(R.id.btnShare);

        // Top 3 players views
        initTopPlayerViews();
    }

    private void initTopPlayerViews() {
        // First Place
        tvFirstPlaceName = findViewById(R.id.tvFirstPlaceName);
        tvFirstPlaceScore = findViewById(R.id.tvFirstPlaceScore);
        ivFirstPlaceAvatar = findViewById(R.id.ivFirstPlaceAvatar);

        // Second Place
        tvSecondPlaceName = findViewById(R.id.tvSecondPlaceName);
        tvSecondPlaceScore = findViewById(R.id.tvSecondPlaceScore);
        ivSecondPlaceAvatar = findViewById(R.id.ivSecondPlaceAvatar);

        // Third Place
        tvThirdPlaceName = findViewById(R.id.tvThirdPlaceName);
        tvThirdPlaceScore = findViewById(R.id.tvThirdPlaceScore);
        ivThirdPlaceAvatar = findViewById(R.id.ivThirdPlaceAvatar);
    }

    private void setupTopPlayers() {
        // Set top 3 players data
        tvFirstPlaceName.setText("Pedro");
        tvFirstPlaceScore.setText("3,845");
        ivFirstPlaceAvatar.setImageResource(R.drawable.avt);

        tvSecondPlaceName.setText("Andrew");
        tvSecondPlaceScore.setText("3,456");
        ivSecondPlaceAvatar.setImageResource(R.drawable.avt);

        tvThirdPlaceName.setText("Freida");
        tvThirdPlaceScore.setText("3,178");
        ivThirdPlaceAvatar.setImageResource(R.drawable.avt);
    }

    private void setupLeaderboard() {
        // Create leaderboard entries
        leaderboardEntries = new ArrayList<>();
        leaderboardEntries.add(new UI_44_LeaderboardEntry(4, "Clinton", 2846, R.drawable.avt));
        leaderboardEntries.add(new UI_44_LeaderboardEntry(5, "Theresa", 2472, R.drawable.avt));
        leaderboardEntries.add(new UI_44_LeaderboardEntry(6, "Jamel", 2186, R.drawable.avt));
        leaderboardEntries.add(new UI_44_LeaderboardEntry(7, "Leif", 1956, R.drawable.avt));

        // Setup RecyclerView
        leaderboardAdapter = new UI_44_LeaderboardAdapter(this, leaderboardEntries); // Corrected this line
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        rvLeaderboard.setAdapter(leaderboardAdapter);
    }

    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> onBackPressed());

        // Save button
        btnSave.setOnClickListener(v -> {
            // Implement save functionality
            // This could be saving the score to local storage or sending to a server
        });

        // Share button
        btnShare.setOnClickListener(v -> {
            // Implement share functionality
            // This could open a share intent to share the score
            shareScore();
        });
    }

    private void shareScore() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "I just scored " + "3,845" + " in the Quiz App! Can you beat my score?";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Share your score"));
    }

    @Override
    public void onBackPressed() {
        // You might want to navigate to a specific screen
        // For now, we'll just finish the current activity
        super.onBackPressed(); // Added this line to resolve the "Overriding method should call 'super.onBackPressed'" warning
        finish();
    }
}