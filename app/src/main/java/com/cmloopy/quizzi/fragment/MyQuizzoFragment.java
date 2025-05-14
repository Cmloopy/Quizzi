package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.cmloopy.quizzi.data.api.Library.MyQuizAPI;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.HomeLibrary.MyQuizzo.QuizCollection;
import com.cmloopy.quizzi.models.HomeLibrary.MyQuizzo.QuizResponse;
import com.cmloopy.quizzi.models.TopCollectionsCategory;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuizzoFragment extends Fragment {

    private static final String TAG = "MyQuizzoFragment";
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private MaterialTextView materialTextView;
    private ProgressBar progressBar;
    private MyQuizAPI quizAPI;

    private int userId = -1;

    RecyclerView.Adapter<?> adapter = null;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_quizzo, container, false);

        // Khởi tạo API service
        quizAPI = RetrofitClient.getMyQuizApi();

        RadioGroup radioGroup = view.findViewById(R.id.tabGroup1);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            handleSearchCategoryChange(checkedId);
        });

        recyclerView = view.findViewById(R.id.rcl_view_quizzo_cls);
        materialTextView = view.findViewById(R.id.txt_title_lib_my_quizzo);
        progressBar = view.findViewById(R.id.progress_bar);

        // Mặc định chọn tab Quizzo
        radioGroup.post(() -> {
            radioGroup.check(R.id.radioLibQuizzoBtn);
            handleSearchCategoryChange(R.id.radioLibQuizzoBtn);
        });

        return view;
    }

    private void handleSearchCategoryChange(int checkedId) {
        if (checkedId == R.id.radioLibQuizzoBtn) {
            fetchUserQuizzes();
        } else if (checkedId == R.id.radioLibCollectionBtn) {
            fetchUserCollections();
        }
    }

    private void fetchUserQuizzes() {
        // Hiển thị loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Sử dụng userId thay vì FIXED_ID
        quizAPI.getUserQuizzes(userId).enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                // Ẩn loading indicator
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> quizResponses = response.body();

                    // Hiển thị số lượng quiz
                    materialTextView.setText(quizResponses.size() + " Quizzo");

                    // Chuyển đổi dữ liệu API thành model Quiz
                    List<Quiz> quizzes = convertToQuizModel(quizResponses);

                    // Thiết lập RecyclerView với danh sách quiz
                    QuizAdapter quizAdapter = new QuizAdapter(quizzes, userId);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(quizAdapter);
                } else {
                    // Xử lý lỗi API
                    String errorMessage = "Failed to load quizzes: " +
                            (response.code() != 0 ? "Error " + response.code() : "Unknown error");
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    // Sử dụng dữ liệu mẫu để hiển thị
                    setDummyQuizData();
                }
            }

            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                // Ẩn loading indicator
                progressBar.setVisibility(View.GONE);

                // Hiển thị thông báo lỗi kết nối
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Sử dụng dữ liệu mẫu để hiển thị
                setDummyQuizData();
            }
        });
    }

    private void fetchUserCollections() {
        // Hiển thị loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Sử dụng userId thay vì FIXED_ID
        quizAPI.getQuizCollectionsByAuthor(userId).enqueue(new Callback<List<QuizCollection>>() {
            @Override
            public void onResponse(Call<List<QuizCollection>> call, Response<List<QuizCollection>> response) {
                // Ẩn loading indicator
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizCollection> collections = response.body();

                    // Sử dụng userId thay vì FIXED_ID
                    List<QuizCollection> filteredCollections = new ArrayList<>();
                    for (QuizCollection collection : collections) {
                        if (collection.getAuthorId() == userId) {
                            filteredCollections.add(collection);
                        }
                    }

                    // Hiển thị số lượng collections
                    materialTextView.setText(filteredCollections.size() + " Collections");

                    // Chuyển đổi dữ liệu API thành model TopCollectionsCategory
                    List<TopCollectionsCategory> categoryList = convertToCollectionModel(filteredCollections);

                    // Thiết lập RecyclerView với danh sách collections
                    gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                    TopCollectionsCategoryAdapter collectionAdapter =
                            new TopCollectionsCategoryAdapter(requireContext(), categoryList);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(collectionAdapter);
                } else {
                    // Xử lý lỗi API
                    String errorMessage = "Failed to load collections: " +
                            (response.code() != 0 ? "Error " + response.code() : "Unknown error");
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    // Sử dụng dữ liệu mẫu để hiển thị
                    setDummyCollectionData();
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollection>> call, Throwable t) {
                // Ẩn loading indicator
                progressBar.setVisibility(View.GONE);

                // Hiển thị thông báo lỗi kết nối
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Sử dụng dữ liệu mẫu để hiển thị
                setDummyCollectionData();
            }
        });
    }

    // Chuyển đổi dữ liệu collection API thành model TopCollectionsCategory
    private List<TopCollectionsCategory> convertToCollectionModel(List<QuizCollection> collections) {
        List<TopCollectionsCategory> categoryList = new ArrayList<>();

        for (QuizCollection collection : collections) {
            // Sử dụng category làm tên hiển thị
            String categoryName = collection.getCategory();

            // Sử dụng hình ảnh mặc định nếu không có coverPhoto
            int imageResource = (collection.getCoverPhoto() != null)
                    ? R.drawable.img_02  // Thay thế bằng logic load ảnh từ URL nếu có
                    : R.drawable.img_02; // Ảnh mặc định

            // Tạo đối tượng TopCollectionsCategory
            TopCollectionsCategory category = new TopCollectionsCategory(categoryName, imageResource);

            // Đặt collectionId
            category.setCollectionId(collection.getId());

            // Log để debug
            Log.d(TAG, "Collection ID: " + collection.getId() + ", Name: " + categoryName);

            categoryList.add(category);
        }

        return categoryList;
    }

    // Hiển thị dữ liệu mẫu cho quiz khi có lỗi
    private void setDummyQuizData() {
        QuizAdapter quizAdapter = getQuizAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(quizAdapter);
        materialTextView.setText("6 Quizzo"); // Số lượng quiz mẫu
    }

    // Hiển thị dữ liệu mẫu cho collection khi có lỗi
    private void setDummyCollectionData() {
        gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        adapter = getCollectionAdapter();
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        materialTextView.setText("12 Collections"); // Số lượng collection mẫu
    }

    // Chuyển đổi dữ liệu API thành model Quiz
    private List<Quiz> convertToQuizModel(List<QuizResponse> quizResponses) {
        List<Quiz> quizzes = new ArrayList<>();

        for (QuizResponse response : quizResponses) {
            // Lấy ảnh thumbnail cho quiz
            int thumbnailResource = (response.getCoverPhoto() != null)
                    ? R.drawable.img_02
                    : R.drawable.ic_launcher_background;

            // Định dạng tiêu đề
            String title = response.getTitle();
            if (title.length() > 20) {
                title = title.substring(0, 17) + "...";
            }

            // Định dạng thời gian tạo quiz
            String createdTime = formatTimeAgo(response.getCreatedAt());

            // Định dạng số điểm (lượt chơi)
            String plays = formatScore(response.getScore());

            // Tên người tạo
            String authorName = "User " + response.getUserId();

            // Avatar của người tạo
            int authorAvatar = R.drawable.ic_launcher_background;

            // Tạo đối tượng Quiz từ dữ liệu API
            Quiz quiz = new Quiz(
                    (long) response.getId(),
                    thumbnailResource,
                    title,
                    createdTime,
                    plays,
                    authorName,
                    authorAvatar,
                    response.getDescription() != null ? response.getDescription() : ""
            );

            // Đặt id cho quiz
            quiz.setId((long)response.getId());

            // Log để debug
            Log.d(TAG, "Quiz ID: " + quiz.getId() + ", Title: " + quiz.getTitle());

            quizzes.add(quiz);
        }

        return quizzes;
    }

    // Định dạng thời gian
    private String formatTimeAgo(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            Date past = format.parse(dateString);
            Date now = new Date();

            long duration = now.getTime() - past.getTime();

            long days = TimeUnit.MILLISECONDS.toDays(duration);
            long months = days / 30;
            long years = days / 365;

            if (years > 0) {
                return years + (years == 1 ? " year ago" : " years ago");
            } else if (months > 0) {
                return months + (months == 1 ? " month ago" : " months ago");
            } else if (days > 7) {
                return (days / 7) + " weeks ago";
            } else if (days > 0) {
                return days + (days == 1 ? " day ago" : " days ago");
            } else {
                long hours = TimeUnit.MILLISECONDS.toHours(duration);
                if (hours > 0) {
                    return hours + (hours == 1 ? " hour ago" : " hours ago");
                } else {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                    return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Recently"; // Giá trị mặc định khi không thể phân tích ngày
        }
    }

    // Định dạng điểm số
    private String formatScore(int score) {
        if (score >= 1000000) {
            float m = score / 1000000f;
            return String.format(Locale.getDefault(), "%.1fM plays", m);
        } else if (score >= 1000) {
            float k = score / 1000f;
            return String.format(Locale.getDefault(), "%.1fK plays", k);
        } else {
            return score + " plays";
        }
    }

    // Các phương thức tạo dữ liệu mẫu
    private QuizAdapter getQuizAdapter() {
        List<Quiz> items = new ArrayList<>();

        // Tạo quiz 1
        Quiz quiz1 = new Quiz(1L,R.drawable.ic_launcher_background, "Quiz 1", "2 days ago", "1.2K plays",
                "User 1", R.drawable.ic_launcher_background, "Description for Quiz 1");
        quiz1.setId(1L);
        items.add(quiz1);

        // Tạo quiz 2
        Quiz quiz2 = new Quiz(1L, R.drawable.ic_launcher_background, "Quiz 2", "1 week ago", "3.5K plays",
                "User 2", R.drawable.ic_launcher_background, "Description for Quiz 2");
        quiz2.setId(2L);
        items.add(quiz2);

        // Tạo quiz 3
        Quiz quiz3 = new Quiz(1L, R.drawable.ic_launcher_background, "Quiz 3", "2 months ago", "10K plays",
                "User 3", R.drawable.ic_launcher_background, "Description for Quiz 3");
        quiz3.setId(3L);
        items.add(quiz3);

        // Tạo quiz 4
        Quiz quiz4 = new Quiz(1L,R.drawable.ic_launcher_background, "Quiz 4", "1 hour ago", "120 plays",
                "User 4", R.drawable.ic_launcher_background, "Description for Quiz 4");
        quiz4.setId(4L);
        items.add(quiz4);

        // Tạo quiz 5
        Quiz quiz5 = new Quiz(1L,R.drawable.ic_launcher_background, "Quiz 5", "3 weeks ago", "5.7K plays",
                "User 5", R.drawable.ic_launcher_background, "Description for Quiz 5");
        quiz5.setId(5L);
        items.add(quiz5);

        // Tạo quiz 6
        Quiz quiz6 = new Quiz(1L,R.drawable.ic_launcher_background, "Quiz 6", "5 days ago", "850 plays",
                "User 6", R.drawable.ic_launcher_background, "Description for Quiz 6");
        quiz6.setId(6L);
        items.add(quiz6);

        return new QuizAdapter(items, userId);
    }

    private TopCollectionsCategoryAdapter getCollectionAdapter() {
        List<TopCollectionsCategory> categoryList = new ArrayList<>();
        categoryList.add(new TopCollectionsCategory("Science", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("History", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Mathematics", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Geography", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Sports", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Movies", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Music", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Literature", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Technology", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Art", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Food", R.drawable.ic_launcher_background));
        categoryList.add(new TopCollectionsCategory("Animals", R.drawable.ic_launcher_background));
        return new TopCollectionsCategoryAdapter(requireContext(), categoryList);
    }
}