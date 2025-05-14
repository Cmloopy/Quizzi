package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;

public class SetiingActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout personalInfoLayout;
    private LinearLayout musicEffectsLayout;
    private LinearLayout logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setiing);

        // Khởi tạo Views
        initViews();

        // Thiết lập listeners
        setupListeners();

        // Cấu hình edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        personalInfoLayout = findViewById(R.id.personal_info_layout);
        musicEffectsLayout = findViewById(R.id.music_effects_layout);
        logoutLayout = findViewById(R.id.logout_layout);
    }

    private void setupListeners() {
        // Nút Back - quay lại màn hình trước
        btnBack.setOnClickListener(v -> onBackPressed());

        // Personal Info - mở màn hình thông tin cá nhân
        personalInfoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SetiingActivity.this, PersonalInfoActivity.class);
            startActivity(intent);
        });

        // Music & Effects - mở màn hình âm thanh và hiệu ứng
        musicEffectsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SetiingActivity.this, MusicEffectsActivity.class);
            startActivity(intent);
        });

        // Logout - xử lý đăng xuất
        logoutLayout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            performLogout();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

        private void performLogout() {
            // Xóa thông tin đăng nhập từ SharedPreferences
            clearUserData();

            // Chuyển về màn hình đăng nhập và xóa toàn bộ activity stack
            Intent intent = new Intent(SetiingActivity.this, SignInForm.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        }

    private void clearUserData() {
        // Xóa dữ liệu đăng nhập từ SharedPreferences
        SharedPreferences signInPrefs = getSharedPreferences("SignInPrefs", MODE_PRIVATE);
        SharedPreferences.Editor signInEditor = signInPrefs.edit();
        signInEditor.clear();
        signInEditor.apply();

        // Xóa dữ liệu người dùng từ UserPrefs
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.clear();
        userEditor.apply();
    }

}