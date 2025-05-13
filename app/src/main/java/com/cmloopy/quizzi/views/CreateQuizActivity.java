package com.cmloopy.quizzi.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.views.QuizCreate.after.QuizCreateActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateQuizActivity extends AppCompatActivity {

    // UI Components
    private MaterialCardView cardCoverImage;
    private LinearLayout layoutCoverPlaceholder;
    private ImageView ivCoverIcon, ivSelectedCover;
    private EditText etTitle, etDescription, etKeyword;
    private Spinner spinnerCollection, spinnerTheme, spinnerVisibility, spinnerQuestionVisibility;
    private Button btnAddQuestion;
    private ImageButton btnClose, btnMore;
    private FlexboxLayout chipContainer;

    // Variables
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CODE = 1000;
    private Uri selectedImageUri = null;
    private List<String> keywordsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_quiz);

        int idUser = getIntent().getIntExtra("idUser",-1);

        // Setup Edge-to-Edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_close), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initializeViews();

        // Setup click listeners
        setupClickListeners(idUser);

        // Setup spinners
        setupSpinners();

        // Load saved keywords
        loadKeywordsFromPrefs();
    }

    private void initializeViews() {
        // Find views by ID
        cardCoverImage = findViewById(R.id.card_cover_image);
        layoutCoverPlaceholder = findViewById(R.id.layout_cover_placeholder);
        ivCoverIcon = findViewById(R.id.iv_cover_icon);
        ivSelectedCover = findViewById(R.id.iv_selected_cover);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_desc_title);
        etKeyword = findViewById(R.id.et_keyword);
        chipContainer = findViewById(R.id.chipContainer);

        spinnerCollection = findViewById(R.id.spinner_collection);
        spinnerTheme = findViewById(R.id.spinner_theme);
        spinnerVisibility = findViewById(R.id.spinner_visibility);
        spinnerQuestionVisibility = findViewById(R.id.spinner_question_visibility);

        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnClose = findViewById(R.id.btn_close);
        btnMore = findViewById(R.id.btn_more);
    }

    private void setupClickListeners(int idUser) {
        // Card cover image click
        cardCoverImage.setOnClickListener(v -> {
            /*if (checkPermission()) {
                openImagePicker();
            } else {
                requestPermission();
            }*/
            openImagePicker();
        });

        // Close button
        btnClose.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        // More options button
        btnMore.setOnClickListener(v -> {
            showMoreOptionsMenu();
        });

        // Add question button
        btnAddQuestion.setOnClickListener(v -> {
            /*if (validateForm()) {
                saveQuizAndAddQuestion();
            }*/
            saveQuizAndAddQuestion(idUser);
        });

        // Keyword input handling
        etKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {

                String keyword = etKeyword.getText().toString().trim();
                if (!keyword.isEmpty() && !keywordsList.contains(keyword)) {
                    keywordsList.add(keyword);
                    addChip(keyword);
                    etKeyword.setText("");
                }
                return true;
            }
            return false;
        });
    }

    private void setupSpinners() {
        // Collection Spinner
        String[] collections = {"Select Collection", "My Collection", "Public Collection", "Work", "School"};
        ArrayAdapter<String> collectionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, collections);
        collectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCollection.setAdapter(collectionAdapter);
        spinnerCollection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Theme Spinner
        String[] themes = {"Quizzo Default", "Colorful", "Minimal", "Dark", "Light"};
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, themes);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);
        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Visibility Spinner
        String[] visibilities = {"Only Me", "Friends", "Public"};
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, visibilities);
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVisibility.setAdapter(visibilityAdapter);
        spinnerVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Question Visibility Spinner
        String[] questionVisibilities = {"Only Me", "Friends", "Public"};
        ArrayAdapter<String> questionVisibilityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, questionVisibilities);
        questionVisibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestionVisibility.setAdapter(questionVisibilityAdapter);
        spinnerQuestionVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Permission handling methods
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {

            }
        }
    }

    // Image picker methods
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // Hide placeholder content and show selected image
            layoutCoverPlaceholder.setVisibility(View.GONE);
            ivSelectedCover.setVisibility(View.VISIBLE);

            // Load the image using an image loading library
            // For example with Glide:
            // Glide.with(this).load(selectedImageUri).centerCrop().into(ivSelectedCover);

            // Alternatively, for simple cases, you can use:
            ivSelectedCover.setImageURI(selectedImageUri);

            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    // UI interaction methods
    private void showExitConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Leave without saving?")
                .setMessage("Your quiz draft will be discarded.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Leave", (dialog, which) -> finish())
                .show();
    }

    private void showMoreOptionsMenu() {
        String[] options = {"Duplicate", "Delete", "Help"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Duplicate
                            Toast.makeText(this, "Duplicate selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 1: // Delete
                            showDeleteConfirmationDialog();
                            break;
                        case 2: // Help
                            Toast.makeText(this, "Help selected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Quiz?")
                .setMessage("This action cannot be undone.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete quiz logic would go here
                    Toast.makeText(this, "Quiz deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Check title
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Please enter a title");
            isValid = false;
        }

        // Check if a collection is selected (not the first item)
        if (spinnerCollection.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a collection", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void saveQuiz() {
        if (validateForm()) {
            // Save quiz logic would go here
            // This would typically involve creating a Quiz object and saving it to a database

            // Save keywords
            saveKeywordsToPrefs();

            Toast.makeText(this, "Quiz saved", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveQuizAndAddQuestion(int idUser) {
        saveKeywordsToPrefs();
        QuizzApi quizzApi = RetrofitClient.getQuizzApi();
        String titles = etTitle.getText().toString().trim();
        String des = etDescription.getText().toString().trim();
        String key = etKeyword.getText().toString().trim();
        String visiblee = "true";
        String visibleQues = "true";
        String shuffer = "false";
        File file = getFileFromUri(selectedImageUri);
        if (file == null) {
            Log.e("Upload", "File is null");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("coverPhotoFile", file.getName(), requestFile);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), idUser + "");
        RequestBody quizCollectionId = RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titles);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"),  des);
        RequestBody keyword = RequestBody.create(MediaType.parse("text/plain"), key);
        RequestBody visible = RequestBody.create(MediaType.parse("text/plain"), visiblee);
        RequestBody visibleQuizQuestion = RequestBody.create(MediaType.parse("text/plain"), visibleQues);
        RequestBody shuffle = RequestBody.create(MediaType.parse("text/plain"), shuffer);

        Call<QuizResponse> call = quizzApi.uploadQuiz(
                userId, quizCollectionId, title, description, keyword,
                visible, visibleQuizQuestion, shuffle, filePart
        );

        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateQuizActivity.this, "Create Quizz Successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Create Quiz", "Success");
                    Intent intent;
                    intent = new Intent(CreateQuizActivity.this, QuizCreateActivity.class);
                    intent.putExtra("quizId", response.body().getId());
                    startActivity(intent);
                } else {
                    Log.e("Upload", "Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {

            }
        });
    }

    // Phương thức để thêm chip
    private void addChip(String keyword) {
        Chip chip = new Chip(this);
        chip.setText(keyword);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(true);

        // Thiết lập màu sắc và kiểu dáng
        chip.setChipBackgroundColorResource(R.color.chip_background);
        chip.setTextColor(getResources().getColor(R.color.chip_text));

        // Thêm viền
        chip.setChipStrokeWidth(1);
        chip.setChipStrokeColorResource(R.color.chip_stroke);

        // Xử lý khi click vào icon đóng
        chip.setOnCloseIconClickListener(v -> {
            chipContainer.removeView(chip);
            keywordsList.remove(keyword);
        });

        // Thêm chip vào container
        chipContainer.addView(chip);
    }

    // Phương thức để lưu từ khóa vào SharedPreferences
    private void saveKeywordsToPrefs() {
        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Tạo chuỗi JSON từ danh sách
        JSONArray jsonArray = new JSONArray(keywordsList);
        editor.putString("saved_keywords", jsonArray.toString());
        editor.apply();
    }

    // Phương thức để tải từ khóa từ SharedPreferences
    private void loadKeywordsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        String jsonKeywords = prefs.getString("saved_keywords", "[]");

        try {
            JSONArray jsonArray = new JSONArray(jsonKeywords);
            keywordsList.clear();
            chipContainer.removeAllViews();

            for (int i = 0; i < jsonArray.length(); i++) {
                String keyword = jsonArray.getString(i);
                keywordsList.add(keyword);
                addChip(keyword);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // Không gọi super.onBackPressed() ở đây để ngăn activity đóng ngay lập tức
        super.onBackPressed();
        showExitConfirmationDialog();
    }
    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            String fileName = getFileName(uri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                File tempFile = new File(getCacheDir(), fileName);
                try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                }
                file = tempFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}