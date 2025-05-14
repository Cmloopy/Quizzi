package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.adapter.RecommendAuthorAdapter;
import com.cmloopy.quizzi.data.manager.AuthorDataManager;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.R;

import java.util.ArrayList;
import java.util.List;

public class RecommendAuthorActivity extends AppCompatActivity {

    private RecyclerView recommendedFriendsRecyclerView;
    private RecommendAuthorAdapter recommendedFriendsAdapter;
    private List<RecommendUser> authorsList = new ArrayList<>();
    private AuthorDataManager authorDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_author);
        Toolbar toolbar = findViewById(R.id.toolbar_with_search_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top Authors");

        recommendedFriendsRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recommendedFriendsRecyclerView.setLayoutManager(linearLayoutManager);

        // Khởi tạo adapter với danh sách rỗng ban đầu
        recommendedFriendsAdapter = new RecommendAuthorAdapter(authorsList);
        recommendedFriendsRecyclerView.setAdapter(recommendedFriendsAdapter);

        // Tải dữ liệu từ AuthorDataManager
        loadAuthors();
    }

    // Không cần thay đổi gì trong RecommendAuthorActivity.java vì nó đã hoạt động tốt
// Tuy nhiên, bạn có thể thêm log để kiểm tra nếu muốn

// Trong phương thức loadAuthors() của RecommendAuthorActivity.java, bạn có thể thêm log:

    private void loadAuthors() {
        authorDataManager = AuthorDataManager.getInstance();

        // Kiểm tra xem đã có dữ liệu cache chưa
        if (authorDataManager.isDataLoaded()) {
            // Nếu đã có dữ liệu trong cache, sử dụng luôn
            authorsList.clear();
            authorsList.addAll(authorDataManager.getCachedTopAuthors());
            recommendedFriendsAdapter.notifyDataSetChanged();

            // Log để kiểm tra
            Log.d("RecommendAuthorActivity", "Đã tải " + authorsList.size() + " tác giả từ cache");

            // Vẫn tiếp tục tải dữ liệu mới từ API
            refreshAuthorsData();
        } else {
            // Log để kiểm tra
            Log.d("RecommendAuthorActivity", "Chưa có dữ liệu cache, tải từ API");

            // Nếu chưa có dữ liệu, tải mới từ API
            authorDataManager.loadTopAuthors(this, new AuthorDataManager.OnAuthorsLoadedListener() {
                @Override
                public void onAuthorsLoaded(List<RecommendUser> authors) {
                    authorsList.clear();
                    authorsList.addAll(authors);
                    recommendedFriendsAdapter.notifyDataSetChanged();

                    // Log để kiểm tra
                    Log.d("RecommendAuthorActivity", "Đã tải " + authors.size() + " tác giả từ API");
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(RecommendAuthorActivity.this, message, Toast.LENGTH_SHORT).show();
                    // Log để kiểm tra
                    Log.e("RecommendAuthorActivity", "Lỗi tải dữ liệu: " + message);
                    // Nếu có lỗi, sử dụng dữ liệu mẫu
                    setupSampleData();
                }
            });
        }
    }

    // Phương thức tải dữ liệu mới nhưng không ngăn hiển thị dữ liệu cũ
    private void refreshAuthorsData() {
        authorDataManager.loadAllAuthors(this, new AuthorDataManager.OnAuthorsLoadedListener() {
            @Override
            public void onAuthorsLoaded(List<RecommendUser> authors) {
                // Chỉ cập nhật nếu có dữ liệu mới
                if (!authors.isEmpty()) {
                    authorsList.clear();
                    authorsList.addAll(authors);
                    recommendedFriendsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String message) {
                // Chỉ hiển thị thông báo lỗi, không thay đổi dữ liệu hiện tại
                Toast.makeText(RecommendAuthorActivity.this,
                        "Cập nhật dữ liệu thất bại: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Dữ liệu mẫu chỉ sử dụng khi có lỗi tải dữ liệu từ API
    private void setupSampleData() {
        authorsList.clear();
        authorsList.add(new RecommendUser("Darron Kulikowski", "darronk", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Maryland Winkles", "mwinkles", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Lauralee Quintero", "lquintero", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Alfonzo Schuessler", "aschuessler", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Terry Johnson", "tjohnson", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Sarah Martinez", "smartinez", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Michael Lee", "mlee", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Jessica Smith", "jsmith", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Robert Williams", "rwilliams", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Emily Davis", "edavis", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("John Brown", "jbrown", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("Julia Wilson", "jwilson", R.drawable.ic_launcher_background));
        authorsList.add(new RecommendUser("David Taylor", "dtaylor", R.drawable.ic_launcher_background));

        recommendedFriendsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}