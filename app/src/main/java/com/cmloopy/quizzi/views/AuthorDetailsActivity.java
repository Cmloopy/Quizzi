package com.cmloopy.quizzi.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.button.MaterialButton;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.fragment.AuthorAboutFragment;
import com.cmloopy.quizzi.fragment.AuthorQuizzoFragment;
import com.cmloopy.quizzi.fragment.AuthorCollectionsFragment;

public class AuthorDetailsActivity extends AppCompatActivity {

    private MaterialButton btnQuizzo, btnCollections, btnAbout;
    private ImageView btnBack, btnSend, btnMore;

    // Biến lưu trữ thông tin tác giả
    private String authorId;
    private String authorName;
    private String authorUsername;
    private int authorAvatar;
    private String authorAvatarUrl;

    // Biến lưu trữ các thống kê
    private int totalQuizs;
    private int totalCollections;
    private String totalPlays;
    private String totalPlayers;
    private String totalFollowers;
    private int totalFollowing;

    // Các view hiển thị thông tin
    private TextView tvAuthorName;
    private TextView tvAuthorUsername;
    private ImageView ivAuthorAvatar;
    private Button btnFollow;

    // Các view hiển thị thống kê
    private TextView tvQuizzoCount;
    private TextView tvPlaysCount;
    private TextView tvPlayersCount;
    private TextView tvCollectionsCount;
    private TextView tvFollowersCount;
    private TextView tvFollowingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_details);

        // Lấy dữ liệu tác giả từ intent
        getAuthorDataFromIntent();

        // Khởi tạo các thành phần UI
        initUI();

        // Hiển thị thông tin tác giả
        displayAuthorInfo();

        // Thiết lập fragment mặc định (About đang active trong ảnh chụp màn hình)
        loadFragment(new AuthorAboutFragment());
        updateButtonStates(btnAbout);

        // Thiết lập listeners cho các nút tab
        setupClickListeners();
    }

    private void getAuthorDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            // Log để kiểm tra dữ liệu
            Log.d("AuthorDetails", "Intent extras: " + intent.getExtras());

            // Thông tin cơ bản
            authorId = intent.getStringExtra("AUTHOR_ID");
            authorName = intent.getStringExtra("AUTHOR_NAME");
            authorUsername = intent.getStringExtra("AUTHOR_USERNAME");
            authorAvatar = intent.getIntExtra("AUTHOR_AVATAR", R.drawable.ic_launcher_background);

            Log.d("AuthorDetails", "Received author: " + authorName + ", username: " + authorUsername);

            if (intent.hasExtra("AUTHOR_AVATAR_URL")) {
                authorAvatarUrl = intent.getStringExtra("AUTHOR_AVATAR_URL");
            }

            // Thống kê
            totalQuizs = intent.getIntExtra("AUTHOR_TOTAL_QUIZS", 265);
            totalCollections = intent.getIntExtra("AUTHOR_TOTAL_COLLECTIONS", 49);
            totalPlays = intent.getStringExtra("AUTHOR_TOTAL_PLAYS") != null ?
                    intent.getStringExtra("AUTHOR_TOTAL_PLAYS") : "32M";
            totalPlayers = intent.getStringExtra("AUTHOR_TOTAL_PLAYERS") != null ?
                    intent.getStringExtra("AUTHOR_TOTAL_PLAYERS") : "274M";
            totalFollowers = intent.getStringExtra("AUTHOR_TOTAL_FOLLOWERS") != null ?
                    intent.getStringExtra("AUTHOR_TOTAL_FOLLOWERS") : "927.3K";
            totalFollowing = intent.getIntExtra("AUTHOR_TOTAL_FOLLOWING", 128);

            Log.d("AuthorDetails", "Stats - Quizs: " + totalQuizs + ", Collections: " + totalCollections);
        }
    }

    @SuppressLint("WrongViewCast")
    private void initUI() {
        // Các nút trên thanh công cụ
        btnBack = findViewById(R.id.btn_back);
        btnSend = findViewById(R.id.btn_send);
        btnMore = findViewById(R.id.btn_more);

        // Các nút tab
        btnQuizzo = findViewById(R.id.btn_quizzo);
        btnCollections = findViewById(R.id.btn_collections);
        btnAbout = findViewById(R.id.btn_about);

        // Khởi tạo các view để hiển thị thông tin tác giả
        tvAuthorName = findViewById(R.id.profile_name);
        tvAuthorUsername = findViewById(R.id.profile_username);
        ivAuthorAvatar = findViewById(R.id.profile_image);
        btnFollow = findViewById(R.id.btn_follow);

        // Khởi tạo các view để hiển thị thống kê
        tvQuizzoCount = findViewById(R.id.quizzo_count);
        tvPlaysCount = findViewById(R.id.plays_count);
        tvPlayersCount = findViewById(R.id.players_count);
        tvCollectionsCount = findViewById(R.id.collections_count);
        tvFollowersCount = findViewById(R.id.followers_count);
        tvFollowingCount = findViewById(R.id.following_count);
    }

    private void displayAuthorInfo() {
        Log.d("AuthorDetails", "Displaying author info: " + authorName);

        // Hiển thị thông tin tác giả cơ bản
        if (tvAuthorName != null && authorName != null) {
            tvAuthorName.setText(authorName);
            Log.d("AuthorDetails", "Set author name: " + authorName);
        } else {
            Log.e("AuthorDetails", "Failed to set author name. TVAuthorName: " + (tvAuthorName != null) + ", authorName: " + authorName);
        }

        if (tvAuthorUsername != null && authorUsername != null) {
            tvAuthorUsername.setText("@" + authorUsername);
            Log.d("AuthorDetails", "Set author username: @" + authorUsername);
        }

        if (ivAuthorAvatar != null) {
            ivAuthorAvatar.setImageResource(authorAvatar);
            Log.d("AuthorDetails", "Set author avatar resource: " + authorAvatar);
        }

        // Hiển thị các thống kê
        if (tvQuizzoCount != null) {
            tvQuizzoCount.setText(String.valueOf(totalQuizs));
            Log.d("AuthorDetails", "Set quizzo count: " + totalQuizs);
        } else {
            Log.e("AuthorDetails", "tvQuizzoCount is null");
        }

        if (tvCollectionsCount != null) {
            tvCollectionsCount.setText(String.valueOf(totalCollections));
            Log.d("AuthorDetails", "Set collections count: " + totalCollections);
        } else {
            Log.e("AuthorDetails", "tvCollectionsCount is null");
        }

        if (tvPlaysCount != null && totalPlays != null) {
            tvPlaysCount.setText(totalPlays);
            Log.d("AuthorDetails", "Set plays count: " + totalPlays);
        } else {
            Log.e("AuthorDetails", "tvPlaysCount issue - null?: " + (tvPlaysCount == null) + ", totalPlays null?: " + (totalPlays == null));
        }

        if (tvPlayersCount != null && totalPlayers != null) {
            tvPlayersCount.setText(totalPlayers);
            Log.d("AuthorDetails", "Set players count: " + totalPlayers);
        }

        if (tvFollowersCount != null && totalFollowers != null) {
            tvFollowersCount.setText(totalFollowers);
            Log.d("AuthorDetails", "Set followers count: " + totalFollowers);
        }

        if (tvFollowingCount != null) {
            tvFollowingCount.setText(String.valueOf(totalFollowing));
            Log.d("AuthorDetails", "Set following count: " + totalFollowing);
        }
    }

    // Thêm phương thức setupClickListeners
    private void setupClickListeners() {
        // Các nút trên thanh công cụ
        btnBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> {
            // Triển khai chức năng chia sẻ
        });
        btnMore.setOnClickListener(v -> {
            // Triển khai menu tùy chọn khác
        });

        // Nút Follow
        if (btnFollow != null) {
            btnFollow.setOnClickListener(v -> {
                // Toggle trạng thái Follow
                if (btnFollow.getText().toString().equals("Follow")) {
                    btnFollow.setText("Following");
                    // Thêm logic follow tác giả
                } else {
                    btnFollow.setText("Follow");
                    // Thêm logic unfollow tác giả
                }
            });
        }

        // Các nút tab
        btnQuizzo.setOnClickListener(v -> {
            loadFragment(new AuthorQuizzoFragment());
            updateButtonStates(btnQuizzo);
        });

        btnCollections.setOnClickListener(v -> {
            loadFragment(new AuthorCollectionsFragment());
            updateButtonStates(btnCollections);
        });

        btnAbout.setOnClickListener(v -> {
            loadFragment(new AuthorAboutFragment());
            updateButtonStates(btnAbout);
        });
    }

    // Thêm phương thức updateButtonStates
    private void updateButtonStates(Button activeButton) {
        // Reset tất cả các nút về trạng thái không hoạt động
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Nếu SDK >= 23 (Marshmallow)
            btnQuizzo.setBackgroundTintList(getColorStateList(R.color.light_gray));
            btnQuizzo.setTextColor(getColor(R.color.purple));

            btnCollections.setBackgroundTintList(getColorStateList(R.color.light_gray));
            btnCollections.setTextColor(getColor(R.color.purple));

            btnAbout.setBackgroundTintList(getColorStateList(R.color.light_gray));
            btnAbout.setTextColor(getColor(R.color.purple));

            // Thiết lập trạng thái nút đang hoạt động
            activeButton.setBackgroundTintList(getColorStateList(R.color.purple));
            activeButton.setTextColor(getColor(R.color.white));
        } else {
            // Nếu SDK < 23
            btnQuizzo.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray));
            btnQuizzo.setTextColor(getResources().getColor(R.color.purple));

            btnCollections.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray));
            btnCollections.setTextColor(getResources().getColor(R.color.purple));

            btnAbout.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray));
            btnAbout.setTextColor(getResources().getColor(R.color.purple));

            // Thiết lập trạng thái nút đang hoạt động
            activeButton.setBackgroundTintList(getResources().getColorStateList(R.color.purple));
            activeButton.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void loadFragment(Fragment fragment) {
        // Truyền dữ liệu tác giả cho fragment
        Bundle args = new Bundle();
        args.putString("AUTHOR_ID", authorId);
        args.putString("AUTHOR_NAME", authorName);
        args.putString("AUTHOR_USERNAME", authorUsername);
        args.putInt("AUTHOR_AVATAR", authorAvatar);

        // Truyền thống kê cho fragment
        args.putInt("AUTHOR_TOTAL_QUIZS", totalQuizs);
        args.putInt("AUTHOR_TOTAL_COLLECTIONS", totalCollections);
        args.putString("AUTHOR_TOTAL_PLAYS", totalPlays);
        args.putString("AUTHOR_TOTAL_PLAYERS", totalPlayers);
        args.putString("AUTHOR_TOTAL_FOLLOWERS", totalFollowers);
        args.putInt("AUTHOR_TOTAL_FOLLOWING", totalFollowing);

        // Truyền avatarUrl nếu có
        if (authorAvatarUrl != null && !authorAvatarUrl.isEmpty()) {
            args.putString("AUTHOR_AVATAR_URL", authorAvatarUrl);
        }

        fragment.setArguments(args);

        // Tải fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}