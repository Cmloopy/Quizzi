package com.cmloopy.quizzi.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cmloopy.quizzi.fragment.FillInBlankFragment;
import com.cmloopy.quizzi.fragment.MultipleChoiceFragment;
import com.cmloopy.quizzi.fragment.TrueFalseFragment;
import com.cmloopy.quizzi.models.Question;
import com.cmloopy.quizzi.models.Quiz;

import java.util.ArrayList;
import java.util.List;

public class PlayGameActivity extends AppCompatActivity {

    private TextView questionOrderTextView;
    private TextView quizNameTextView;
    private ImageButton moreOptionsButton;
    private ImageView questionImage;
    private TextView textQuestion;
    private ProgressBar progressTimer;
    private Button nextButton;

    private Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private CountDownTimer timer;
    private boolean answered = false;
    private long timeLeftInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        this.loadQuiz();


        // Ánh xạ các thành phần UI
        questionOrderTextView = findViewById(R.id.playing_game_question_order);
        quizNameTextView = findViewById(R.id.playing_game_quiz_name);
        moreOptionsButton = findViewById(R.id.text_right);
        progressTimer = findViewById(R.id.progress_timer);
        nextButton = findViewById(R.id.playing_game_btn);
        textQuestion = findViewById(R.id.question_text_area);

        // Thiết lập tên quiz
        quizNameTextView.setText(currentQuiz.getTitle());

        // Thiết lập sự kiện cho nút More Options
        moreOptionsButton.setOnClickListener(v -> showMoreOptions());

        // Thiết lập sự kiện cho nút Next
        nextButton.setOnClickListener(v -> {
            if (answered) {
                loadNextQuestion();
            } else {
                // Nếu chưa trả lời, xem như bỏ qua câu hỏi
                skipQuestion();
            }
        });

        // Bắt đầu với câu hỏi đầu tiên
        loadQuestion(currentQuestionIndex);
    }

    private void loadQuiz() {
        //currentQuiz = Quiz.CreateSampleData().get(0);
        //questions= currentQuiz.getQuestions();
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            finishQuiz();
            return;
        }

        // Cập nhật số thứ tự câu hỏi
        questionOrderTextView.setText((index + 1) + "/" + questions.size());

        // Hủy bỏ timer hiện tại nếu có
        if (timer != null) {
            timer.cancel();
        }

        // Reset trạng thái trả lời
        answered = false;

        // Load câu hỏi tương ứng
        Question currentQuestion = questions.get(index);

        // Dựa vào loại câu hỏi, hiển thị Fragment tương ứng
        Fragment fragment = MultipleChoiceFragment.newInstance(currentQuestion);
        switch (currentQuestion.getType()) {
            case "multiple_choice":
                fragment = MultipleChoiceFragment.newInstance(currentQuestion);
                break;
            case "true_false":
                fragment = TrueFalseFragment.newInstance(currentQuestion);
                break;

        }

        textQuestion.setText(currentQuestion.getQuestion());

        // Thay đổi nội dung trong FrameLayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.question_answer_area, fragment);
        transaction.commit();

        // Bắt đầu timer cho câu hỏi
        timeLeftInMillis = currentQuestion.getTime_limit()*10000;
        startTimer();

        // Cập nhật nút Next
        updateNextButtonState();
    }

    private void startTimer() {
        progressTimer.setProgress(100);

        timer = new CountDownTimer(timeLeftInMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int progress = (int) (millisUntilFinished * 100 / 30000);
                progressTimer.setProgress(progress);
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 10;
                progressTimer.setProgress(0);
                // Thời gian hết, tự động chuyển sang câu hỏi tiếp theo
                if (!answered) {
                    skipQuestion();
                }
            }
        }.start();
    }

    private void updateNextButtonState() {
        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next");
        }
    }

    private void loadNextQuestion() {
        currentQuestionIndex++;
        loadQuestion(currentQuestionIndex);
    }

    private void skipQuestion() {
        // Xử lý khi người dùng bỏ qua câu hỏi
        Toast.makeText(this, "Skipped question", Toast.LENGTH_SHORT).show();
        loadNextQuestion();
    }

    private void finishQuiz() {
        // Xử lý khi hoàn thành bài quiz
        Toast.makeText(this, "Quiz completed!", Toast.LENGTH_LONG).show();
        // TODO: Chuyển đến màn hình kết quả
        finish();
    }

    private void showMoreOptions() {
        // Hiển thị menu tùy chọn
        // TODO: Implement menu options (e.g., quit quiz, report question)
        Toast.makeText(this, "Options menu", Toast.LENGTH_SHORT).show();
    }

    // Phương thức này được gọi từ các Fragment khi người dùng trả lời câu hỏi
    public void onQuestionAnswered(boolean correct) {
        answered = true;
        if (timer != null) {
            timer.cancel();
        }

        // Thông báo kết quả
        if (correct) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
