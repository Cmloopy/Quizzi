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
import android.text.Editable;
import android.text.TextWatcher;
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

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.QuizzApi;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateQuizActivity extends AppCompatActivity {

    // UI Components
    private MaterialCardView cardCoverImage;
    private LinearLayout layoutCoverPlaceholder;
    private ImageView ivCoverIcon, ivSelectedCover;
    private EditText etTitle, etDescription, etKeyword;
    private Spinner spinnerCollection, spinnerTheme, spinnerVisibility, spinnerQuestionVisibility;
    private Button btnAddQuestion, btnSaveQuiz;
    private ImageButton btnClose, btnMore;
    private FlexboxLayout chipContainer;
    private Long quizId = -1L;
    private QuizResponse originalQuiz;
    private static final String TAG = "UpdateQuizActivity";
    private ProgressDialog progressDialog;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CODE = 1000;
    private Uri selectedImageUri = null;
    private List<String> keywordsList = new ArrayList<>();
    private Map<String, Object> user;
    int idUser = -1;
    private String visibility = "true";
    private String questionVisibility = "true";
    private QuizAPI quizApi;
    private QuizzApi quizzApi;
    private List<QuizCollectionResponse> quizCollectionResponses;
    private ArrayList<String> collections;
    private int collectionSelectedPosition = 0;
    private int originalCollectionPosition = 0;
    private boolean hasImageChanged = false;
    private boolean collectionsLoaded = false;
    private boolean quizDataLoaded = false;

    private static final int MAX_TITLE_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 150;
    private static final int MAX_KEYWORD_LENGTH = 30;
    private TextView tvTitleCounter;
    private TextView tvDescriptionCounter;
    private TextView tvKeywordCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_quiz);

        user = QCLocalStorageUtils.getLoggedInUser(this);

        // Get quiz ID from intent
        quizId = getIntent().getLongExtra("quizId", -1);
        idUser = getIntent().getIntExtra("userId", -1);

        if (quizId == -1) {
            Toast.makeText(this, "Invalid quiz ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Updating quiz with ID: " + quizId);
        Log.d(TAG, "User ID: " + idUser);

        quizApi = RetrofitClient.getQuizCreateApi();
        quizzApi = RetrofitClient.getQuizzApi();
        quizCollectionResponses = new ArrayList<>();
        collections = new ArrayList<>();

        // Initialize Progress Dialog FIRST - before calling any methods that use it
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_close), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
        setupSpinners();
        loadQuizDetails();
        setupCollectionSpinner();// Now safe to call since progressDialog is initialized
    }

    private void initializeViews() {
        cardCoverImage = findViewById(R.id.card_cover_image);
        layoutCoverPlaceholder = findViewById(R.id.layout_cover_placeholder);
        ivCoverIcon = findViewById(R.id.iv_cover_icon);
        ivSelectedCover = findViewById(R.id.iv_selected_cover);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_desc_title);
        etKeyword = findViewById(R.id.et_keyword);

        tvTitleCounter = findViewById(R.id.tv_title_counter);
        tvDescriptionCounter = findViewById(R.id.tv_description_counter);
        tvKeywordCounter = findViewById(R.id.tv_keyword_counter);

        chipContainer = findViewById(R.id.chipContainer);

        spinnerCollection = findViewById(R.id.spinner_collection);
        spinnerTheme = findViewById(R.id.spinner_theme);
        spinnerVisibility = findViewById(R.id.spinner_visibility);
        spinnerQuestionVisibility = findViewById(R.id.spinner_question_visibility);

        btnSaveQuiz = findViewById(R.id.btn_save_quiz);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnClose = findViewById(R.id.btn_close);
        btnMore = findViewById(R.id.btn_more);

        setupTitleTextWatcher(etTitle, tvTitleCounter, "Title", MAX_TITLE_LENGTH);
        setupTitleTextWatcher(etDescription, tvDescriptionCounter, "Description", MAX_DESCRIPTION_LENGTH);
        setupTitleTextWatcher(etKeyword, tvKeywordCounter, "Keyword", MAX_KEYWORD_LENGTH);
    }

    private void setupTitleTextWatcher(TextView textView, TextView textCounter, String fieldType, int maxLength) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update counter in real-time
                updateTitleCounter(textCounter, s.length(), maxLength);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > maxLength) {
                    s.delete(maxLength, s.length());
                    Toast.makeText(UpdateQuizActivity.this,
                            fieldType + " cannot exceed " + maxLength + " characters",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateTitleCounter(textCounter, 0, maxLength);
    }

    private void updateTitleCounter(TextView textCounter, int currentLength, int maxLength) {
        String counterText = currentLength + "/" + maxLength;
        textCounter.setText(counterText);

        if (currentLength >= maxLength * 0.9) {
            textCounter.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (currentLength >= maxLength * 0.7) {
            textCounter.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            textCounter.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }


    private void setupClickListeners() {
        cardCoverImage.setOnClickListener(v -> openImagePicker());

        btnClose.setOnClickListener(v -> showExitConfirmationDialog());

        btnMore.setOnClickListener(v -> showMoreOptionsMenu());
        btnMore.setVisibility(View.GONE);

        btnSaveQuiz.setOnClickListener(v -> updateQuiz());
        btnAddQuestion.setOnClickListener(v -> saveQuizAndAddQuestion());
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

    private void setupCollectionSpinner() {
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
                            UpdateQuizActivity.this,
                            android.R.layout.simple_spinner_item,
                            collections);
                    collectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCollection.setAdapter(collectionAdapter);

                    // Mark collections as loaded and try to set selection
                    collectionsLoaded = true;
                    checkAndSetCollectionSelection();
                } else {
                    Log.e(TAG, "Failed to load collections: " + response.code());
                    Toast.makeText(UpdateQuizActivity.this, "Failed to load collections", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollectionResponse>> call, Throwable t) {
                Log.e(TAG, "Collection API call failed", t);
                Toast.makeText(UpdateQuizActivity.this, "Network error loading collections", Toast.LENGTH_SHORT).show();
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
            }
        });
        setCollectionSelection();
    }

    private void setupSpinners() {
        String[] themes = {"Quizzo Default", "Colorful", "Minimal", "Dark", "Light"};
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                themes);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);

        // Visibility Spinner
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

        // Question Visibility Spinner
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
                Log.d(TAG, "Question visibility set to: " + questionVisibility);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                questionVisibility = "true";
            }
        });
    }

    private void loadQuizDetails() {
        if (progressDialog != null) {
            progressDialog.setMessage("Loading quiz details...");
            progressDialog.show();
        }

        Call<QuizResponse> call = quizzApi.getQuizById(quizId);
        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    originalQuiz = response.body();
                    populateQuizData();
                } else {
                    Log.e(TAG, "Failed to load quiz: " + response.code());
                    Toast.makeText(UpdateQuizActivity.this, "Failed to load quiz details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.e(TAG, "Error loading quiz", t);
                Toast.makeText(UpdateQuizActivity.this, "Network error loading quiz", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateQuizData() {
        if (originalQuiz == null) return;

        // Populate text fields
        etTitle.setText(originalQuiz.getTitle());
        etDescription.setText(originalQuiz.getDescription());

        // Load cover image if exists
        if (originalQuiz.getCoverPhoto() != null && !originalQuiz.getCoverPhoto().isEmpty()) {
            layoutCoverPlaceholder.setVisibility(View.GONE);
            ivSelectedCover.setVisibility(View.VISIBLE);

            // Load image using Glide or your preferred image loading library
            Glide.with(this)
                    .load(originalQuiz.getCoverPhoto())
                    .into(ivSelectedCover);
        }

        // Set visibility spinners
        spinnerVisibility.setSelection(originalQuiz.isVisible() ? 0 : 1);
        visibility = originalQuiz.isVisible() ? "true" : "false";

        spinnerQuestionVisibility.setSelection(originalQuiz.isVisibleQuizQuestion() ? 0 : 1);
        questionVisibility = originalQuiz.isVisibleQuizQuestion() ? "true" : "false";

        // Handle keywords
        if (originalQuiz.getKeyword() != null && !originalQuiz.getKeyword().isEmpty()) {
            String[] keywords = originalQuiz.getKeyword().split(",");
            keywordsList.clear();
            chipContainer.removeAllViews();

            for (String keyword : keywords) {
                String trimmedKeyword = keyword.trim();
                if (!trimmedKeyword.isEmpty()) {
                    keywordsList.add(trimmedKeyword);
                    addChip(trimmedKeyword);
                }
            }
        }

        // Mark quiz data as loaded and try to set collection selection
        quizDataLoaded = true;
        checkAndSetCollectionSelection();
    }

    private void setCollectionSelection() {
        if (originalQuiz == null || collections == null || collections.isEmpty()) {
            Log.d(TAG, "Cannot set collection selection - missing data");
            return;
        }

        Log.d(TAG, "Setting collection selection for quiz collection ID: " + originalQuiz.getQuizCollectionId());
        String currentCollection = "";
        for (int i = 0; i < quizCollectionResponses.size(); i++) {
            Log.d(TAG, "Response id: " + quizCollectionResponses.get(i).getId() + " - Current Quiz Id: " + originalQuiz.getQuizCollectionId());

            if (quizCollectionResponses.get(i).getId() == originalQuiz.getQuizCollectionId()) {
                Log.d(TAG, "Found matching collection at position: " + (i + 1));
                currentCollection = quizCollectionResponses.get(i).getCategory();
            }
        }

        for(int i = 0; i < collections.size(); i++) {
            if(currentCollection.equals(collections.get(i))) {
                spinnerCollection.setSelection(i);
                collectionSelectedPosition = i;
                originalCollectionPosition = i;
                return;
            }
        }

        Log.w(TAG, "No matching collection found for ID: " + originalQuiz.getQuizCollectionId());
    }

    private void checkAndSetCollectionSelection() {
        if (collectionsLoaded && quizDataLoaded && originalQuiz != null) {
            setCollectionSelection();
        }
    }

    private void updateQuiz() {
        if (!validateForm()) {
            return;
        }

        progressDialog.setMessage("Updating quiz...");
        progressDialog.show();

        String titles = etTitle.getText().toString().trim();
        String des = etDescription.getText().toString().trim();
        String key = buildKeywordsString();
        String visiblee = visibility;
        String visibleQues = questionVisibility;
        String shuffer = "false";

        Long collectionId = getCollectionIdFromPosition(collectionSelectedPosition);

        File file = null;
        MultipartBody.Part filePart = null;

        if (hasImageChanged && selectedImageUri != null) {
            file = getFileFromUri(selectedImageUri);
            if (file != null) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                filePart = MultipartBody.Part.createFormData("coverPhotoFile", file.getName(), requestFile);
            }
        }

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), idUser + "");
        RequestBody quizCollectionId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(collectionId));
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titles);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), des);
        RequestBody keyword = RequestBody.create(MediaType.parse("text/plain"), key);
        RequestBody visible = RequestBody.create(MediaType.parse("text/plain"), visiblee);
        RequestBody visibleQuizQuestion = RequestBody.create(MediaType.parse("text/plain"), visibleQues);
        RequestBody shuffle = RequestBody.create(MediaType.parse("text/plain"), shuffer);

        Call<QuizResponse> call = quizApi.updateQuiz(
                quizId,
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

        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdateQuizActivity.this, "Quiz updated successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Quiz updated with ID: " + quizId);

                    // Set result to indicate successful update
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("quiz_updated", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(UpdateQuizActivity.this, "Failed to update quiz: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update quiz: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(UpdateQuizActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage(), t);
            }
        });
    }

    private String buildKeywordsString() {
        if (keywordsList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keywordsList.size(); i++) {
            sb.append(keywordsList.get(i));
            if (i < keywordsList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
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

    private void saveQuizAndAddQuestion() {
        if (!validateForm()) {
            return;
        }
        navigateToQuestionCreate(quizId);
        updateQuiz();
        progressDialog.show();
    }

    private void navigateToQuestionCreate(Long quizId) {
        Intent intent = new Intent(UpdateQuizActivity.this, QuestionCreateActivity.class);
        intent.putExtra("quizId", quizId);
        startActivity(intent);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            hasImageChanged = true;

            // Hide placeholder content and show selected image
            layoutCoverPlaceholder.setVisibility(View.GONE);
            ivSelectedCover.setVisibility(View.VISIBLE);

            // Load the image
            ivSelectedCover.setImageURI(selectedImageUri);
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExitConfirmationDialog() {
        if (hasDataChanged()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Discard changes?")
                    .setMessage("You have unsaved changes. Are you sure you want to leave?")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Discard", (dialog, which) -> {
                        setResult(RESULT_CANCELED);
                        finish();
                    })
                    .show();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private boolean hasDataChanged() {
        if (originalQuiz == null) return false;

        String currentTitle = etTitle.getText().toString().trim();
        String currentDescription = etDescription.getText().toString().trim();
        String currentKeywords = buildKeywordsString();
        boolean currentVisibility = visibility.equals("true");
        boolean currentQuestionVisibility = questionVisibility.equals("true");

        // Compare original keywords with current keywords
        String originalKeywords = originalQuiz.getKeyword() != null ? originalQuiz.getKeyword() : "";

        return !currentTitle.equals(originalQuiz.getTitle()) ||
                !currentDescription.equals(originalQuiz.getDescription() != null ? originalQuiz.getDescription() : "") ||
                !currentKeywords.equals(originalKeywords) ||
                currentVisibility != originalQuiz.isVisible() ||
                currentQuestionVisibility != originalQuiz.isVisibleQuizQuestion() ||
                collectionSelectedPosition != originalCollectionPosition ||
                hasImageChanged;
    }

    private void showMoreOptionsMenu() {
        String[] options = {"Help"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Help
                            Toast.makeText(this, "Help selected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
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
        super.onBackPressed();
        showExitConfirmationDialog();
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