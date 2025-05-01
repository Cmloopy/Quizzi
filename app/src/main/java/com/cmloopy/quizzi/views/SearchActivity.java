package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.adapter.RecommendAuthorAdapter;
import com.cmloopy.quizzi.adapter.RecommendUserAdapter;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.models.TopCollectionsCategory;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private EditText searchInput;
    private RadioGroup searchCategory;
    private ImageButton cancelButton;
    RecyclerView.Adapter<?> adapter = null;


    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchEditText);
        cancelButton = findViewById(R.id.clearButton);
        searchCategory = findViewById(R.id.tabGroup);

//        handleSearchCategoryChange(R.id.radioQuizBtn);
        searchCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                handleSearchCategoryChange(checkId);
            }
        });
        searchCategory.post(() -> {
            searchCategory.check(R.id.radioQuizBtn);
            handleSearchCategoryChange(R.id.radioQuizBtn);
        });

        searchInput.setOnClickListener(view -> performSearch());

        cancelButton.setOnClickListener(view -> clearSearch());


        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        RecommendUserAdapter recommendedFriendsAdapter = new RecommendUserAdapter(friendsList);
        return recommendedFriendsAdapter;
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
        QuizAdapter quizAdapter  = new QuizAdapter(items);
        return quizAdapter;
    }
    private TopCollectionsCategoryAdapter getCollectionAdapter() {
        List<TopCollectionsCategory> categoryList = new ArrayList<>();
        categoryList.add(new TopCollectionsCategory("Education", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Games", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Business", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Entertainment", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Art", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Plants", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Finance", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Food & Drink", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Health", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Kids", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Sports", R.drawable.img_02));
        categoryList.add(new TopCollectionsCategory("Lifestyle", R.drawable.img_02));

        TopCollectionsCategoryAdapter collectionAdapter = new TopCollectionsCategoryAdapter(this, categoryList);
        return  collectionAdapter;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSearchCategoryChange(int checkedId) {

        if (checkedId == R.id.radioPeopleBtn) {
            gridLayoutManager = new GridLayoutManager(this, 1);
            adapter = this.getAuthorAdapter();
        } else if (checkedId == R.id.radioQuizBtn) {
            gridLayoutManager = new GridLayoutManager(this, 1);
            adapter = this.getQuizAdapter();
        } else if (checkedId == R.id.radioCollectionBtn) {
            gridLayoutManager = new GridLayoutManager(this, 2);
            adapter = this.getCollectionAdapter();
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();


        // Xác định danh mục tìm kiếm dựa trên radio button được chọn
        int checkedId = searchCategory.getCheckedRadioButtonId();
        RecyclerView.Adapter adapter = null;

        if (checkedId == R.id.radioPeopleBtn) {
            adapter = filterAuthors(query);
        } else if (checkedId == R.id.radioQuizBtn) {
            adapter = filterQuizzes(query);
        } else if (checkedId == R.id.radioCollectionBtn) {
            adapter = filterCollections(query);
        }

        // Cập nhật RecyclerView với kết quả tìm kiếm
        recyclerView.setAdapter(adapter);
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

        for (TopCollectionsCategory category : getCollectionAdapter().getCategoryList()) {
            if (category.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(category);
            }
        }

        return new TopCollectionsCategoryAdapter(this, filteredList);
    }


    private void clearSearch() {
        searchInput.setText(""); // Xóa nội dung nhập
        recyclerView.setAdapter(adapter); // Trả về danh sách mặc định
    }

}
