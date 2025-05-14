package com.cmloopy.quizzi.views.playquiz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.playquiz.FinalBoardScoreAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.GamePlayApi;
import com.cmloopy.quizzi.databinding.ActivityFinalScoreboardBinding;
import com.cmloopy.quizzi.models.tracking.QuizGameTracking;
import com.cmloopy.quizzi.views.MainActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinalScoreboardActivity extends AppCompatActivity {
    private ActivityFinalScoreboardBinding binding;
    private int userId;
    private int quizId;
    GamePlayApi gamePlayApi = RetrofitClient.playGame();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinalScoreboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getIntExtra("userId",-1);
        quizId = getIntent().getIntExtra("quizId",-1);

        Call<List<QuizGameTracking>> call = gamePlayApi.getQuizTracking(quizId);
        call.enqueue(new Callback<List<QuizGameTracking>>() {
            @Override
            public void onResponse(Call<List<QuizGameTracking>> call, Response<List<QuizGameTracking>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<QuizGameTracking> res = response.body();
                    Collections.sort(res, new Comparator<QuizGameTracking>() {
                        @Override
                        public int compare(QuizGameTracking p1, QuizGameTracking p2) {
                            return Integer.compare(p2.getTotalPoints(), p1.getTotalPoints());
                        }
                    });
                    binding.tvFirstPlaceScore.setText(res.get(0).getTotalPoints()+ "");
                    binding.tvSecondPlaceScore.setText(res.get(1).getTotalPoints()+ "");
                    binding.tvThirdPlaceScore.setText(res.get(2).getTotalPoints()+ "");
                    res.remove(0);
                    res.remove(0);
                    res.remove(0);
                    binding.rvLeaderboard.setLayoutManager(new LinearLayoutManager(FinalScoreboardActivity.this));
                    binding.rvLeaderboard.setAdapter(new FinalBoardScoreAdapter(FinalScoreboardActivity.this, res));
                }
            }

            @Override
            public void onFailure(Call<List<QuizGameTracking>> call, Throwable t) {

            }
        });
        binding.ivBack.setOnClickListener(v->{
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("idUser", userId);
            startActivity(intent);
            finish();
        });
    }
}