package com.cmloopy.quizzi.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.adapter.RecommendUserAdapter;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.R;

import java.util.ArrayList;
import java.util.List;

public class RecommendUserActivity extends AppCompatActivity {

    private RecyclerView recommendedFriendsRecyclerView;
    private RecommendUserAdapter recommendedFriendsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_user);

        Toolbar toolbar = findViewById(R.id.toolbar_with_search_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        recommendedFriendsRecyclerView = findViewById(R.id.recyclerView);
        recommendedFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupRecommendedFriends();
    }

    private void setupRecommendedFriends() {
        List<RecommendUser> friendsList = new ArrayList<>();
        friendsList.add(new RecommendUser("Darron Kulikowski", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Maryland Winkles", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Lauralee Quintero", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));

        recommendedFriendsAdapter = new RecommendUserAdapter(friendsList);
        recommendedFriendsRecyclerView.setAdapter(recommendedFriendsAdapter);
    }
}