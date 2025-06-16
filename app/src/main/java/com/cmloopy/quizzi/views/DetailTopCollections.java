package com.cmloopy.quizzi.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCMenuAdapter;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCMenuItem;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuizCollectionAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.models.quiz.QuizResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTopCollections extends AppCompatActivity {
    private static final String TAG = "DetailTopCollections";
    private static final int UPDATE_COLLECTION_REQUEST_CODE = 1002;

    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private List<QuizResponse> originalQuizList;
    private List<QuizResponse> filteredQuizList;
    private TextView tvTitle;
    private TextView tvQuizCount;
    private ImageView btnBack;
    private ImageView btnEdit;
    private ImageView imgBanner;
    private EditText searchEditText;
    private ImageView clearSearchButton;
    private TextView sortTextButton;
    private ProgressDialog progressDialog;

    private int collectionId = -1;
    private int userId = -1;
    private boolean isNewestFirst = true;
    private String currentSearchQuery = "";
    private QuizCollection quizCollection;

    // API clients
    QuizCollectionAPI quizCollectionAPI = RetrofitClient.getCollectionService();
    QuizAPI quizAPI = RetrofitClient.getQuizApi();

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
        btnEdit = findViewById(R.id.btnEdit);
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
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Edit/Options button - shows popup menu
        if (btnEdit != null) {
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Options button clicked");
                    showPopupMenu(v);
                }
            });
        } else {
            Log.e(TAG, "btnEdit is null - check if ID exists in layout");
        }

        // Search functionality
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

    private void showPopupMenu(View anchorView) {
        if (quizCollection == null) {
            Log.e(TAG, "Collection data not loaded yet");
            return;
        }

        // Check if user is the owner of the collection
        if (userId != quizCollection.getAuthorId()) {
            Log.d(TAG, "User is not collection owner, menu not displayed");
            return;
        }

        View popupView = LayoutInflater.from(this).inflate(R.layout.ui_qc_custom_menu, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(10);

        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Collection-specific menu items
        List<QCMenuItem> menuItems = Arrays.asList(
                new QCMenuItem("Edit Collection", R.drawable.ic_edit_dark, false),
                new QCMenuItem("Delete Collection", R.drawable.ic_78_delete, false)
        );

        QCMenuAdapter adapter = new QCMenuAdapter(menuItems, item -> {
            popupWindow.dismiss();
            handleMenuClick(item);
        });
        recyclerView.setAdapter(adapter);

        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private void handleMenuClick(QCMenuItem item) {
        switch (item.getTitle()) {
            case "Edit Collection":
                openEditCollection();
                break;
            case "Delete Collection":
                deleteCollection();
                break;
            default:
                Log.d(TAG, "Unknown menu item: " + item.getTitle());
                break;
        }
    }

    private void openEditCollection() {
         Intent intent = new Intent(DetailTopCollections.this, UpdateCollectionActivity.class);
         intent.putExtra("collectionId", collectionId);
         intent.putExtra("userId", userId);
         startActivityForResult(intent, UPDATE_COLLECTION_REQUEST_CODE);

        Toast.makeText(this, "Edit Collection feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void deleteCollection() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Collection")
                .setMessage("Are you sure you want to delete this collection? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performCollectionDeletion();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performCollectionDeletion() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting collection...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<Void> call = quizAPI.deleteQuizCollection((long) collectionId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(DetailTopCollections.this, "Collection deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("collection_deleted", true);
                    resultIntent.putExtra("collectionId", collectionId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(DetailTopCollections.this, "Failed to delete collection", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Delete failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "Delete collection error: " + t.getMessage());
                Toast.makeText(DetailTopCollections.this, "Network error during deletion", Toast.LENGTH_SHORT).show();
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
        // Create a new adapter instance with filtered list
        quizAdapter = new QuizAdapter(new ArrayList<>(filteredQuizList), userId);
        recyclerView.setAdapter(quizAdapter);

        Log.d(TAG, "Updated RecyclerView with " + filteredQuizList.size() + " quizzes");
    }

    private void updateQuizCount() {
        tvQuizCount.setText(filteredQuizList.size() + " Quizzo");
    }

    private void fetchCollectionDetails(int collectionId) {
        // Display loading message
        Toast.makeText(this, "Loading collection details...", Toast.LENGTH_SHORT).show();

        // Call API to get collection details
        Call<QuizCollection> call = quizCollectionAPI.getCollectionById(collectionId);
        Log.d(TAG, "Fetching collection details from URL: " + call.request().url().toString());

        call.enqueue(new Callback<QuizCollection>() {
            @Override
            public void onResponse(Call<QuizCollection> call, Response<QuizCollection> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    quizCollection = response.body();
                    Log.d(TAG, "Received collection: " + quizCollection.getCategory());

                    // Display collection info from API
                    tvTitle.setText(quizCollection.getCategory());

                    // Display cover photo if available
                    if (quizCollection.getCoverPhoto() != null && !quizCollection.getCoverPhoto().isEmpty()) {
                        // Use Glide to load and display image
                        Glide.with(DetailTopCollections.this)
                                .load(quizCollection.getCoverPhoto())
                                .placeholder(R.drawable.img_02)
                                .error(R.drawable.img_02)
                                .into(imgBanner);
                    } else {
                        // Use default image
                        imgBanner.setImageResource(R.drawable.img_02);
                    }

                    // Log collection info
                    Log.d(TAG, "Collection description: " + quizCollection.getDescription());
                    Log.d(TAG, "Collection author ID: " + quizCollection.getAuthorId());
                    Log.d(TAG, "Collection timestamp: " + quizCollection.getTimestamp());
                    Log.d(TAG, "Collection visibleTo: " + quizCollection.isVisibleTo());

                    // If collection has quizzes, setup RecyclerView
                    if (quizCollection.getQuizzes() != null && !quizCollection.getQuizzes().isEmpty()) {
                        setupQuizRecyclerView(quizCollection.getQuizzes());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_COLLECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean collectionUpdated = data.getBooleanExtra("collection_updated", false);
            if (collectionUpdated) {
                Toast.makeText(this, "Collection updated successfully", Toast.LENGTH_SHORT).show();
                // Reload collection data
                fetchCollectionDetails(collectionId);
            }
        }
    }
}