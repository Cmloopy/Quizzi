// File: com/cmloopy/quizzi/views/TopCollections.java
package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuizCollectionAPI;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopCollections extends AppCompatActivity {
    private static final String TAG = "TopCollections";
    private RecyclerView recyclerView;
    private ImageView backBtn;
    private TopCollectionsCategoryAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_collections);
        backBtn = findViewById(R.id.TopCollectionsBackButton);

        recyclerView = findViewById(R.id.TopCollectionsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setNestedScrollingEnabled(false);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        userId = getIntent().getIntExtra("userId", -1);
        // Gọi API để lấy danh sách collection
        fetchCollections();
    }



    private void fetchCollections() {

        // Lấy instance của CollectionService
        QuizCollectionAPI quizCollectionAPI = RetrofitClient.getCollectionService();

        // Debug log URL sẽ được gọi
        Call<List<QuizCollection>> call = quizCollectionAPI.getAllCollections();

        // Gọi API
        call.enqueue(new Callback<List<QuizCollection>>() {
            @Override
            public void onResponse(Call<List<QuizCollection>> call, Response<List<QuizCollection>> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizCollection> collections = response.body();

                    adapter = new TopCollectionsCategoryAdapter(TopCollections.this, collections, userId);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollection>> call, Throwable t) {

            }
        });
    }
}