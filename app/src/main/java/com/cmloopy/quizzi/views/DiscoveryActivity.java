package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.quiz.QuizResponse;

import java.io.IOException;
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

public class DiscoveryActivity extends AppCompatActivity {

    private static final String TAG = "DiscoveryActivity";

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private ProgressBar progressBar;
    private QuizAPI quizApi;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        userId = getIntent().getIntExtra("userId",-1);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_with_search_view);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Discover");
        }

        // Tìm và khởi tạo progress bar
        progressBar = findViewById(R.id.progressBar);
        // Nếu không tìm thấy progressBar trong layout, chúng ta sẽ ghi log cảnh báo
        if (progressBar == null) {
            Log.w(TAG, "progressBar không được tìm thấy trong layout, không có chỉ báo tải");
        }

        // Khởi tạo API client
        quizApi = RetrofitClient.getQuizCreateApi();

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sử dụng dữ liệu mẫu ban đầu
        adapter = new QuizAdapter(Collections.emptyList(), userId);
        recyclerView.setAdapter(adapter);

        // Lấy danh sách quizzes từ API
        fetchQuizzes();
    }

    private void fetchQuizzes() {
        showLoading(true);

        if (quizApi == null) {
            Log.e(TAG, "quizApi là null!");
            showLoading(false);
            showError("Khởi tạo API client thất bại");
            return;
        }

        Log.d(TAG, "Đang lấy danh sách quizzes...");
        Call<List<QuizResponse>> call = quizApi.getAllQuizzes();

        call.enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                showLoading(false);
                Log.d(TAG, "Đã nhận phản hồi, mã: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> allQuizzes = response.body();

                    // Lọc chỉ lấy các quiz có visible = true
                    List<QuizResponse> visibleQuizzes = new ArrayList<>();
                    for (QuizResponse quiz : allQuizzes) {
                        if (quiz.isVisible()) {
                            visibleQuizzes.add(quiz);
                        }
                    }

                    if (visibleQuizzes.size() > 0) {
                        // Sắp xếp quiz theo score (cao nhất trước) giống như trong HomeFragment
                        Collections.sort(visibleQuizzes, new Comparator<QuizResponse>() {
                            @Override
                            public int compare(QuizResponse q1, QuizResponse q2) {
                                Integer score1 = q1.getScore() != null ? q1.getScore() : 0;
                                Integer score2 = q2.getScore() != null ? q2.getScore() : 0;
                                return Integer.compare(score2, score1);
                            }
                        });

                        // Chuyển đổi đối tượng QuizResponse thành đối tượng Quiz theo cấu trúc của bạn
                        List<Quiz> quizList = new ArrayList<>();

                        for (QuizResponse quizResponse : visibleQuizzes) {
                            // Định dạng ngày
                            String formattedDate = "Unknown date";
                            if (quizResponse.getCreatedAt() != null) {
                                try {
                                    // Chuyển đổi String thành Date
                                    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                    Date createdDate = apiFormat.parse(quizResponse.getCreatedAt());

                                    if (createdDate != null) {
                                        formattedDate = getRelativeTimeSpan(createdDate);
                                    }
                                } catch (ParseException e) {
                                    Log.e(TAG, "Lỗi khi phân tích ngày: " + e.getMessage());
                                }
                            }

                            // Định dạng score để hiển thị là "plays"
                            String plays = quizResponse.getScore() != null ? quizResponse.getScore() + "K plays" : "0K plays";

                            // Tạo đối tượng Quiz từ QuizResponse
                            // Sử dụng constructor và các phương thức của lớp Quiz
                            Quiz quiz = new Quiz(
                                    quizResponse.getId(),
                                    R.drawable.ic_launcher_background, // imageResource
                                    quizResponse.getTitle() != null ? quizResponse.getTitle() : "Không có tiêu đề", // title
                                    formattedDate, // date
                                    plays, // plays
                                    "User #" + quizResponse.getUserId(), // author
                                    R.drawable.ic_launcher_background, // authorAvatarResource
                                    quizResponse.getDescription() != null ? quizResponse.getDescription() : "Không có mô tả" // description
                            );

                            quizList.add(quiz);
                        }

                        // Cập nhật adapter với danh sách quiz mới
                        adapter.updateQuizzes(quizList);
                    } else {
                        Log.d(TAG, "Không tìm thấy quiz nào có thể hiển thị");
                        showError("Không tìm thấy quiz nào");
                    }
                } else {
                    Log.e(TAG, "Lỗi API: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Nội dung lỗi: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    showError("Không thể tải danh sách quiz");
                }
            }

            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Lỗi mạng: " + t.getMessage(), t);
                showError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // Phương thức để tạo chuỗi thời gian tương đối (ví dụ: "2 tháng trước")
    private String getRelativeTimeSpan(Date date) {
        Date now = new Date();
        long diffInMillis = now.getTime() - date.getTime();

        // Chuyển đổi milliseconds thành các đơn vị thời gian
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long months = days / 30; // Gần đúng
        long years = days / 365; // Gần đúng

        if (years > 0) {
            return years == 1 ? "1 year ago" : years + " years ago";
        } else if (months > 0) {
            return months == 1 ? "1 month ago" : months + " months ago";
        } else if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else if (hours > 0) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (minutes > 0) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        } else {
            return "Just now";
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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