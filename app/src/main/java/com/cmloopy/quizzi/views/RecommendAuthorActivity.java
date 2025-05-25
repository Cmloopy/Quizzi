package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.adapter.RecommendAuthorAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.user.User;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendAuthorActivity extends AppCompatActivity {

    private RecyclerView recommendedFriendsRecyclerView;
    private RecommendAuthorAdapter recommendedFriendsAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_author);
        Toolbar toolbar = findViewById(R.id.toolbar_with_search_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top Authors");
        userId = getIntent().getIntExtra("userId",-1);

        recommendedFriendsRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recommendedFriendsRecyclerView.setLayoutManager(linearLayoutManager);

        recommendedFriendsAdapter = new RecommendAuthorAdapter(Collections.emptyList(), userId);
        recommendedFriendsRecyclerView.setAdapter(recommendedFriendsAdapter);

        loadAuthors();
    }
    private void loadAuthors() {
        Call<List<User>> call = RetrofitClient.getUserApi().getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();
                recommendedFriendsAdapter.setData(users);
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                recommendedFriendsAdapter.setData(Collections.emptyList());
            }
        });
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