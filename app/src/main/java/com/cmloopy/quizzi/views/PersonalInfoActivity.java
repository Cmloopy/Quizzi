package com.cmloopy.quizzi.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.user.User;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etDob, etCountry, etAge;
    private ImageButton btnBack, btnCalendar, btnDropdownCountry;
    private ImageView btnEditProfile, profileImage;
    private Button btnSave;
    private Calendar calendar;
    private static final int PICK_IMAGE = 100;

    // Trạng thái chỉnh sửa
    private boolean isEditing = false;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Khởi tạo calendar
        calendar = Calendar.getInstance();

        // Ánh xạ các view
        initViews();

        // Lấy thông tin user hiện tại từ SharedPreferences
        currentUser = getCurrentUserFromPrefs();
        if (currentUser != null) {
            // Hiển thị thông tin user
            displayUserInfo();
        }

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
        btnSave = findViewById(R.id.btn_save);

        profileImage = findViewById(R.id.profile_image);

        // Ẩn nút Save khi không ở chế độ chỉnh sửa
        btnSave.setVisibility(View.GONE);
    }

    private User getCurrentUserFromPrefs() {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userJson = userPrefs.getString("currentUserJson", "");

        if (!userJson.isEmpty()) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(userJson, User.class);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private void displayUserInfo() {
        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhoneNumber());

            if (currentUser.getDateOfBirth() != null && !currentUser.getDateOfBirth().isEmpty()) {
                etDob.setText(currentUser.getDateOfBirth());
            }

            if (currentUser.getCountry() != null && !currentUser.getCountry().isEmpty()) {
                etCountry.setText(currentUser.getCountry());
            }

            etAge.setText(String.valueOf(currentUser.getAge()));

            // Nếu có URL ảnh đại diện, hiển thị ảnh đại diện
            // (Cần thêm thư viện như Glide hoặc Picasso để load ảnh)
        }
    }

    private void setupClickListeners() {
        // Quay lại màn hình trước
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
                Log.d("PersonalInfo", "Back button clicked");
            });
        }

        // Mở album để thay đổi ảnh đại diện
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                Log.d("PersonalInfo", "Edit profile button clicked");
                openGallery();
            });
        }

        // Mở dialog chọn ngày sinh
        if (btnCalendar != null) {
            btnCalendar.setOnClickListener(v -> {
                Log.d("PersonalInfo", "Calendar button clicked");
                showDatePicker();
            });
        }

        // Mở dialog chọn quốc gia
        if (btnDropdownCountry != null) {
            btnDropdownCountry.setOnClickListener(v -> {
                Log.d("PersonalInfo", "Country dropdown clicked");
                showCountrySelector();
            });
        }

        // Xử lý nút Save
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                Log.d("PersonalInfo", "Save button clicked");
                saveUserInfo();
            });
        }

        // Bật chế độ chỉnh sửa
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            // Thêm click thường thay vì long click
            toolbar.setOnClickListener(v -> {
                Log.d("PersonalInfo", "Toolbar clicked");
                toggleEditMode();
            });
        }
    }
    private void toggleEditMode() {
        isEditing = !isEditing;

        // Bật/tắt khả năng chỉnh sửa các trường
        etFullName.setEnabled(isEditing);
        etEmail.setEnabled(isEditing);
        etPhone.setEnabled(isEditing);

        // Hiển thị/ẩn nút Save
        btnSave.setVisibility(isEditing ? View.VISIBLE : View.GONE);

        // Thông báo cho người dùng biết đã bật/tắt chế độ chỉnh sửa
        Toast.makeText(this, isEditing ? "Edit mode enabled" : "Edit mode disabled", Toast.LENGTH_SHORT).show();
    }

    private void saveUserInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "No user data to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin từ các trường EditText
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String country = etCountry.getText().toString().trim();

        // Cập nhật đối tượng User
        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhoneNumber(phone);
        currentUser.setDateOfBirth(dob);
        currentUser.setCountry(country);

        // Lưu thông tin đã cập nhật vào SharedPreferences
        saveUserToPrefs(currentUser);

        // Gọi API để cập nhật thông tin lên server (nếu cần)
        // updateUserInfoToServer(currentUser);

        // Tắt chế độ chỉnh sửa
        toggleEditMode();

        Toast.makeText(this, "Information saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void saveUserToPrefs(User user) {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();

        try {
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            editor.putString("currentUserJson", userJson);
            editor.apply();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving user data", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức gọi API để cập nhật thông tin lên server (nếu cần)
    /*
    private void updateUserInfoToServer(User user) {
        UserApi userApi = RetrofitClient.getUserApi();
        Call<User> call = userApi.updateUserInfo(user.getId(), user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PersonalInfoActivity.this, "Information updated on server", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonalInfoActivity.this, "Failed to update server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

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