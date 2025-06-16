package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

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

    private void reloadCurrentTab() {
        Log.d(TAG, "Reloading current tab: " + currentSelectedTab);
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

                Toast.makeText(requireContext(),
                        "Sorted by " + (isNewestFirst ? "newest" : "oldest"),
                        Toast.LENGTH_SHORT).show();
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

    private void updateQuizRecyclerView() {
        QuizAdapter quizAdapter = new QuizAdapter(new ArrayList<>(filteredQuizList), userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(quizAdapter);
    }

    private void updateCollectionRecyclerView() {
        gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        TopCollectionsCategoryAdapter collectionAdapter =
                new TopCollectionsCategoryAdapter(requireContext(), new ArrayList<>(filteredCollectionList), userId);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(collectionAdapter);
    }

    private void handleSearchCategoryChange(int checkedId) {
        // Clear search when switching tabs
        currentSearchQuery = "";
        searchEditText.setText("");

        if (checkedId == R.id.radioLibQuizzoBtn) {
            // Show sort button for quizzes
            sortTextButton.setVisibility(View.VISIBLE);
            sortTextButton.setText(isNewestFirst ? "Newest" : "Oldest");
            fetchUserQuizzes();
        } else if (checkedId == R.id.radioLibCollectionBtn) {
            // Hide sort button for collections
            sortTextButton.setVisibility(View.GONE);
            fetchUserCollections();
        }
    }

    private void fetchUserQuizzes() {
        progressBar.setVisibility(View.VISIBLE);
        quizAPI.getUserQuizzes(userId).enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> quizResponses = response.body();
//                    List<Quiz> quizzes = convertToQuizModel(quizResponses);

                    originalQuizList.clear();
                    originalQuizList.addAll(quizResponses);

                    // Apply search and sort
                    performSearch(currentSearchQuery);
                } else {
                    String errorMessage = "Failed to load quizzes: " +
                            (response.code() != 0 ? "Error " + response.code() : "Unknown error");
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserCollections() {
        progressBar.setVisibility(View.VISIBLE);

        quizAPI.getQuizCollectionsByAuthor(userId).enqueue(new Callback<List<QuizCollection>>() {
            @Override
            public void onResponse(Call<List<QuizCollection>> call, Response<List<QuizCollection>> response) {
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
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollection>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadCurrentTab();
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