package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.quiz.QuizResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoveryActivity extends AppCompatActivity {

    private static final String TAG = "DiscoveryActivity";

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private ProgressBar progressBar;
    private QuizzApi quizApi;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        userId = getIntent().getIntExtra("userId",-1);
        Toolbar toolbar = findViewById(R.id.toolbar_with_search_view);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Discover");
        }
        progressBar = findViewById(R.id.progressBar);
        quizApi = RetrofitClient.getQuizzApi();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new QuizAdapter(Collections.emptyList(), userId);
        recyclerView.setAdapter(adapter);

        fetchQuizzes();
    }

    private void fetchQuizzes() {
        showLoading(true);

        Call<List<QuizResponse>> call = quizApi.getAllQuiz();
        call.enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> allQuizzes = response.body();
                    adapter.setData(allQuizzes);
                }
            }
            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                showLoading(false);
                adapter.setData(Collections.emptyList());
            }
        });
    }


    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}