package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.adapter.RecommendAuthorAdapter;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.R;

import java.util.ArrayList;
import java.util.List;

public class RecommendAuthorActivity extends AppCompatActivity {

    private RecyclerView recommendedFriendsRecyclerView;
    private RecommendAuthorAdapter recommendedFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_author);
        Toolbar toolbar = findViewById(R.id.toolbar_with_search_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top Authors");


        recommendedFriendsRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recommendedFriendsRecyclerView.setLayoutManager(linearLayoutManager);
        setupRecommendedFriends();
    }

    private void setupRecommendedFriends() {
        List<RecommendUser> friendsList = new ArrayList<>();
        friendsList.add(new RecommendUser("Darron Kulikowski", "darronk", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Maryland Winkles", "mwinkles", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Lauralee Quintero", "lquintero", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", "aschuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Terry Johnson", "tjohnson", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Sarah Martinez", "smartinez", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Michael Lee", "mlee", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Jessica Smith", "jsmith", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Robert Williams", "rwilliams", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Emily Davis", "edavis", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("John Brown", "jbrown", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Julia Wilson", "jwilson", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("David Taylor", "dtaylor", R.drawable.ic_launcher_background));

        recommendedFriendsAdapter = new RecommendAuthorAdapter(friendsList);
        recommendedFriendsRecyclerView.setAdapter(recommendedFriendsAdapter);
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