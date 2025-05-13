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
import com.cmloopy.quizzi.data.api.Topcollection.CollectionService;
import com.cmloopy.quizzi.models.DetailTopCollectionItem;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    private static final String TAG = "DetailTopCollections";
    private RecyclerView recyclerView;
    private DetailTopCollectionAdapter quizAdapter;
    private List<DetailTopCollectionItem> quizList;
    private TextView tvTitle;
    private TextView tvQuizCount;
    private ImageView btnBack;
    private ImageView btnSearch;
    private ImageView imgBanner;
    private int collectionId = -1;

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

        // Thiết lập sự kiện click cho nút Search
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailTopCollections.this, "Search function not implemented yet", Toast.LENGTH_SHORT).show();
                // TODO: Mở tính năng tìm kiếm
            }
        });

        // Lấy collectionId từ Intent
        if (getIntent() != null) {
            collectionId = getIntent().getIntExtra("collectionId", -1);
            Log.d(TAG, "Received collection ID: " + collectionId);
        }

        // Nếu có collectionId, lấy thông tin chi tiết
        if (collectionId != -1) {
            fetchCollectionDetails(collectionId);
        } else {
            Toast.makeText(this, "No collection ID provided", Toast.LENGTH_SHORT).show();
            loadDummyData();
        }
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

                    // Hiển thị số lượng quizzes
                    int quizCount = (collection.getQuizzes() != null) ? collection.getQuizzes().size() : 0;
                    tvQuizCount.setText(quizCount + " Quizzo");

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

                    // Hiển thị description trong log (có thể thêm TextView cho description nếu cần)
                    Log.d(TAG, "Collection description: " + collection.getDescription());

                    // Hiển thị author ID trong log (có thể fetch thông tin author từ API khác nếu cần)
                    Log.d(TAG, "Collection author ID: " + collection.getAuthorId());

                    // Hiển thị timestamp trong log (có thể hiển thị trên UI nếu cần)
                    Log.d(TAG, "Collection timestamp: " + collection.getTimestamp());

                    // Hiển thị visibleTo trong log (có thể sử dụng để kiểm soát quyền xem nếu cần)
                    Log.d(TAG, "Collection visibleTo: " + collection.isVisibleTo());

                    // Nếu collection có quizzes, hiển thị chúng
                    if (collection.getQuizzes() != null && !collection.getQuizzes().isEmpty()) {
                        loadQuizData(collection.getQuizzes());
                    } else {
                        Toast.makeText(DetailTopCollections.this,
                                "Collection has no quizzes. Using sample data.",
                                Toast.LENGTH_SHORT).show();
                        loadDummyData();
                    }
                } else {
                    Log.e(TAG, "API error: " + response.message());

                    Toast.makeText(DetailTopCollections.this,
                            "Failed to load collection details. Using sample data.",
                            Toast.LENGTH_SHORT).show();

                    loadDummyData();
                }
            }

            @Override
            public void onFailure(Call<QuizCollection> call, Throwable t) {
                Log.e(TAG, "API call failed", t);

                Toast.makeText(DetailTopCollections.this,
                        "Network error: " + t.getMessage() + ". Using sample data.",
                        Toast.LENGTH_SHORT).show();

                loadDummyData();
            }
        });
    }

    private void loadQuizData(List<Object> quizzes) {
        quizList = new ArrayList<>();
        Gson gson = new Gson();

        for (Object quizObj : quizzes) {
            try {
                // Chuyển đổi Object thành JsonObject
                String quizJsonStr = gson.toJson(quizObj);
                JSONObject quizJson = new JSONObject(quizJsonStr);

                // Lấy dữ liệu từ JSON
                int quizId = quizJson.optInt("id", 0);
                String title = quizJson.optString("title", "Untitled Quiz");
                String authorName = quizJson.optString("authorName", "Unknown Author");

                // Lấy và định dạng timestamp
                String timestamp = quizJson.optString("timestamp", "");
                String timeAgo = formatTimeAgo(timestamp);

                // Lấy số lượt chơi
                int playCount = quizJson.optInt("playCount", 0);
                String plays = formatPlayCount(playCount);

                // Lấy ảnh đại diện của quiz (nếu có)
                String imageUrl = quizJson.optString("coverImage", "");
                int imageResource = R.drawable.img_1; // Ảnh mặc định

                // Tạo item và thêm vào danh sách
                DetailTopCollectionItem item = new DetailTopCollectionItem(
                        imageResource,
                        title,
                        authorName,
                        timeAgo,
                        plays
                );

                item.setQuizId(quizId);

                // Nếu có URL ảnh, có thể lưu vào item để adapter tải ảnh sau
                // item.setImageUrl(imageUrl);

                quizList.add(item);

                Log.d(TAG, "Added quiz: " + title + " by " + authorName);

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing quiz JSON", e);
            } catch (Exception e) {
                Log.e(TAG, "Error processing quiz object", e);
            }
        }

        // Nếu không có quiz nào được thêm vào sau khi xử lý, sử dụng dữ liệu mẫu
        if (quizList.isEmpty()) {
            Log.d(TAG, "No quizzes could be processed from API. Using sample data.");
            loadDummyData();
            return;
        }

        // Cập nhật RecyclerView với dữ liệu thực tế
        quizAdapter = new DetailTopCollectionAdapter(quizList);
        recyclerView.setAdapter(quizAdapter);

        Log.d(TAG, "Loaded " + quizList.size() + " quizzes from API");
    }

    // Định dạng timestamp thành "X time ago"
    private String formatTimeAgo(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "Unknown time";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date past = sdf.parse(timestamp);
            Date now = new Date();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                return seconds + " seconds ago";
            } else if (minutes < 60) {
                return minutes + " minutes ago";
            } else if (hours < 24) {
                return hours + " hours ago";
            } else if (days < 7) {
                return days + " days ago";
            } else if (days < 30) {
                return (days / 7) + " weeks ago";
            } else if (days < 365) {
                return (days / 30) + " months ago";
            } else {
                return (days / 365) + " years ago";
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing timestamp: " + timestamp, e);
            return "Unknown time";
        }
    }

    // Định dạng số lượt chơi (ví dụ: 1000 -> 1K)
    private String formatPlayCount(int playCount) {
        if (playCount < 1000) {
            return playCount + " plays";
        } else if (playCount < 1000000) {
            float k = playCount / 1000f;
            return String.format(Locale.getDefault(), "%.1fK plays", k);
        } else {
            float m = playCount / 1000000f;
            return String.format(Locale.getDefault(), "%.1fM plays", m);
        }
    }

    private void loadDummyData() {
        // Hiển thị số lượng quizzes mẫu
        tvQuizCount.setText("3 Quizzo");

        // Tạo dữ liệu mẫu
        quizList = new ArrayList<>();

        DetailTopCollectionItem item1 = new DetailTopCollectionItem(
                R.drawable.img_1, "Re-Train Your Brain", "Thad Eddings", "2 weeks ago", "2.6K plays");
        item1.setQuizId(101);
        quizList.add(item1);

        DetailTopCollectionItem item2 = new DetailTopCollectionItem(
                R.drawable.img_1, "Book is a Window", "Sarojetta Ordonez", "2 months ago", "5.1K plays");
        item2.setQuizId(102);
        quizList.add(item2);

        DetailTopCollectionItem item3 = new DetailTopCollectionItem(
                R.drawable.img_1, "Back to School Quiz", "Albruna Schuster", "2 years ago", "16.4K plays");
        item3.setQuizId(103);
        quizList.add(item3);

        quizAdapter = new DetailTopCollectionAdapter(quizList);
        recyclerView.setAdapter(quizAdapter);

        Log.d(TAG, "Loaded dummy data");
    }
}