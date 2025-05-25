package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.DetailTopCollectionAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.CollectionService;
import com.cmloopy.quizzi.models.DetailTopCollectionItem;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTopCollections extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DetailTopCollectionAdapter quizAdapter;
    private TextView tvTitle;
    private TextView tvQuizCount;
    private ImageView btnBack;
    private ImageView btnSearch;
    private ImageView imgBanner;
    private int collectionId = -1;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_collections);

        // Khởi tạo views theo ID trong layout
        recyclerView = findViewById(R.id.detailCollectionRecyclerQuiz);
        tvTitle = findViewById(R.id.detailCollectionTextTitle);
        tvQuizCount = findViewById(R.id.detailCollectionTextQuizCount);
        btnBack = findViewById(R.id.detailCollectionBtnBack);
        btnSearch = findViewById(R.id.detailCollectionBtnSearch);
        imgBanner = findViewById(R.id.detailCollectionImgBanner);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        // Thiết lập sự kiện click cho nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity hiện tại
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Lấy collectionId từ Intent
        if (getIntent() != null) {
            collectionId = getIntent().getIntExtra("collectionId", -1);
            userId = getIntent().getIntExtra("userId", -1);
        }
        // Nếu có collectionId, lấy thông tin chi tiết
        if (collectionId != -1) {
            fetchCollectionDetails(collectionId);
        }
    }

    private void fetchCollectionDetails(int collectionId) {
        CollectionService collectionService = RetrofitClient.getCollectionService();

        // Gọi API để lấy thông tin chi tiết của collection
        Call<QuizCollection> call = collectionService.getCollectionById(collectionId);

        call.enqueue(new Callback<QuizCollection>() {
            @Override
            public void onResponse(Call<QuizCollection> call, Response<QuizCollection> response) {

                if (response.isSuccessful() && response.body() != null) {
                    QuizCollection collection = response.body();
                    tvTitle.setText(collection.getCategory());
                    tvQuizCount.setText(collection.getQuizzes().size() + "Quizzo");

                    DetailTopCollectionAdapter adapter = new DetailTopCollectionAdapter(collection.getQuizzes(), userId);
                    recyclerView.setLayoutManager(new LinearLayoutManager(DetailTopCollections.this));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<QuizCollection> call, Throwable t) {

            }
        });
    }

}