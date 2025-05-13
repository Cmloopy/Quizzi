package com.cmloopy.quizzi.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
    // Thêm khai báo biến authorAvatarUrl
    private String authorAvatarUrl;
    // Thêm biến để lưu trữ thông tin tác giả
    private String authorId;
    private String authorName;
    private String authorUsername;
    private int authorAvatar;

    // Thêm các view để hiển thị thông tin tác giả
    private TextView tvAuthorName;
    private TextView tvAuthorUsername;
    private ImageView ivAuthorAvatar;
    private Button btnFollow;

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
            authorId = intent.getStringExtra("AUTHOR_ID");
            authorName = intent.getStringExtra("AUTHOR_NAME");
            authorUsername = intent.getStringExtra("AUTHOR_USERNAME");
            authorAvatar = intent.getIntExtra("AUTHOR_AVATAR", R.drawable.ic_launcher_background);
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

        // Khởi tạo các view để hiển thị thông tin tác giả theo ID trong layout
        tvAuthorName = findViewById(R.id.profile_name);
        tvAuthorUsername = findViewById(R.id.profile_username);
        ivAuthorAvatar = findViewById(R.id.profile_image);
        btnFollow = findViewById(R.id.btn_follow);
    }

    private void displayAuthorInfo() {
        // Hiển thị thông tin tác giả nếu có
        if (authorName != null && tvAuthorName != null) {
            tvAuthorName.setText(authorName);
        }

        if (authorUsername != null && tvAuthorUsername != null) {
            tvAuthorUsername.setText("@" + authorUsername);
        }

        if (ivAuthorAvatar != null) {
            ivAuthorAvatar.setImageResource(authorAvatar);
        }
    }

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

    private void loadFragment(Fragment fragment) {
        // Truyền dữ liệu tác giả cho fragment
        Bundle args = new Bundle();
        args.putString("AUTHOR_ID", authorId);
        args.putString("AUTHOR_NAME", authorName);
        args.putString("AUTHOR_USERNAME", authorUsername);
        args.putInt("AUTHOR_AVATAR", authorAvatar);

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
}