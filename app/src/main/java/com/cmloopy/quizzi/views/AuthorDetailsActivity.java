package com.cmloopy.quizzi.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_details);

        // Khởi tạo các thành phần UI
        initUI();

        // Thiết lập fragment mặc định (About đang active trong ảnh chụp màn hình)
        loadFragment(new AuthorAboutFragment());
        updateButtonStates(btnAbout);

        // Thiết lập listeners cho các nút tab
        setupClickListeners();
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

        // Bỏ đi các phương thức setStrokeWidth và setStrokeColor nếu không dùng MaterialButton
    }
}