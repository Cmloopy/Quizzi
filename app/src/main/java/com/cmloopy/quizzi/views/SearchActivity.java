package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.adapter.RecommendUserAdapter;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.models.TopCollectionsCategory;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private EditText searchInput;
    private RadioGroup searchCategory;
    private ImageButton cancelButton;
    private TextView emptyStateView;
    private RecyclerView.Adapter<?> adapter = null;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_TIME = 300; // 300ms debounce

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Khởi tạo các view
        searchInput = findViewById(R.id.searchEditText);
        cancelButton = findViewById(R.id.clearButton);
        searchCategory = findViewById(R.id.tabGroup);
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateView = findViewById(R.id.emptyStateView); // Giả sử bạn đã thêm view này vào layout

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Thiết lập sự kiện nhóm radio
        searchCategory.setOnCheckedChangeListener((radioGroup, checkId) -> {
            handleSearchCategoryChange(checkId);
            // Tìm kiếm lại với danh mục mới
            if (!searchInput.getText().toString().trim().isEmpty()) {
                performSearch();
            }
        });

        // Thiết lập danh mục mặc định khi khởi động
        searchCategory.post(() -> {
            searchCategory.check(R.id.radioQuizBtn);
            handleSearchCategoryChange(R.id.radioQuizBtn);
        });

        // Thiết lập tìm kiếm trong khi gõ với debounce
        setupSearchWithDebounce();

        // Thiết lập nút xóa
        cancelButton.setOnClickListener(view -> clearSearch());
    }

    private void setupSearchWithDebounce() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý gì
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hủy bỏ tìm kiếm trước đó nếu có
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tạo runnable mới cho tìm kiếm với nội dung hiện tại
                searchRunnable = () -> performSearch();

                // Lên lịch thực hiện sau một khoảng thời gian debounce
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_TIME);
            }
        });
    }

    private RecommendUserAdapter getAuthorAdapter() {
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

        return new RecommendUserAdapter(friendsList);
    }

    private QuizAdapter getQuizAdapter() {
        List<Quiz> items = new ArrayList<>();

        items.add(new Quiz(R.drawable.ic_launcher_background, "Get Smarter with Prod...",
                "2 months ago", "5.5K plays", "Titus Kitamura", R.drawable.ic_launcher_background, "16 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Great Ideas Come from...",
                "6 months ago", "10.3K plays", "Alfonzo Schuessler", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Having Fun & Always S...",
                "2 years ago", "18.5K plays", "Daryl Nehls", R.drawable.ic_launcher_background, "12 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Can You Imagine, Worl...",
                "3 months ago", "4.9K plays", "Edgar Torrey", R.drawable.ic_launcher_background, "20 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Back to School, Get Sm...",
                "1 year ago", "12.4K plays", "Darcel Ballentine", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "What is Your Favorite ...",
                "5 months ago", "6.2K plays", "Elmer Laverty", R.drawable.ic_launcher_background, "16 Qs"));

        return new QuizAdapter(items);
    }

    private TopCollectionsCategoryAdapter getCollectionAdapter() {
        List<TopCollectionsCategory> categoryList = new ArrayList<>();

        // Tạo danh sách danh mục với collectionId
        TopCollectionsCategory education = new TopCollectionsCategory("General Knowledge", R.drawable.img_1);
        education.setCollectionId(1);
        categoryList.add(education);

        TopCollectionsCategory games = new TopCollectionsCategory("Sports", R.drawable.img_1);
        games.setCollectionId(2);
        categoryList.add(games);

        TopCollectionsCategory business = new TopCollectionsCategory("Music", R.drawable.img_1);
        business.setCollectionId(3);
        categoryList.add(business);

        TopCollectionsCategory entertainment = new TopCollectionsCategory("Technology", R.drawable.img_1);
        entertainment.setCollectionId(4);
        categoryList.add(entertainment);

        TopCollectionsCategory art = new TopCollectionsCategory("History", R.drawable.img_1);
        art.setCollectionId(5);
        categoryList.add(art);

        TopCollectionsCategory plants = new TopCollectionsCategory("Movies", R.drawable.img_1);
        plants.setCollectionId(6);
        categoryList.add(plants);

        TopCollectionsCategory finance = new TopCollectionsCategory("Education", R.drawable.img_1);
        finance.setCollectionId(7);
        categoryList.add(finance);

        TopCollectionsCategory foodDrink = new TopCollectionsCategory("General Knowledge", R.drawable.img_1);
        foodDrink.setCollectionId(8);
        categoryList.add(foodDrink);

        TopCollectionsCategory health = new TopCollectionsCategory("History", R.drawable.img_1);
        health.setCollectionId(9);
        categoryList.add(health);

        TopCollectionsCategory kids = new TopCollectionsCategory("Movies", R.drawable.img_1);
        kids.setCollectionId(10);
        categoryList.add(kids);

        TopCollectionsCategory sports = new TopCollectionsCategory("Geography", R.drawable.img_1);
        sports.setCollectionId(11);
        categoryList.add(sports);

        TopCollectionsCategory lifestyle = new TopCollectionsCategory("Entertainment", R.drawable.img_1);
        lifestyle.setCollectionId(12);
        categoryList.add(lifestyle);

        // Log để kiểm tra ID collection
        for (TopCollectionsCategory category : categoryList) {
            Log.d(TAG, "Created category: " + category.getName() + " with ID: " + category.getCollectionId());
        }

        // Hiển thị thông báo tổng số danh mục đã tạo
        Toast.makeText(this, "Loaded " + categoryList.size() + " collection categories", Toast.LENGTH_SHORT).show();

        return new TopCollectionsCategoryAdapter(this, categoryList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSearchCategoryChange(int checkedId) {
        if (checkedId == R.id.radioPeopleBtn) {
            gridLayoutManager = new GridLayoutManager(this, 1);
            adapter = this.getAuthorAdapter();
            Log.d(TAG, "Switched to People category");
        } else if (checkedId == R.id.radioQuizBtn) {
            gridLayoutManager = new GridLayoutManager(this, 1);
            adapter = this.getQuizAdapter();
            Log.d(TAG, "Switched to Quiz category");
        } else if (checkedId == R.id.radioCollectionBtn) {
            gridLayoutManager = new GridLayoutManager(this, 2);
            adapter = this.getCollectionAdapter();
            Log.d(TAG, "Switched to Collections category");
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        // Ẩn thông báo trạng thái trống vì đang hiển thị danh sách đầy đủ
        showEmptyState(false);
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        int checkedId = searchCategory.getCheckedRadioButtonId();
        RecyclerView.Adapter<?> searchAdapter = null;
        boolean isEmpty = false;

        if (query.isEmpty()) {
            // Nếu truy vấn trống, hiển thị toàn bộ danh sách
            if (checkedId == R.id.radioPeopleBtn) {
                searchAdapter = getAuthorAdapter();
            } else if (checkedId == R.id.radioQuizBtn) {
                searchAdapter = getQuizAdapter();
            } else if (checkedId == R.id.radioCollectionBtn) {
                searchAdapter = getCollectionAdapter();
            }
        } else {
            // Nếu có truy vấn, lọc kết quả
            if (checkedId == R.id.radioPeopleBtn) {
                searchAdapter = filterAuthors(query);
                isEmpty = ((RecommendUserAdapter) searchAdapter).getItemCount() == 0;
            } else if (checkedId == R.id.radioQuizBtn) {
                searchAdapter = filterQuizzes(query);
                isEmpty = ((QuizAdapter) searchAdapter).getItemCount() == 0;
            } else if (checkedId == R.id.radioCollectionBtn) {
                searchAdapter = filterCollections(query);
                isEmpty = ((TopCollectionsCategoryAdapter) searchAdapter).getItemCount() == 0;
            }
        }

        // Cập nhật RecyclerView và hiển thị trạng thái trống nếu cần
        recyclerView.setAdapter(searchAdapter);
        showEmptyState(isEmpty);
    }

    private void showEmptyState(boolean show) {
        if (emptyStateView != null) {
            emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private QuizAdapter filterQuizzes(String query) {
        List<Quiz> filteredList = new ArrayList<>();

        for (Quiz quiz : getQuizAdapter().getListQuiz()) {
            if (quiz.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(quiz);
            }
        }

        return new QuizAdapter(filteredList);
    }

    private RecommendUserAdapter filterAuthors(String query) {
        List<RecommendUser> filteredList = new ArrayList<>();

        for (RecommendUser user : getAuthorAdapter().getFriendsList()) {
            if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        return new RecommendUserAdapter(filteredList);
    }

    private TopCollectionsCategoryAdapter filterCollections(String query) {
        List<TopCollectionsCategory> filteredList = new ArrayList<>();
        List<TopCollectionsCategory> allCategories = getCollectionAdapter().getCategoryList();

        Log.d(TAG, "Filtering collections with query: " + query);

        for (TopCollectionsCategory category : allCategories) {
            if (category.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(category);
                Log.d(TAG, "Added filtered category: " + category.getName() + " with ID: " + category.getCollectionId());
            }
        }

        Log.d(TAG, "Filtered to " + filteredList.size() + " categories");
        return new TopCollectionsCategoryAdapter(this, filteredList);
    }

    private void clearSearch() {
        // Xóa nội dung nhập và hủy bỏ tìm kiếm đang chờ
        searchInput.setText("");
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        // Trở lại danh sách mặc định và ẩn trạng thái trống
        recyclerView.setAdapter(adapter);
        showEmptyState(false);

        Log.d(TAG, "Search cleared");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dọn dẹp handler khi activity bị hủy
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}