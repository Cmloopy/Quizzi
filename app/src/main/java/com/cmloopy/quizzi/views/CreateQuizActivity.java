package com.cmloopy.quizzi.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.models.quiz.QuizCollectionResponse;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.utils.QuestionCreate.storage.QCLocalStorageUtils;
import com.cmloopy.quizzi.views.QuestionCreate.QuestionCreateActivity;
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
import java.util.Map;

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
    private Button btnAddQuestion, btnSaveQuiz;
    private ImageButton btnClose, btnMore;
    private FlexboxLayout chipContainer;
    private boolean quizCreatedSuccessfully = false;
    private Long currentQuizId = null;
    private static final String TAG = "CreateQuizActivity";
    private ProgressDialog progressDialog;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int QUESTION_CREATE_REQUEST_CODE = 2000;
    private Uri selectedImageUri = null;
    private List<String> keywordsList = new ArrayList<>();
    private Map<String, Object> user;
    int idUser = -1;
    private String visibility = "true";
    private String questionVisibility = "true";
    private QuizAPI quizApi; // Changed from QuizzApi to QuizAPI
    private List<QuizCollectionResponse> quizCollectionResponses;
    private ArrayList<String> collections;
    private int collectionSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_quiz);
        user = QCLocalStorageUtils.getLoggedInUser(this);
        clearQuizCreationState();
        Log.d(TAG, "CURRENT user: " + user.get("user_id"));

        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        currentQuizId = prefs.getLong("current_quiz_id", -1);
        if (currentQuizId != -1) {
            quizCreatedSuccessfully = true;
            Log.d(TAG, "Resuming quiz with ID: " + currentQuizId);
        }

        idUser = getIntent().getIntExtra("userId", -1);
        Log.d(TAG, "CURRENT id user: " + idUser);
        quizApi = RetrofitClient.getQuizCreateApi(); // Changed to use getQuizCreateApi()
        quizCollectionResponses = new ArrayList<>();
        collections = new ArrayList<>();
//        int idUser;
//        Object idUserObj = user.getOrDefault("user_id", -1);
//        try {
//            if (idUserObj instanceof Number) {
//                idUser = ((Number) idUserObj).intValue();
//            } else if (idUserObj instanceof String) {
//                idUser = Integer.parseInt((String) idUserObj);
//            } else {
//                idUser = -1;
//            }
//        } catch (Exception e) {
//            idUser = -1;
//        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_close), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners(idUser);
        setupSpinners();
        loadKeywordsFromPrefs();

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating quiz...");
        progressDialog.setCancelable(false);
    }

    private void initializeViews() {
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
        btnSaveQuiz = findViewById(R.id.btn_save_quiz);
        btnClose = findViewById(R.id.btn_close);
        btnMore = findViewById(R.id.btn_more);
    }

    private void setupClickListeners(int idUser) {
        cardCoverImage.setOnClickListener(v -> {
            /*if (checkPermission()) {
                openImagePicker();
            } else {
                requestPermission();
            }*/
            openImagePicker();
        });

        btnClose.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        btnMore.setOnClickListener(v -> {
            showMoreOptionsMenu();
        });

        // Add Question button - create quiz if needed and navigate to question creation
        btnAddQuestion.setOnClickListener(v -> {
            saveQuizAndAddQuestion(idUser);
        });

        // Save Quiz button - only save the quiz without navigating to question creation
        btnSaveQuiz.setOnClickListener(v -> {
            saveQuizOnly(idUser);
        });

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
        Call<List<QuizCollectionResponse>> collectionCall = quizApi.getAllQuizCollections();
        collectionCall.enqueue(new Callback<List<QuizCollectionResponse>>() {
            @Override
            public void onResponse(Call<List<QuizCollectionResponse>> call, Response<List<QuizCollectionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    quizCollectionResponses = new ArrayList<>(response.body());
                    collections = new ArrayList<>();
                    collections.add("Select Collection");

                    for (QuizCollectionResponse collectionResponse : quizCollectionResponses) {
                        collections.add(collectionResponse.getCategory());
                    }

                    ArrayAdapter<String> collectionAdapter = new ArrayAdapter<>(
                            CreateQuizActivity.this,
                            android.R.layout.simple_spinner_item,
                            collections);
                    collectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCollection.setAdapter(collectionAdapter);
                } else {
                    Log.e(TAG, "Failed to load collections: " + response.code());
                    Toast.makeText(CreateQuizActivity.this, "Failed to load collections", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollectionResponse>> call, Throwable t) {
                Log.e(TAG, "Collection API call failed", t);
                Toast.makeText(CreateQuizActivity.this, "Network error loading collections", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerCollection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCollection = parent.getItemAtPosition(position).toString();
                    collectionSelectedPosition = position;
                    Log.d(TAG, "Selected collection: " + selectedCollection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                collectionSelectedPosition = 0;
                // Do nothing
            }
        });

        // Theme Spinner
        String[] themes = {"Quizzo Default", "Colorful", "Minimal", "Dark", "Light"};
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                themes);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);
        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle theme selection
                String selectedTheme = themes[position];
                Log.d(TAG, "Selected theme: " + selectedTheme);
                // Apply theme logic here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to first theme
            }
        });

        String[] visibilities = {"Public", "Private"};
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                visibilities);
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVisibility.setAdapter(visibilityAdapter);
        spinnerVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                visibility = position == 0 ? "true" : "false";
                Log.d(TAG, "Quiz visibility set to: " + visibility);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                visibility = "true";
            }
        });

        String[] questionVisibilities = {"Public", "Private"};
        ArrayAdapter<String> questionVisibilityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                questionVisibilities);
        questionVisibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestionVisibility.setAdapter(questionVisibilityAdapter);
        spinnerQuestionVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionVisibility = position == 0 ? "true" : "false";
                Log.d(TAG, "Quiz questionVisibility set to: " + questionVisibility);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                questionVisibility = "true";
            }
        });
    }

    public void storeQuizSuccess(Long quizId, String quizTitle) {
        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("current_quiz_id", quizId);
        editor.putString("current_quiz_title", quizTitle);
        editor.apply();

        quizCreatedSuccessfully = true;
        currentQuizId = quizId;

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Toast.makeText(CreateQuizActivity.this, "Quiz saved successfully!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Quiz saved with ID: " + quizId);
    }

    public void clearQuizCreationState() {
        quizCreatedSuccessfully = false;
        currentQuizId = null;

        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("current_quiz_id");
        editor.remove("current_quiz_title");
        editor.apply();

        Log.d(TAG, "Quiz creation state cleared");
    }

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
                Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
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

            // Load the image
            ivSelectedCover.setImageURI(selectedImageUri);
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        } else if (requestCode == QUESTION_CREATE_REQUEST_CODE) {
            // Check if returning from QuestionCreateActivity
            Log.d(TAG, "Returned from QuestionCreateActivity");
            // No specific action needed here as we're keeping the quiz state
        }
    }

    // UI interaction methods
    private void showExitConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Leave without saving?")
                .setMessage("Your quiz progress will be lost if you leave without saving.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Leave", (dialog, which) -> {
                    saveQuizOnly(idUser);
                    clearQuizCreationState();
                    finish();
                })
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
                    // Clear quiz creation state
                    clearQuizCreationState();
                    Toast.makeText(this, "Quiz deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Please enter a title");
            isValid = false;
        }

        if (spinnerCollection.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a collection", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void navigateToQuestionCreate(Long quizId) {
        Intent intent = new Intent(CreateQuizActivity.this, QuestionCreateActivity.class);
        intent.putExtra("quizId", quizId);
        startActivity(intent);
    }

    private void saveQuizOnly(int idUser) {
        if (!validateForm()) {
            return;
        }

        if (quizCreatedSuccessfully && currentQuizId != null && currentQuizId > 0) {
            progressDialog.show();
            updateExistingQuiz(idUser);
            return;
        }

        saveKeywordsToPrefs();
        progressDialog.show();
        createNewQuiz(idUser, false);
    }

    private void saveQuizAndAddQuestion(int idUser) {
        saveKeywordsToPrefs();

        if (!validateForm()) {
            return;
        }

        if (quizCreatedSuccessfully && currentQuizId != null && currentQuizId > 0) {
            Log.d(TAG, "Using existing quiz: " + currentQuizId);
            updateExistingQuiz(idUser);
            navigateToQuestionCreate(currentQuizId);
            return;
        }

        progressDialog.show();
        createNewQuiz(idUser, true);
    }

    // Create a new quiz
    private void createNewQuiz(int idUser, boolean navigateToQuestions) {
        String titles = etTitle.getText().toString().trim();
        String des = etDescription.getText().toString().trim();
        String key = etKeyword.getText().toString().trim();
        String visiblee = visibility;
        String visibleQues = questionVisibility;
        String shuffer = "false";

        int selectedPosition = spinnerCollection.getSelectedItemPosition();
        Long collectionId;

        if (selectedPosition <= 0) {
            Toast.makeText(CreateQuizActivity.this, "Please select a collection", Toast.LENGTH_SHORT).show();
            return;
        } else {
            collectionId = getCollectionIdFromPosition(selectedPosition);
        }

        File file = null;
        if (selectedImageUri != null) {
            file = getFileFromUri(selectedImageUri);
        }
        if (file == null && selectedImageUri != null) {
            Log.e(TAG, "Failed to convert URI to File");
        }

        // Create request parts
        RequestBody requestFile = file != null ? RequestBody.create(MediaType.parse("image/*"), file) : null;
        MultipartBody.Part filePart = file != null ? MultipartBody.Part.createFormData("coverPhotoFile", file.getName(), requestFile) : null;
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), idUser + "");
        RequestBody quizCollectionId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(collectionId));
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titles);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), des);
        RequestBody keyword = RequestBody.create(MediaType.parse("text/plain"), key);
        RequestBody visible = RequestBody.create(MediaType.parse("text/plain"), visiblee);
        RequestBody visibleQuizQuestion = RequestBody.create(MediaType.parse("text/plain"), visibleQues);
        RequestBody shuffle = RequestBody.create(MediaType.parse("text/plain"), shuffer);

        // Show loading dialog
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(CreateQuizActivity.this);
            progressDialog.setMessage("Creating quiz...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

        Call<QuizResponse> call = quizApi.uploadQuiz(
                userId, quizCollectionId, title, description, keyword,
                visible, visibleQuizQuestion, shuffle, filePart
        );

        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    storeQuizSuccess(response.body().getId(), response.body().getTitle());

                    if (navigateToQuestions) {
                        navigateToQuestionCreate(response.body().getId());
                    }
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(CreateQuizActivity.this, "Failed to create quiz: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to create quiz: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateQuizActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    private Long getCollectionIdFromPosition(int position) {
        Long defaultId = 1L;

        if (collections != null && position > 0 && position < collections.size()) {
            String selectedCategory = collections.get(position);

            for (QuizCollectionResponse collection : quizCollectionResponses) {
                if (collection.getCategory().equals(selectedCategory)) {
                    return collection.getId();
                }
            }
        }

        return defaultId;
    }

    // Update an existing quiz
    private void updateExistingQuiz(int idUser) {
        String titles = etTitle.getText().toString().trim();
        String des = etDescription.getText().toString().trim();
        String key = etKeyword.getText().toString().trim();
        String visiblee = visibility;
        String visibleQues = questionVisibility;
        String shuffer = "false";

        // Get collection ID from selected position
        int selectedPosition = spinnerCollection.getSelectedItemPosition();
        Long collectionId;

        if (selectedPosition <= 0) {
            collectionId = 1L; // Default collection ID
        } else {
            collectionId = getCollectionIdFromPosition(selectedPosition);
        }

        // Handle the image file if selected
        File file = null;
        if (selectedImageUri != null) {
            file = getFileFromUri(selectedImageUri);
        }
        if (file == null && selectedImageUri != null) {
            Log.e(TAG, "Failed to convert URI to File");
        }

        // Create request parts
        RequestBody requestFile = file != null ? RequestBody.create(MediaType.parse("image/*"), file) : null;
        MultipartBody.Part filePart = file != null ? MultipartBody.Part.createFormData("coverPhotoFile", file.getName(), requestFile) : null;
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), idUser + "");
        RequestBody quizCollectionId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(collectionId));
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titles);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), des);
        RequestBody keyword = RequestBody.create(MediaType.parse("text/plain"), key);
        RequestBody visible = RequestBody.create(MediaType.parse("text/plain"), visiblee);
        RequestBody visibleQuizQuestion = RequestBody.create(MediaType.parse("text/plain"), visibleQues);
        RequestBody shuffle = RequestBody.create(MediaType.parse("text/plain"), shuffer);

        // Log the request details for debugging
        Log.d(TAG, "Updating quiz with ID: " + currentQuizId);
        Log.d(TAG, "User ID: " + idUser);
        Log.d(TAG, "Collection ID: " + collectionId);
        Log.d(TAG, "Title: " + titles);
        Log.d(TAG, "Visibility: " + visiblee);

        Call<QuizResponse> call = quizApi.updateQuiz(
                currentQuizId,
                userId,
                quizCollectionId,
                title,
                description,
                keyword,
                visible,
                visibleQuizQuestion,
                shuffle,
                filePart
        );

        Log.d(TAG, "Update request URL: " + call.request().url().toString());

        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    // Update stored quiz details
                    storeQuizSuccess(response.body().getId(), response.body().getTitle());
                    Toast.makeText(CreateQuizActivity.this, "Quiz updated successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Quiz updated with ID: " + currentQuizId);
                } else {
                    Toast.makeText(CreateQuizActivity.this, "Failed to update quiz: " + currentQuizId + " - " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update quiz: " + response.code());

                    // Additional debug info
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateQuizActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void addChip(String keyword) {
        Chip chip = new Chip(this);
        chip.setText(keyword);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(true);

        chip.setChipBackgroundColorResource(R.color.chip_background);
        chip.setTextColor(getResources().getColor(R.color.chip_text));

        chip.setChipStrokeWidth(1);
        chip.setChipStrokeColorResource(R.color.chip_stroke);

        chip.setOnCloseIconClickListener(v -> {
            chipContainer.removeView(chip);
            keywordsList.remove(keyword);
        });

        chipContainer.addView(chip);
    }

    private void saveKeywordsToPrefs() {
        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray jsonArray = new JSONArray(keywordsList);
        editor.putString("saved_keywords", jsonArray.toString());
        editor.apply();
    }

    private void loadKeywordsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        String savedKeywords = prefs.getString("saved_keywords", "");

        if (!savedKeywords.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(savedKeywords);
                keywordsList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    String keyword = jsonArray.getString(i);
                    keywordsList.add(keyword);
                    addChip(keyword);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading keywords from preferences", e);
            }
        }
    }

    @SuppressLint("Range")
    private File getFileFromUri(Uri uri) {
        String fileName = null;
        try {
            if (uri.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

            if (fileName == null) {
                fileName = uri.getPath();
                int cut = fileName.lastIndexOf('/');
                if (cut != -1) {
                    fileName = fileName.substring(cut + 1);
                }
            }

            File destinationFile = new File(getCacheDir(), fileName);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return destinationFile;
        } catch (Exception e) {
            Log.e(TAG, "Error getting file from URI", e);
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (quizCreatedSuccessfully) {
            showExitConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}