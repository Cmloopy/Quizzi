package com.cmloopy.quizzi.views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.TextView;
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
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    // UI Components
    private MaterialCardView cardCoverImage;
    private LinearLayout layoutCoverPlaceholder;
    private ImageView ivCoverIcon, ivSelectedCover;
    private EditText etTitle, etDescription, etKeyword;
    private Spinner spinnerCollection, spinnerTheme, spinnerVisibility, spinnerQuestionVisibility;
    private Button btnSave, btnAddQuestion;
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

        // Setup Edge-to-Edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_close), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initializeViews();

        // Setup click listeners
        setupClickListeners();

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

        btnSave = findViewById(R.id.btn_save);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnClose = findViewById(R.id.btn_close);
        btnMore = findViewById(R.id.btn_more);
    }

    private void setupClickListeners() {
        // Card cover image click
        cardCoverImage.setOnClickListener(v -> {
            if (checkPermission()) {
                openImagePicker();
            } else {
                requestPermission();
            }
        });

        // Close button
        btnClose.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        // More options button
        btnMore.setOnClickListener(v -> {
            showMoreOptionsMenu();
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            saveQuiz();
        });

        // Add question button
        btnAddQuestion.setOnClickListener(v -> {
            if (validateForm()) {
                saveQuizAndAddQuestion();
            }
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
                    Toast.makeText(CreateQuizActivity.this,
                            "Selected: " + collections[position], Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CreateQuizActivity.this,
                        "Theme: " + themes[position], Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CreateQuizActivity.this,
                        "Visibility: " + visibilities[position], Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CreateQuizActivity.this,
                        "Question Visibility: " + questionVisibilities[position], Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
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

    private void saveQuizAndAddQuestion() {
        // Save quiz and redirect to add question screen
        Toast.makeText(this, "Quiz saved, redirecting to add question", Toast.LENGTH_SHORT).show();

        // Save keywords
        saveKeywordsToPrefs();

        // In a real app, you'd save the quiz first, then open the question editor
        // Intent intent = new Intent(this, AddQuestionActivity.class);
        // intent.putExtra("QUIZ_ID", quizId);
        // startActivity(intent);
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
}