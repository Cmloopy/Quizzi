package com.cmloopy.quizzi.views.playquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.GamePlayApi;
import com.cmloopy.quizzi.databinding.ActivityReviewBinding;
import com.cmloopy.quizzi.models.tracking.QuizTrackingResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    private ActivityReviewBinding binding;
    private int userId;
    private long quizId;
    private int totalPoint;

    GamePlayApi gamePlayApi = RetrofitClient.playGame();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userId = getIntent().getIntExtra("userId",-1);
        quizId = getIntent().getLongExtra("quizId",-1);
        totalPoint = getIntent().getIntExtra("totalPoint",-1);

        binding.textView2.setText("Your TotalScore: " + (totalPoint+ ""));

        QuizTrackingResponse quizTrackingResponse = new QuizTrackingResponse(quizId, userId, totalPoint, 0, 0, 0);
        Call<QuizTrackingResponse> call = gamePlayApi.saveTotalPoint(quizTrackingResponse);
        call.enqueue(new Callback<QuizTrackingResponse>() {
            @Override
            public void onResponse(Call<QuizTrackingResponse> call, Response<QuizTrackingResponse> response) {
                if(response.isSuccessful()&&response.body() != null){
                    Intent intent = new Intent(ReviewActivity.this, FinalScoreboardActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("quizId", quizId);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        startActivity(intent);
                        finish();
                    }, 3000);
                }
            }
            @Override
            public void onFailure(Call<QuizTrackingResponse> call, Throwable t) {

            }
        });
    }
}