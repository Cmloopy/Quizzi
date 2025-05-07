package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.R;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_with_search_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Discover");

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set adapter
        adapter = new QuizAdapter(Quiz.CreateSampleData());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}