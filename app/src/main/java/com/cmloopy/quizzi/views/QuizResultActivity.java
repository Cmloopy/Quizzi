package com.cmloopy.quizzi.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class QuizResultActivity extends AppCompatActivity {

    private TextView scoreTextView;
    private TextView totalPointsTextView;
    private TextView percentageTextView;
    private TextView feedbackTextView;
    private Button homeButton;
    private Button reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        // Ánh xạ các View
        scoreTextView = findViewById(R.id.score_text_view);
        totalPointsTextView = findViewById(R.id.total_points_text_view);
        percentageTextView = findViewById(R.id.percentage_text_view);
        feedbackTextView = findViewById(R.id.feedback_text_view);
        homeButton = findViewById(R.id.home_button);
        reviewButton = findViewById(R.id.review_button);

        // Lấy dữ liệu Quiz từ Intent
        if (getIntent().hasExtra("quiz")) {
            Quiz quiz = (Quiz) getIntent().getSerializableExtra("quiz");
            displayResults(quiz);
        } else {
            // Xử lý trường hợp không có dữ liệu
            finish();
        }

        // Thiết lập sự kiện cho nút Home
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Thiết lập sự kiện cho nút Review
        reviewButton.setOnClickListener(v -> {
            // TODO: Chuyển đến màn hình xem lại câu trả lời

        });
    }

    private void displayResults(Quiz quiz) {
        int userScore = 10;
        int totalPoints = 20;
        int percentage = (totalPoints > 0) ? (userScore * 100 / totalPoints) : 0;

        // Hiển thị điểm số
        scoreTextView.setText(String.valueOf(userScore));
        totalPointsTextView.setText("/" + totalPoints);
        percentageTextView.setText(percentage + "%");

        // Hiển thị phản hồi dựa trên tỷ lệ phần trăm
        if (percentage >= 90) {
            feedbackTextView.setText("Excellent! You mastered this quiz!");
        } else if (percentage >= 75) {
            feedbackTextView.setText("Great job! You did very well!");
        } else if (percentage >= 60) {
            feedbackTextView.setText("Good work! You passed the quiz.");
        } else if (percentage >= 40) {
            feedbackTextView.setText("Not bad, but needs improvement.");
        } else {
            feedbackTextView.setText("Keep practicing. You'll do better next time!");
        }
    }
}