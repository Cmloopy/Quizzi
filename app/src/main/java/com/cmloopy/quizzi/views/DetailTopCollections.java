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
import android.widget.LinearLayout;
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
    private static final int QUIZ_DETAILS_REQUEST_CODE = 1003;

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

    // No quiz found section
    private LinearLayout noQuizContainer;
    private ImageView noQuizIcon;
    private TextView noQuizMessage;

    private int collectionId = -1;
    private int userId = -1;
    private boolean isNewestFirst = true;
    private String currentSearchQuery = "";
    private QuizCollection quizCollection;

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

        // Initialize no quiz found section
        noQuizContainer = findViewById(R.id.noQuizContainer);
        noQuizIcon = findViewById(R.id.noQuizIcon);
        noQuizMessage = findViewById(R.id.noQuizMessage);

        originalQuizList = new ArrayList<>();
        filteredQuizList = new ArrayList<>();

        sortTextButton.setText(isNewestFirst ? "Newest" : "Oldest");
    }

    private void setupEventListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                performSearch(currentSearchQuery);
                clearSearchButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        clearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                searchEditText.clearFocus();
                currentSearchQuery = "";
            }
        });

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

        Call<Void> call = quizAPI.deleteQuizCollection((long) quizCollection.getId());
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
                    Log.e(TAG, "Delete failed with code: " + response.code() + " Quiz collection id" + quizCollection.getId() + " - " + collectionId);
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

                if (quiz.getTitle() != null &&
                        quiz.getTitle().toLowerCase().contains(searchQuery)) {
                    matches = true;
                }

                if (!matches && quiz.getDescription() != null &&
                        quiz.getDescription().toLowerCase().contains(searchQuery)) {
                    matches = true;
                }

                if (matches) {
                    filteredQuizList.add(quiz);
                }
            }
        }

        sortQuizzes(filteredQuizList);
        updateQuizRecyclerView();
        updateQuizCount();
        updateNoQuizVisibility();
    }

    private void toggleSortOrder() {
        isNewestFirst = !isNewestFirst;

        sortTextButton.setText(isNewestFirst ? "Newest" : "Oldest");

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

                    if (date1 == null && date2 == null) return 0;
                    if (date1 == null) return isNewestFirst ? 1 : -1;
                    if (date2 == null) return isNewestFirst ? -1 : 1;

                    return isNewestFirst ? date2.compareTo(date1) : date1.compareTo(date2);
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date for sorting", e);
                    return 0;
                }
            }
        });
    }

    private void updateQuizRecyclerView() {
        quizAdapter = new QuizAdapter(new ArrayList<>(filteredQuizList), userId);
        recyclerView.setAdapter(quizAdapter);

        Log.d(TAG, "Updated RecyclerView with " + filteredQuizList.size() + " quizzes");
    }

    private void updateQuizCount() {
        tvQuizCount.setText(filteredQuizList.size() + " Quizzo");
    }

    private void updateNoQuizVisibility() {
        boolean hasQuizzes = !filteredQuizList.isEmpty();

        // Show/hide RecyclerView and No Quiz section
        recyclerView.setVisibility(hasQuizzes ? View.VISIBLE : View.GONE);
        noQuizContainer.setVisibility(hasQuizzes ? View.GONE : View.VISIBLE);

        // Update message and icon based on the situation
        if (!hasQuizzes) {
            if (!currentSearchQuery.isEmpty()) {
                noQuizIcon.setImageResource(R.drawable.ic_empty_questions);
                noQuizMessage.setText("No quizzes found matching \"" + currentSearchQuery + "\"");
            } else if (originalQuizList.isEmpty()) {
                noQuizIcon.setImageResource(R.drawable.ic_empty_questions);
                noQuizMessage.setText("This collection doesn't have any quizzes yet");
            } else {
                noQuizIcon.setImageResource(R.drawable.ic_empty_questions);
                noQuizMessage.setText("No quizzes available");
            }
        }

        Log.d(TAG, "No quiz visibility updated - hasQuizzes: " + hasQuizzes + ", searchQuery: '" + currentSearchQuery + "'");
    }

    private void fetchCollectionDetails(int collectionId) {
        Toast.makeText(this, "Loading collection details...", Toast.LENGTH_SHORT).show();

        Call<QuizCollection> call = quizCollectionAPI.getCollectionById(collectionId);
        Log.d(TAG, "Fetching collection details from URL: " + call.request().url().toString());

        call.enqueue(new Callback<QuizCollection>() {
            @Override
            public void onResponse(Call<QuizCollection> call, Response<QuizCollection> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    quizCollection = response.body();
                    Log.d(TAG, "Received collection: " + quizCollection.getCategory());

                    tvTitle.setText(quizCollection.getCategory());

                    imgBanner.setVisibility(View.VISIBLE);
                    if (quizCollection.getCoverPhoto() != null && !quizCollection.getCoverPhoto().isEmpty()) {

                        Glide.with(DetailTopCollections.this)
                                .load(quizCollection.getCoverPhoto())
                                .placeholder(R.drawable.ic_image_placeholder_2)
                                .error(R.drawable.ic_image_placeholder_2)
                                .into(imgBanner);
                    } else {
                        imgBanner.setImageResource(R.drawable.ic_image_placeholder_2);
                    }

                    if(userId != quizCollection.getAuthorId()) {
                        btnEdit.setVisibility(View.GONE);
                    }

                    Log.d(TAG, "Collection description: " + quizCollection.getDescription());
                    Log.d(TAG, "Collection author ID: " + quizCollection.getAuthorId());
                    Log.d(TAG, "Collection timestamp: " + quizCollection.getTimestamp());
                    Log.d(TAG, "Collection visibleTo: " + quizCollection.isVisibleTo());

                    if (quizCollection.getQuizzes() != null && !quizCollection.getQuizzes().isEmpty()) {
                        setupQuizRecyclerView(quizCollection.getQuizzes());
                    } else {
                        originalQuizList.clear();
                        filteredQuizList.clear();
                        updateQuizRecyclerView();
                        updateQuizCount();
                        updateNoQuizVisibility();
                        Log.d(TAG, "Collection has no quizzes - showing empty state");
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
        originalQuizList.clear();
        originalQuizList.addAll(quizzes);

        performSearch(currentSearchQuery);

        Log.d(TAG, "Loaded " + originalQuizList.size() + " quizzes from collection");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Current request code: " + requestCode);
        if (resultCode == RESULT_OK && data != null) {
            boolean shouldReload = false;
            String message = "";

            if (requestCode == UPDATE_COLLECTION_REQUEST_CODE) {
                boolean collectionUpdated = data.getBooleanExtra("updatedCollection", false);
                if (collectionUpdated) {
                    shouldReload = true;
                    message = "Collection updated successfully";
                    Log.d(TAG, "Collection was updated, reloading data");
                }
            }
            else if (requestCode == QUIZ_DETAILS_REQUEST_CODE) {
                boolean quizDeleted = data.getBooleanExtra("quiz_deleted", false);
                boolean quizUpdated = data.getBooleanExtra("quiz_updated", false);
                Log.d(TAG, "Current request code: " + data.getBooleanExtra("quiz_updated", false));
                Log.d(TAG, "Current request code: " + data.getBooleanExtra("quiz_deleted", false));

                if (quizDeleted) {
                    int deletedQuizId = data.getIntExtra("quizId", -1);
                    shouldReload = true;
                    message = "Quiz deleted successfully";
                    Log.d(TAG, "Quiz with ID " + deletedQuizId + " was deleted, reloading data");
                }
                else if (quizUpdated) {
                    long updatedQuizId = data.getLongExtra("quizId", -1);
                    shouldReload = true;
                    message = "Quiz updated successfully";
                    Log.d(TAG, "Quiz with ID " + updatedQuizId + " was updated, reloading data");
                }
            }

            if (shouldReload) {
                if (!message.isEmpty()) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                fetchCollectionDetails(collectionId);
            }
        }
    }

    public void startQuizDetailsActivity(Long quizId) {
        Intent intent = new Intent(this, QuizzDetails.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, QUIZ_DETAILS_REQUEST_CODE);
    }
}