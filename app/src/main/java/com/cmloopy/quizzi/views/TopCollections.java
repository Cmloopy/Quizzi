// File: com/cmloopy/quizzi/views/TopCollections.java
package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.Topcollection.CollectionService;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.models.TopCollectionsCategory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopCollections extends AppCompatActivity {
    private static final String TAG = "TopCollections";
    private RecyclerView recyclerView;
    private TopCollectionsCategoryAdapter adapter;
    private List<TopCollectionsCategory> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_collections);

        recyclerView = findViewById(R.id.TopCollectionsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setNestedScrollingEnabled(false);

        // Gọi API để lấy danh sách collection
        fetchCollections();
    }

    private void fetchCollections() {
        // Hiển thị thông báo đang tải
        Toast.makeText(this, "Loading collections...", Toast.LENGTH_SHORT).show();

        // Lấy instance của CollectionService
        CollectionService collectionService = RetrofitClient.getCollectionService();

        // Debug log URL sẽ được gọi
        Call<List<QuizCollection>> call = collectionService.getAllCollections();
        Log.d(TAG, "Fetching collections from URL: " + call.request().url().toString());

        // Gọi API
        call.enqueue(new Callback<List<QuizCollection>>() {
            @Override
            public void onResponse(Call<List<QuizCollection>> call, Response<List<QuizCollection>> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizCollection> collections = response.body();
                    Log.d(TAG, "API returned " + collections.size() + " collections");

                    // Chuyển đổi dữ liệu API thành TopCollectionsCategory
                    categoryList = convertToTopCollectionsCategory(collections);

                    // Thiết lập adapter
                    adapter = new TopCollectionsCategoryAdapter(TopCollections.this, categoryList);
                    recyclerView.setAdapter(adapter);

                    Toast.makeText(TopCollections.this, "Loaded " + collections.size() + " collections", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "API error: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }

                    Toast.makeText(TopCollections.this,
                            "Failed to load collections. Using sample data.",
                            Toast.LENGTH_SHORT).show();

                    // Sử dụng dữ liệu mẫu khi có lỗi
                    loadDummyData();
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollection>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);

                Toast.makeText(TopCollections.this,
                        "Network error: " + t.getMessage() + ". Using sample data.",
                        Toast.LENGTH_SHORT).show();

                // Sử dụng dữ liệu mẫu khi có lỗi
                loadDummyData();
            }
        });
    }

    private List<TopCollectionsCategory> convertToTopCollectionsCategory(List<QuizCollection> collections) {
        List<TopCollectionsCategory> categoryList = new ArrayList<>();

        for (QuizCollection collection : collections) {
            // Debug log
            Log.d(TAG, "Processing collection: ID=" + collection.getId() +
                    ", Category=" + collection.getCategory());

            // Tạo đối tượng TopCollectionsCategory
            TopCollectionsCategory category = new TopCollectionsCategory(
                    collection.getCategory(),
                    R.drawable.img_1
            );

            // Đặt collectionId
            category.setCollectionId(collection.getId());

            categoryList.add(category);
        }

        return categoryList;
    }

    private void loadDummyData() {
        categoryList = new ArrayList<>();
        categoryList.add(new TopCollectionsCategory("Education", R.drawable.img_02, 1));
        categoryList.add(new TopCollectionsCategory("Games", R.drawable.img_02, 2));
        categoryList.add(new TopCollectionsCategory("Business", R.drawable.img_02, 3));
        categoryList.add(new TopCollectionsCategory("Entertainment", R.drawable.img_02, 4));
        categoryList.add(new TopCollectionsCategory("Art", R.drawable.img_02, 5));
        categoryList.add(new TopCollectionsCategory("Plants", R.drawable.img_02, 6));
        categoryList.add(new TopCollectionsCategory("Finance", R.drawable.img_02, 7));
        categoryList.add(new TopCollectionsCategory("Food & Drink", R.drawable.img_02, 8));
        categoryList.add(new TopCollectionsCategory("Health", R.drawable.img_02, 9));
        categoryList.add(new TopCollectionsCategory("Kids", R.drawable.img_02, 10));

        adapter = new TopCollectionsCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);
    }
}