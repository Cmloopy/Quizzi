package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.models.TopCollectionsCategory;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCGenericSelectionDialog;
import com.cmloopy.quizzi.utils.QuestionCreate.helper.QCHelper;
import com.google.android.material.textview.MaterialTextView;

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

public class MyQuizzoFragment extends Fragment {

    private static final String TAG = "MyQuizzoFragment";
    private static final int QUIZ_DETAILS_REQUEST_CODE = 2001;

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private MaterialTextView materialTextView;
    private MaterialTextView sortTextButton;
    private EditText searchEditText;
    private ProgressBar progressBar;
    private QuizzApi quizAPI;

    // Empty state views
    private LinearLayout emptyStateContainer;
    private ImageView emptyStateIcon;
    private TextView emptyStateTitle;
    private TextView emptyStateMessage;

    private int userId = -1;

    RecyclerView.Adapter<?> adapter = null;
    private int currentSelectedTab = R.id.radioLibQuizzoBtn;

    // Data storage for search and sort
    private List<QuizResponse> originalQuizList = new ArrayList<>();
    private List<QuizResponse> filteredQuizList = new ArrayList<>();
    private List<QuizCollection> originalCollectionList = new ArrayList<>();
    private List<QuizCollection> filteredCollectionList = new ArrayList<>();

    // Sort state
    private boolean isNewestFirst = true;
    private String currentSearchQuery = "";
    private boolean isInitialLoadComplete = false;

    public MyQuizzoFragment() {}

    public static MyQuizzoFragment newInstance(int userId) {
        MyQuizzoFragment fragment = new MyQuizzoFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called, userId: " + userId);

        if (userId != -1) {
            // Add a delay to ensure the fragment is fully ready
            view.postDelayed(() -> {
                if (isAdded() && !isInitialLoadComplete) {
                    reloadCurrentTab();
                    isInitialLoadComplete = true;
                }
            }, 100);
        } else {
            Log.e(TAG, "Invalid userId in onViewCreated");
        }
    }

    public void refreshData() {
        Log.d(TAG, "refreshData called, userId: " + userId + ", isAdded: " + isAdded());

        if (isAdded() && getContext() != null && userId != -1) {
            // Reset the flag when manually refreshing
            isInitialLoadComplete = false;
            reloadCurrentTab();
        } else {
            Log.w(TAG, "Cannot refresh data - fragment not ready or invalid userId");
        }
    }

    private void reloadCurrentTab() {
        Log.d(TAG, "Reloading current tab: " + currentSelectedTab + ", userId: " + userId);

        if (userId == -1) {
            Log.e(TAG, "Invalid userId in reloadCurrentTab");
            return;
        }

        handleSearchCategoryChange(currentSelectedTab);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_quizzo, container, false);

        // Khởi tạo API service
        quizAPI = RetrofitClient.getQuizzApi();

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText);
        RadioGroup radioGroup = view.findViewById(R.id.tabGroup1);
        recyclerView = view.findViewById(R.id.rcl_view_quizzo_cls);
        materialTextView = view.findViewById(R.id.txt_title_lib_my_quizzo);
        progressBar = view.findViewById(R.id.progress_bar);
        sortTextButton = view.findViewById(R.id.sortTextButton);

        // Initialize empty state views
        emptyStateContainer = view.findViewById(R.id.emptyStateContainer);
        emptyStateIcon = view.findViewById(R.id.emptyStateIcon);
        emptyStateTitle = view.findViewById(R.id.emptyStateTitle);
        emptyStateMessage = view.findViewById(R.id.emptyStateMessage);

        // Setup search functionality
        setupSearchBar();

        // Setup sort functionality
        setupSortButton();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            currentSelectedTab = checkedId;
            handleSearchCategoryChange(checkedId);
        });

        // Mặc định chọn tab Quizzo
        radioGroup.post(() -> {
            radioGroup.check(R.id.radioLibQuizzoBtn);
            currentSelectedTab = R.id.radioLibQuizzoBtn;
            handleSearchCategoryChange(R.id.radioLibQuizzoBtn);
        });

        return view;
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                performSearch(currentSearchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSortButton() {
        sortTextButton.setOnClickListener(v -> {
            if (currentSelectedTab == R.id.radioLibQuizzoBtn) {
                // Toggle sort order
                isNewestFirst = !isNewestFirst;

                // Update button text
                sortTextButton.setText(isNewestFirst ? "Newest" : "Oldest");

                // Apply sort and search
                performSearch(currentSearchQuery);

                // Check if fragment is still attached before showing Toast
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(),
                            "Sorted by " + (isNewestFirst ? "newest" : "oldest"),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performSearch(String query) {
        if (currentSelectedTab == R.id.radioLibQuizzoBtn) {
            searchQuizzes(query);
        } else if (currentSelectedTab == R.id.radioLibCollectionBtn) {
            searchCollections(query);
        }
    }

    private void searchQuizzes(String query) {
        filteredQuizList.clear();

        if (query.isEmpty()) {
            filteredQuizList.addAll(originalQuizList);
        } else {
            for (QuizResponse quiz : originalQuizList) {
                if (quiz.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        quiz.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredQuizList.add(quiz);
                }
            }
        }

        // Apply sorting
        sortQuizzes(filteredQuizList);

        // Update UI
        updateQuizRecyclerView();
        materialTextView.setText(filteredQuizList.size() + " Quizzo");

        // Handle empty state
        handleEmptyState(filteredQuizList.isEmpty(), true, query);
    }

    private void searchCollections(String query) {
        filteredCollectionList.clear();

        if (query.isEmpty()) {
            filteredCollectionList.addAll(originalCollectionList);
        } else {
            for (QuizCollection collection : originalCollectionList) {
                if (collection.getCategory().toLowerCase().contains(query.toLowerCase())) {
                    filteredCollectionList.add(collection);
                }
            }
        }

        // Update UI
        updateCollectionRecyclerView();
        materialTextView.setText(filteredCollectionList.size() + " Collections");

        // Handle empty state
        handleEmptyState(filteredCollectionList.isEmpty(), false, query);
    }

    private void handleEmptyState(boolean isEmpty, boolean isQuizTab, String searchQuery) {
        if (isEmpty) {
            // Show empty state
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);

            if (isQuizTab) {
                // Quiz empty state
                emptyStateIcon.setImageResource(R.drawable.ic_empty_questions != 0 ? R.drawable.ic_empty_questions : android.R.drawable.ic_menu_help);

                if (!searchQuery.isEmpty()) {
                    // No search results
                    emptyStateTitle.setText("No quizzes found");
                    emptyStateMessage.setText("No quizzes match your search \"" + searchQuery + "\".\nTry using different keywords.");
                } else {
                    // No quizzes at all
                    emptyStateTitle.setText("No quizzes yet");
                    emptyStateMessage.setText("You haven't created any quizzes yet.\nCreate your first quiz to get started!");
                }
            } else {
                // Collection empty state
                emptyStateIcon.setImageResource(R.drawable.ic_empty_questions != 0 ? R.drawable.ic_empty_questions : android.R.drawable.ic_menu_gallery);

                if (!searchQuery.isEmpty()) {
                    // No search results
                    emptyStateTitle.setText("No collections found");
                    emptyStateMessage.setText("No collections match your search \"" + searchQuery + "\".\nTry using different keywords.");
                } else {
                    // No collections at all
                    emptyStateTitle.setText("No collections yet");
                    emptyStateMessage.setText("You haven't created any collections yet.\nCreate your first collection to organize your quizzes!");
                }
            }
        } else {
            // Hide empty state, show content
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }

    private void sortQuizzes(List<QuizResponse> quizzes) {
        Collections.sort(quizzes, new Comparator<QuizResponse>() {
            @Override
            public int compare(QuizResponse q1, QuizResponse q2) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
                    Date date1 = format.parse(q1.getCreatedAt());
                    Date date2 = format.parse(q2.getCreatedAt());

                    if (date1 == null || date2 == null) {
                        return 0;
                    }

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

    boolean initQuizRecyclerView = true;
    boolean initCollectionRecyclerView = true;

    private void updateQuizRecyclerView() {
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "Fragment not attached, skipping RecyclerView update");
            return;
        }

        QuizAdapter quizAdapter = new QuizAdapter(new ArrayList<>(filteredQuizList), userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(quizAdapter);
        if(initQuizRecyclerView) {
            recyclerView.addItemDecoration(new QCHelper.LinearItemDecoration(5));
        }
        initQuizRecyclerView = false;
    }

    private void updateCollectionRecyclerView() {
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "Fragment not attached, skipping RecyclerView update");
            return;
        }

        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        TopCollectionsCategoryAdapter collectionAdapter =
                new TopCollectionsCategoryAdapter(getContext(), new ArrayList<>(filteredCollectionList), userId);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(collectionAdapter);
        if(initCollectionRecyclerView) {
            recyclerView.addItemDecoration(new QCHelper.GridItemDecoration(2, 15));
        }
        initCollectionRecyclerView = false;
    }

    private void handleSearchCategoryChange(int checkedId) {
        // Clear search when switching tabs
        currentSearchQuery = "";
        searchEditText.setText("");

        if (checkedId == R.id.radioLibQuizzoBtn) {
            // Show sort button for quizzes
            sortTextButton.setVisibility(View.VISIBLE);
            sortTextButton.setText(isNewestFirst ? "Newest" : "Oldest");
            // Update search hint for quizzes
            searchEditText.setHint("Search quizzes by name or keywords...");
            fetchUserQuizzes();
        } else if (checkedId == R.id.radioLibCollectionBtn) {
            // Hide sort button for collections
            sortTextButton.setVisibility(View.GONE);
            // Update search hint for collections
            searchEditText.setHint("Search collections by category...");
            fetchUserCollections();
        }
    }

    private void fetchUserQuizzes() {
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "Fragment not attached, skipping API call");
            return;
        }

        if (userId == -1) {
            Log.e(TAG, "Invalid userId, cannot fetch quizzes");
            return;
        }

        Log.d(TAG, "Fetching quizzes for userId: " + userId);
        progressBar.setVisibility(View.VISIBLE);

        // Hide empty state while loading
        emptyStateContainer.setVisibility(View.GONE);

        quizAPI.getUserQuizzes(userId).enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "Fragment not attached, ignoring API response");
                    return;
                }

                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "API Response received, success: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> quizResponses = response.body();
                    Log.d(TAG, "Received " + quizResponses.size() + " quizzes");

                    originalQuizList.clear();
                    originalQuizList.addAll(quizResponses);

                    // Apply search and sort
                    performSearch(currentSearchQuery);
                } else {
                    Log.e(TAG, "API call failed with code: " + response.code());
                    String errorMessage = "Failed to load quizzes: " +
                            (response.code() != 0 ? "Error " + response.code() : "Unknown error");
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    // Show empty state on error
                    handleEmptyState(true, true, currentSearchQuery);
                }
            }

            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "Fragment not attached, ignoring API failure");
                    return;
                }

                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "API call failed", t);
                Toast.makeText(getContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Show empty state on network error
                handleEmptyState(true, true, currentSearchQuery);
            }
        });
    }

    private void fetchUserCollections() {
        // Check if fragment is still attached
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "Fragment not attached, skipping API call");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Hide empty state while loading
        emptyStateContainer.setVisibility(View.GONE);

        quizAPI.getQuizCollectionsByAuthor(userId).enqueue(new Callback<List<QuizCollection>>() {
            @Override
            public void onResponse(Call<List<QuizCollection>> call, Response<List<QuizCollection>> response) {
                // Check if fragment is still attached before updating UI
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "Fragment not attached, ignoring API response");
                    return;
                }

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizCollection> collections = response.body();

                    List<QuizCollection> filteredCollections = new ArrayList<>();
                    for (QuizCollection collection : collections) {
                        if (collection.getAuthorId() == userId) {
                            filteredCollections.add(collection);
                        }
                    }

                    // Store original data
                    originalCollectionList.clear();
                    originalCollectionList.addAll(filteredCollections);

                    // Apply search
                    performSearch(currentSearchQuery);
                } else {
                    String errorMessage = "Failed to load collections: " +
                            (response.code() != 0 ? "Error " + response.code() : "Unknown error");
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    // Show empty state on error
                    handleEmptyState(true, false, currentSearchQuery);
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollection>> call, Throwable t) {
                // Check if fragment is still attached before updating UI
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "Fragment not attached, ignoring API failure");
                    return;
                }

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Show empty state on network error
                handleEmptyState(true, false, currentSearchQuery);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called, isInitialLoadComplete: " + isInitialLoadComplete);

        if (!isInitialLoadComplete && userId != -1) {
            reloadCurrentTab();
            isInitialLoadComplete = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called - requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (requestCode == QUIZ_DETAILS_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            boolean quizModified = data.getBooleanExtra("quiz_modified", false);
            boolean quizDeleted = data.getBooleanExtra("quiz_deleted", false);

            Log.d(TAG, "Quiz modified: " + quizModified + ", Quiz deleted: " + quizDeleted);

            if (quizModified || quizDeleted) {
                Toast.makeText(getContext(), "Quiz list updated", Toast.LENGTH_SHORT).show();
                reloadCurrentTab();
            }
        }
    }
}