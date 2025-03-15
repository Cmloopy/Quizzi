package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.views.MainActivity;

public class ResetPasswordSuccess extends AppCompatActivity {

    private Button goToHomeButton;
    private TextView welcomeBackTitle;
    private TextView successMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password_success);

        // Initialize UI components
        initUI();

        // Set up listeners
        setupListeners();
    }

    private void initUI() {
        goToHomeButton = findViewById(R.id.goToHomeButton);
        welcomeBackTitle = findViewById(R.id.welcomeBackTitle);
        successMessage = findViewById(R.id.successMessage);
    }

    private void setupListeners() {
        goToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHome();
            }
        });
    }

    private void navigateToHome() {
        try {
            // Chuyển hướng đến MainActivity
            Intent intent = new Intent(ResetPasswordSuccess.this, MainActivity.class);

            // Sử dụng flag đơn giản hơn
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish(); // Đóng activity hiện tại
        } catch (Exception e) {
            // Log lỗi nếu có
            e.printStackTrace();

            // Giải pháp thay thế: Chuyển đến màn hình chính bằng Intent ACTION_MAIN
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // Phương thức để gọi từ CreateNewPassword sau khi tạo mật khẩu thành công
    public static void showSuccessScreen(AppCompatActivity context) {
        Intent intent = new Intent(context, ResetPasswordSuccess.class);
        context.startActivity(intent);
        // Đóng màn hình tạo mật khẩu để người dùng không thể quay lại
        context.finish();
    }

    // Ngăn người dùng sử dụng nút back để quay lại màn hình trước đó
    @Override
    public void onBackPressed() {
        // Chuyển hướng đến trang chủ thay vì cho phép quay lại màn hình trước đó
        super.onBackPressed();
        navigateToHome();
    }
}