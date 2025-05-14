package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.TopCollectionsCategory;
import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.views.FollowActivity;
import com.cmloopy.quizzi.views.PersonalInfoActivity;
import com.cmloopy.quizzi.views.SetiingActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    UserApi userApi = RetrofitClient.getUserApi();

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private MaterialTextView materialTextView;
    private MaterialTextView followerBtn;
    private ShapeableImageView btnSetting;

    // Thêm các MaterialTextView cho hiển thị thông tin user
    private MaterialTextView usernameTextView;    // username/@account
    private MaterialTextView quizzosCountTextView;  // số lượng quizzes
    private MaterialTextView playsCountTextView;    // số lượng plays
    private MaterialTextView playersCountTextView;  // số lượng players
    private MaterialTextView collectionsCountTextView; // số lượng collections
    private MaterialTextView followersCountTextView;   // số lượng followers
    private MaterialTextView followingCountTextView;   // số lượng following
    private MaterialButton btnEditProfile;


    // Lưu trữ thông tin user
    private User userData;
    private int userId;

    RecyclerView.Adapter<?> adapter = null;

    public static ProfileFragment newInstance(int idUser) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("userId", idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getInt("userId", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo các view
        RadioGroup radioGroup = view.findViewById(R.id.tabGroup1);
        recyclerView = view.findViewById(R.id.rcl_view_profile);
        materialTextView = view.findViewById(R.id.txt_title_lib_my_quizzo_profile);
        btnSetting = view.findViewById(R.id.btn_setting);

        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        // Tham chiếu đến các view để hiển thị thông tin người dùng
        followerBtn = view.findViewById(R.id.follower_btn);

        // Các TextView hiển thị thống kê
        // Vì XML không có ID cho từng TextView cụ thể, chúng ta cần tìm chúng dựa vào vị trí
        // Trong trường hợp lý tưởng, chúng ta nên thêm các ID vào file XML

        // Tìm các LinearLayout chứa thông tin thống kê
        LinearLayout statsLayout = view.findViewById(R.id.linearLayout3);
        if (statsLayout != null) {
            // Hàng thứ nhất (Quizzo, Plays, Player)
            LinearLayout row1 = (LinearLayout) statsLayout.getChildAt(0);
            if (row1 != null) {
                quizzosCountTextView = (MaterialTextView) row1.getChildAt(0);
                playsCountTextView = (MaterialTextView) row1.getChildAt(1);
                playersCountTextView = (MaterialTextView) row1.getChildAt(2);
            }

            // Hàng thứ hai (Collection, Follower, Following)
            LinearLayout row2 = (LinearLayout) statsLayout.getChildAt(1);
            if (row2 != null) {
                collectionsCountTextView = (MaterialTextView) row2.getChildAt(0);
                followersCountTextView = (MaterialTextView) row2.getChildAt(1);
                followingCountTextView = (MaterialTextView) row2.getChildAt(2);
            }
        }

        // Tìm TextView hiển thị username/@account
        LinearLayout userInfoLayout = view.findViewById(R.id.linearLayout2);
        if (userInfoLayout != null) {
            LinearLayout nameContainer = (LinearLayout) userInfoLayout.getChildAt(1);
            if (nameContainer != null && nameContainer.getChildCount() > 1) {
                // TextView thứ hai trong container là email/username
                usernameTextView = (MaterialTextView) nameContainer.getChildAt(1);
            }
        }

        // Lấy thông tin người dùng từ SharedPreferences
        userData = getCurrentUserFromPrefs();
        if (userData != null) {
            // Nếu có dữ liệu trong SharedPreferences, hiển thị ngay lập tức
            updateUserInfo(userData);
            Log.d(TAG, "Đã lấy thông tin user từ SharedPreferences");
        } else {
            // Nếu không có dữ liệu trong SharedPreferences, gọi API
            Log.d(TAG, "Không tìm thấy dữ liệu user trong SharedPreferences, gọi API");
            int userId = getCurrentUserId();

            if (userId != -1) {
                fetchUserInfo(userId);
            } else {
                // Sử dụng userId từ arguments nếu không có trong SharedPreferences
                userId = getArguments().getInt("userId", -1);
                if (userId != -1) {
                    fetchUserInfo(userId);
                } else {
                    Toast.makeText(requireContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        }

        radioGroup.setOnCheckedChangeListener((radioGroup1, checkId) -> {
            handleSearchCategoryChange(checkId);
        });

        radioGroup.post(() -> {
            radioGroup.check(R.id.radioLibQuizzoBtn);
            handleSearchCategoryChange(R.id.radioLibQuizzoBtn);
        });

        followerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FollowActivity.class);
            startActivity(intent);
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SetiingActivity.class);
            startActivity(intent);
        });

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PersonalInfoActivity.class);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Lấy thông tin User từ SharedPreferences sử dụng Gson
     */
    private User getCurrentUserFromPrefs() {
        SharedPreferences userPrefs = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        String userJson = userPrefs.getString("currentUserJson", "");

        if (!userJson.isEmpty()) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(userJson, User.class);
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi parse JSON user: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * Lấy userId từ SharedPreferences
     */
    private int getCurrentUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        return sharedPreferences.getInt("current_userId", -1); // -1 là giá trị mặc định nếu không tìm thấy
    }

    /**
     * Gọi API để lấy thông tin người dùng
     */
    private void fetchUserInfo(int userId) {
        Call<User> call = userApi.getInfoUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userData = response.body();
                    // Cập nhật UI
                    updateUserInfo(userData);
                    // Lưu dữ liệu vào SharedPreferences
                    saveUserToPrefs(userData);
                    Log.d(TAG, "Đã lấy và lưu thông tin user từ API");
                } else {
                    Log.e(TAG, "Lỗi khi lấy thông tin người dùng: " + response.code());
                    Toast.makeText(requireContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Lỗi API: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Lưu thông tin User vào SharedPreferences
     */
    private void saveUserToPrefs(User user) {
        SharedPreferences userPrefs = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();

        try {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            editor.putString("currentUserJson", userJson);
            editor.apply();
            Log.d(TAG, "Đã lưu thông tin user vào SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lưu user JSON: " + e.getMessage());
        }
    }

    /**
     * Cập nhật UI với thông tin người dùng
     */
    private void updateUserInfo(User user) {
        // Cập nhật tên người dùng
        String displayName = (user.getFullName() != null && !user.getFullName().isEmpty())
                ? user.getFullName()
                : user.getUsername();

        if (followerBtn != null) {
            followerBtn.setText(displayName);
        }

        // Cập nhật username/email
        if (usernameTextView != null) {
            usernameTextView.setText("@" + user.getUsername());
        }

        // Cập nhật số lượng quizzes
        if (quizzosCountTextView != null) {
            quizzosCountTextView.setText(user.getTotalQuizs() + "\nQuizzo");
        }

        // Cập nhật số lượng plays
        if (playsCountTextView != null) {
            playsCountTextView.setText(formatNumber(user.getTotalPlays()) + "\nPlays");
        }

        // Cập nhật số lượng players
        if (playersCountTextView != null) {
            playersCountTextView.setText(formatNumber(user.getTotalPlayers()) + "\nPlayer");
        }

        // Cập nhật số lượng collections
        if (collectionsCountTextView != null) {
            collectionsCountTextView.setText(user.getTotalCollections() + "\nCollection");
        }

        // Cập nhật số lượng followers
        if (followersCountTextView != null) {
            followersCountTextView.setText(formatNumber(user.getTotalFollowers()) + "\nFollower");
        }

        // Cập nhật số lượng following
        if (followingCountTextView != null) {
            followingCountTextView.setText(formatNumber(user.getTotalFollowees()) + "\nFollowing");
        }

        // Cập nhật tiêu đề quizzes
        if (materialTextView != null) {
            materialTextView.setText(user.getTotalQuizs() + " Quizzo");
        }
    }

    /**
     * Format số lớn thành dạng K, M (ví dụ: 1500 -> 1.5K, 1500000 -> 1.5M)
     */
    private String formatNumber(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1000000) {
            float result = number / 1000f;
            return String.format("%.1fK", result);
        } else {
            float result = number / 1000000f;
            return String.format("%.1fM", result);
        }
    }

    private void handleSearchCategoryChange(int checkedId) {
        if (checkedId == R.id.radioLibQuizzoBtn) {
            // Cập nhật tiêu đề dựa trên thông tin người dùng thực tế
            if (userData != null) {
                materialTextView.setText(userData.getTotalQuizs() + " Quizzo");
            }
            adapter = this.getQuizAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);
        } else if (checkedId == R.id.radioLibCollectionBtn) {
            // Cập nhật tiêu đề dựa trên thông tin người dùng thực tế
            if (userData != null) {
                materialTextView.setText(userData.getTotalCollections() + " Collections");
            } else {
                materialTextView.setText("7 Collections");
            }
            gridLayoutManager = new GridLayoutManager(requireContext(), 2);
            adapter = this.getCollectionAdapter();
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    private QuizAdapter getQuizAdapter() {
        // Đây là nơi để lấy dữ liệu quiz từ API nếu có
        List<Quiz> items = new ArrayList<>();

        items.add(new Quiz(1L, R.drawable.ic_launcher_background, "Get Smarter with Prod...",
                "2 months ago", "5.5K plays", "Titus Kitamura", R.drawable.ic_launcher_background, "16 Qs"));

        // Thêm các quiz khác
        items.add(new Quiz(1L, R.drawable.ic_launcher_background, "Great Ideas Come from...",
                "6 months ago", "10.3K plays", "Alfonzo Schuessler", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(1L,R.drawable.ic_launcher_background, "Having Fun & Always S...",
                "2 years ago", "18.5K plays", "Daryl Nehls", R.drawable.ic_launcher_background, "12 Qs"));

        items.add(new Quiz(1L, R.drawable.ic_launcher_background, "Can You Imagine, Worl...",
                "3 months ago", "4.9K plays", "Edgar Torrey", R.drawable.ic_launcher_background, "20 Qs"));

        items.add(new Quiz(1L,R.drawable.ic_launcher_background, "Back to School, Get Sm...",
                "1 year ago", "12.4K plays", "Darcel Ballentine", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(1L,R.drawable.ic_launcher_background, "What is Your Favorite ...",
                "5 months ago", "6.2K plays", "Elmer Laverty", R.drawable.ic_launcher_background, "16 Qs"));

        return new QuizAdapter(items, userId);
    }

    private TopCollectionsCategoryAdapter getCollectionAdapter() {
        // Đây cũng là nơi để lấy dữ liệu collections từ API nếu có
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

        return new TopCollectionsCategoryAdapter(requireContext(), categoryList);
    }
}