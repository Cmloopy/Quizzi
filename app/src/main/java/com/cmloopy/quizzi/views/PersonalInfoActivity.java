package com.cmloopy.quizzi.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etDob, etCountry, etAge;
    private ImageButton btnBack, btnCalendar, btnDropdownCountry;
    private ImageView btnEditProfile, profileImage;
    private Calendar calendar;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Khởi tạo calendar
        calendar = Calendar.getInstance();

        // Ánh xạ các view
        initViews();

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etDob = findViewById(R.id.et_dob);
        etCountry = findViewById(R.id.et_country);
        etAge = findViewById(R.id.et_age);

        btnBack = findViewById(R.id.btn_back);
        btnCalendar = findViewById(R.id.btn_calendar);
        btnDropdownCountry = findViewById(R.id.btn_dropdown_country);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        profileImage = findViewById(R.id.profile_image);
    }

    private void setupClickListeners() {
        // Quay lại màn hình trước
        btnBack.setOnClickListener(v -> finish());

        // Mở album để thay đổi ảnh đại diện
        btnEditProfile.setOnClickListener(v -> openGallery());

        // Mở dialog chọn ngày sinh
        btnCalendar.setOnClickListener(v -> showDatePicker());

        // Mở dialog chọn quốc gia
        btnDropdownCountry.setOnClickListener(v -> showCountrySelector());

        // Cho phép chỉnh sửa email và số điện thoại
        etEmail.setEnabled(true);
        etPhone.setEnabled(true);
    }

    // Mở thư viện ảnh để chọn ảnh đại diện
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    // Hiển thị date picker để chọn ngày sinh
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateLabel();
                        updateAge();
                    }
                },
                1995, // Năm mặc định 1995
                11,   // Tháng mặc định (0-11), 11 = December
                27    // Ngày mặc định
        );

        // Giới hạn ngày max là ngày hiện tại
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    // Cập nhật hiển thị ngày sinh
    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        etDob.setText(dateFormat.format(calendar.getTime()));
    }

    // Tính và cập nhật tuổi dựa trên ngày sinh
    private void updateAge() {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        etAge.setText(String.valueOf(age));
    }

    // Hiển thị dialog chọn quốc gia
    private void showCountrySelector() {
        final String[] countries = {
                "United States", "Canada", "United Kingdom",
                "Australia", "Germany", "France", "Japan",
                "China", "India", "Brazil", "Vietnam"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Country");
        builder.setItems(countries, (dialog, which) -> {
            etCountry.setText(countries[which]);
        });
        builder.show();
    }

    // Xử lý kết quả trả về từ gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            // Lấy URI của ảnh đã chọn
            Uri imageUri = data.getData();

            // Hiển thị ảnh đã chọn
            profileImage.setImageURI(imageUri);

            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
        }
    }
}