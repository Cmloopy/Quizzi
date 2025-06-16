package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.CollectionService;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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

public class DetailTopCollections extends AppCompatActivity {
    private static final String TAG = "DetailTopCollections";
    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private List<QuizResponse> originalQuizList;
    private List<QuizResponse> filteredQuizList;
    private TextView tvTitle;
    private TextView tvQuizCount;
    private ImageView btnBack;
    private ImageView imgBanner;
    private EditText searchEditText;
    private ImageView clearSearchButton;
    private TextView sortTextButton;
    private int collectionId = -1;
    private int userId = -1;
    private boolean isNewestFirst = true;
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_collections);

        initViews();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        setupEventListeners();

        if (getIntent() != null) {
            collectionId = getIntent().getIntExtra("collectionId", -1);
            userId = getIntent().getIntExtra("userId", -1);
            Log.d(TAG, "Received collection ID: " + collectionId + ", User ID: " + userId);
        }

        if (collectionId != -1) {
            fetchCollectionDetails(collectionId);
        } else {
            Toast.makeText(this, "No collection ID provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.detailCollectionRecyclerQuiz);
        tvTitle = findViewById(R.id.detailCollectionTextTitle);
        tvQuizCount = findViewById(R.id.detailCollectionTextQuizCount);
        btnBack = findViewById(R.id.detailCollectionBtnBack);
        imgBanner = findViewById(R.id.detailCollectionImgBanner);
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        sortTextButton = findViewById(R.id.sortTextButton);

        originalQuizList = new ArrayList<>();
        filteredQuizList = new ArrayList<>();

        // Initialize sort button text
        sortTextButton.setText(isNewestFirst ? "Newest" : "Oldest");
    }

    private void setupEventListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity hiện tại
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                performSearch(currentSearchQuery);
                // Show/hide clear button based on text length
                clearSearchButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear search button
        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                searchEditText.clearFocus();
                currentSearchQuery = "";
            }
        });

        // Sort button
        sortTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSortOrder();
            }
        });
    }

    private void performSearch(String query) {
        filteredQuizList.clear();

        if (query.isEmpty()) {
            filteredQuizList.addAll(originalQuizList);
        } else {
            String searchQuery = query.toLowerCase();
            for (QuizResponse quiz : originalQuizList) {
                boolean matches = false;

                // Search by title
                if (quiz.getTitle() != null &&
                        quiz.getTitle().toLowerCase().contains(searchQuery)) {
                    matches = true;
                }

                // Search by description
                if (!matches && quiz.getDescription() != null &&
                        quiz.getDescription().toLowerCase().contains(searchQuery)) {
                    matches = true;
                }

                if (matches) {
                    filteredQuizList.add(quiz);
                }
            }
        }

        // Apply sorting
        sortQuizzes(filteredQuizList);

        // Update UI
        updateQuizRecyclerView();
        updateQuizCount();
    }

    private void toggleSortOrder() {
        isNewestFirst = !isNewestFirst;

        // Update button text
        sortTextButton.setText(isNewestFirst ? "Newest" : "Oldest");

        // Apply search and sort
        performSearch(currentSearchQuery);

        Toast.makeText(this,
                "Sorted by " + (isNewestFirst ? "newest" : "oldest"),
                Toast.LENGTH_SHORT).show();
    }

    private void sortQuizzes(List<QuizResponse> quizzes) {
        Collections.sort(quizzes, new Comparator<QuizResponse>() {
            @Override
            public int compare(QuizResponse q1, QuizResponse q2) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());

                    // Try to get creation dates
                    String dateStr1 = q1.getCreatedAt() != null ? q1.getCreatedAt() : q1.getUpdatedAt();
                    String dateStr2 = q2.getCreatedAt() != null ? q2.getCreatedAt() : q2.getUpdatedAt();

                    Date date1 = null;
                    Date date2 = null;

                    if (dateStr1 != null) {
                        date1 = format.parse(dateStr1);
                    }
                    if (dateStr2 != null) {
                        date2 = format.parse(dateStr2);
                    }

                    // Handle null dates
                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return isNewestFirst ? 1 : -1;
                    if (date2 == null) return isNewestFirst ? -1 : 1;

                    // If newest first, sort descending (newer dates first)
                    // If oldest first, sort ascending (older dates first)
                    return isNewestFirst ? date2.compareTo(date1) : date1.compareTo(date2);
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date for sorting", e);
                    return 0;
                }
            }
        });
    }

    private void updateQuizRecyclerView() {
        // Create a new adapter instance with filtered list (like MyQuizzoFragment does)
        quizAdapter = new QuizAdapter(new ArrayList<>(filteredQuizList), userId);
        recyclerView.setAdapter(quizAdapter);

        Log.d(TAG, "Updated RecyclerView with " + filteredQuizList.size() + " quizzes");
    }

    private void updateQuizCount() {
        tvQuizCount.setText(filteredQuizList.size() + " Quizzo");
    }

    private void fetchCollectionDetails(int collectionId) {
        // Hiển thị thông báo đang tải
        Toast.makeText(this, "Loading collection details...", Toast.LENGTH_SHORT).show();

        // Lấy instance của CollectionService
        CollectionService collectionService = RetrofitClient.getCollectionService();

        // Gọi API để lấy thông tin chi tiết của collection
        Call<QuizCollection> call = collectionService.getCollectionById(collectionId);
        Log.d(TAG, "Fetching collection details from URL: " + call.request().url().toString());

        call.enqueue(new Callback<QuizCollection>() {
            @Override
            public void onResponse(Call<QuizCollection> call, Response<QuizCollection> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    QuizCollection collection = response.body();
                    Log.d(TAG, "Received collection: " + collection.getCategory());

                    // Hiển thị thông tin collection từ API
                    tvTitle.setText(collection.getCategory());

                    // Hiển thị ảnh bìa nếu có
                    if (collection.getCoverPhoto() != null && !collection.getCoverPhoto().isEmpty()) {
                        // Sử dụng Glide để tải và hiển thị ảnh
                        Glide.with(DetailTopCollections.this)
                                .load(collection.getCoverPhoto())
                                .placeholder(R.drawable.img_02)
                                .error(R.drawable.img_02)
                                .into(imgBanner);
                    } else {
                        // Sử dụng ảnh mặc định
                        imgBanner.setImageResource(R.drawable.img_02);
                    }

                    // Log thông tin collection
                    Log.d(TAG, "Collection description: " + collection.getDescription());
                    Log.d(TAG, "Collection author ID: " + collection.getAuthorId());
                    Log.d(TAG, "Collection timestamp: " + collection.getTimestamp());
                    Log.d(TAG, "Collection visibleTo: " + collection.isVisibleTo());

                    // Nếu collection có quizzes, setup RecyclerView
                    if (collection.getQuizzes() != null && !collection.getQuizzes().isEmpty()) {
                        setupQuizRecyclerView(collection.getQuizzes());
                    } else {
                        // Clear lists and update UI to show empty state
                        originalQuizList.clear();
                        filteredQuizList.clear();
                        updateQuizRecyclerView();
                        updateQuizCount();
                        Toast.makeText(DetailTopCollections.this,
                                "Collection has no quizzes.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API error: " + response.message());
                    Toast.makeText(DetailTopCollections.this,
                            "Failed to load collection details.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuizCollection> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                Toast.makeText(DetailTopCollections.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupQuizRecyclerView(List<QuizResponse> quizzes) {
        // Store original data
        originalQuizList.clear();
        originalQuizList.addAll(quizzes);

        // Apply initial search and sort
        performSearch(currentSearchQuery);

        Log.d(TAG, "Loaded " + originalQuizList.size() + " quizzes from collection");
    }
}