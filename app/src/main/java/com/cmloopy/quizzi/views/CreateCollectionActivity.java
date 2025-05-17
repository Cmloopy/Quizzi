package com.cmloopy.quizzi.views;

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
import android.view.View;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.models.quiz.QuizCollectionResponse;
import com.cmloopy.quizzi.utils.QuestionCreate.storage.QCLocalStorageUtils;
import com.cmloopy.quizzi.views.QuestionCreate.QuestionCreateActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

public class CreateCollectionActivity extends AppCompatActivity {

    private MaterialCardView cardCoverImage;
    private LinearLayout layoutCoverPlaceholder;
    private ImageView ivCoverIcon, ivSelectedCover;
    private EditText etTitle;
    private Spinner spinnerVisibility;
    private Button btnSaveQuizCollection;
    private ImageButton btnClose, btnMore;
    private FlexboxLayout chipContainer;
    private boolean quizCollectionCreatedSuccessfully = false;
    private Long currentQuizCollectionId = null;
    private static final String TAG = "CreateQuizCollectionActivity";
    private ProgressDialog progressDialog;
    private String visibility = "true";

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int QUESTION_CREATE_REQUEST_CODE = 2000;
    private Uri selectedImageUri = null;
    private List<String> keywordsList = new ArrayList<>();
    private Map<String, Object> user;
    int idUser = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_collection);
        user = QCLocalStorageUtils.getLoggedInUser(this);
        clearQuizCollectionCreationState();
        Log.d(TAG, "CURRENT user: " + user.get("user_id"));

        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        currentQuizCollectionId = prefs.getLong("current_quiz_collection_id", -1);
        if (currentQuizCollectionId != -1) {
            quizCollectionCreatedSuccessfully = true;
            Log.d(TAG, "Resuming quizCollection with ID: " + currentQuizCollectionId);
        }

        idUser = getIntent().getIntExtra("userId", -1);
        Log.d(TAG, "CURRENT id user: " + idUser);

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating quizCollection...");
        progressDialog.setCancelable(false);
    }

    private void initializeViews() {
        cardCoverImage = findViewById(R.id.card_cover_image);
        layoutCoverPlaceholder = findViewById(R.id.layout_cover_placeholder);
        ivCoverIcon = findViewById(R.id.iv_cover_icon);
        ivSelectedCover = findViewById(R.id.iv_selected_cover);

        etTitle = findViewById(R.id.et_title);
        chipContainer = findViewById(R.id.chipContainer);

        spinnerVisibility = findViewById(R.id.spinner_visibility);

        btnSaveQuizCollection = findViewById(R.id.btn_save_quiz);
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
            clearQuizCollectionCreationState();
            finish();
//            showExitConfirmationDialog();
        });

        btnMore.setOnClickListener(v -> {
            showMoreOptionsMenu();
        });


        btnSaveQuizCollection.setOnClickListener(v -> {
            saveQuizCollectionOnly(idUser);
            clearQuizCollectionCreationState();
            finish();
        });

    }

    private void setupSpinners() {
        // Visibility Spinner
        String[] visibilities = {"Public", "Private"};
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, visibilities);
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVisibility.setAdapter(visibilityAdapter);
        spinnerVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set visibility based on selection (Public = true, Private = false)
                visibility = position == 0 ? "true" : "false";
                Log.d(TAG, "Collection visibility set to: " + visibility);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                visibility = "true";
            }
        });
    }

    public void storeQuizCollectionSuccess(Long quizCollectionId, String quizCollectionTitle) {
        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("current_quiz_collection_id", quizCollectionId);
        editor.putString("current_quiz_collection_title", quizCollectionTitle);
        editor.apply();

        quizCollectionCreatedSuccessfully = true;
        currentQuizCollectionId = quizCollectionId;

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Toast.makeText(CreateCollectionActivity.this, "QuizCollection saved successfully!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "QuizCollection saved with ID: " + quizCollectionId);
    }

    public void clearQuizCollectionCreationState() {
        quizCollectionCreatedSuccessfully = false;
        currentQuizCollectionId = null;

        SharedPreferences prefs = getSharedPreferences("QuizzoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("current_quiz_collection_id");
        editor.remove("current_quiz_collection_title");
        editor.apply();

        Log.d(TAG, "QuizCollection creation state cleared");
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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            layoutCoverPlaceholder.setVisibility(View.GONE);
            ivSelectedCover.setVisibility(View.VISIBLE);

            ivSelectedCover.setImageURI(selectedImageUri);
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        } else if (requestCode == QUESTION_CREATE_REQUEST_CODE) {
            Log.d(TAG, "Returned from QuestionCreateActivity");
        }
    }

    private void showExitConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Leave without saving?")
                .setMessage("Your quizCollection progress will be lost if you leave without saving.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Leave", (dialog, which) -> {
                    saveQuizCollectionOnly(idUser);
                    clearQuizCollectionCreationState();
                    finish();
                })
                .show();
    }

    private void showMoreOptionsMenu() {
        String[] options = {"Duplicate", "Save", "Delete", "Help"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(this, "Duplicate selected", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            showDeleteConfirmationDialog();
                            break;
                        case 2:
                            saveQuizCollectionOnly(idUser);
                        case 3:
                            Toast.makeText(this, "Help selected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete QuizCollection?")
                .setMessage("This action cannot be undone.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete quizCollection logic would go here
                    // Clear quizCollection creation state
                    clearQuizCollectionCreationState();
                    Toast.makeText(this, "QuizCollection deleted", Toast.LENGTH_SHORT).show();
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


        return isValid;
    }

    private void navigateToQuestionCreate(Long quizCollectionId) {
        Intent intent = new Intent(CreateCollectionActivity.this, QuestionCreateActivity.class);
        intent.putExtra("quizCollectionId", quizCollectionId);
        startActivity(intent);
    }

    private void saveQuizCollectionOnly(int idUser) {
        if (!validateForm()) {
            return;
        }

        if (quizCollectionCreatedSuccessfully && currentQuizCollectionId != null && currentQuizCollectionId > 0) {
            progressDialog.show();
            updateExistingQuizCollection(idUser);
            return;
        }

        progressDialog.show();
        createNewQuizCollection(idUser, false);
    }

    private void createNewQuizCollection(int idUser, boolean navigateToQuestions) {
        QuizAPI quizCollectionApi = RetrofitClient.getQuizApi();

        String titles = etTitle.getText().toString().trim();
        String visiblee = visibility; // Use the value from spinner

        File file = null;
        if (selectedImageUri != null) {
            file = getFileFromUri(selectedImageUri);
        }
        if (file == null && selectedImageUri != null) {
            Log.e(TAG, "Failed to convert URI to File");
        }

        RequestBody requestFile = file != null ? RequestBody.create(MediaType.parse("image/*"), file) : null;
        MultipartBody.Part filePart = file != null ? MultipartBody.Part.createFormData("coverPhotoFile", file.getName(), requestFile) : null;
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), idUser + "");
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titles);
        RequestBody visible = RequestBody.create(MediaType.parse("text/plain"), visiblee);


        Call<QuizCollectionResponse> call = quizCollectionApi.uploadQuizCollection(
                userId,
                title,
                visible,
                filePart
        );

        call.enqueue(new Callback<QuizCollectionResponse>() {
            @Override
            public void onResponse(Call<QuizCollectionResponse> call, Response<QuizCollectionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    storeQuizCollectionSuccess(response.body().getId(), response.body().getCategory());

                    if (navigateToQuestions) {
                        navigateToQuestionCreate(response.body().getId());
                    }
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(CreateCollectionActivity.this, "Failed to create quizCollection: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to create quizCollection: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizCollectionResponse> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateCollectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateExistingQuizCollection(int idUser) {
        QuizAPI quizCollectionApi = RetrofitClient.getQuizApi();

        String titles = etTitle.getText().toString().trim();
        String visiblee = visibility; // Use the value from spinner

        File file = null;
        if (selectedImageUri != null) {
            file = getFileFromUri(selectedImageUri);
        }
        if (file == null && selectedImageUri != null) {
            Log.e(TAG, "Failed to convert URI to File");
        }

        RequestBody requestFile = file != null ? RequestBody.create(MediaType.parse("image/*"), file) : null;
        MultipartBody.Part filePart = file != null ? MultipartBody.Part.createFormData("coverPhotoFile", file.getName(), requestFile) : null;
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), idUser + "");
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titles);
        RequestBody visible = RequestBody.create(MediaType.parse("text/plain"), visiblee);

        Call<QuizCollectionResponse> call = quizCollectionApi.updateQuizCollection(
                currentQuizCollectionId,
                userId,
                title,
                visible,
                filePart
        );

        call.enqueue(new Callback<QuizCollectionResponse>() {
            @Override
            public void onResponse(Call<QuizCollectionResponse> call, Response<QuizCollectionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update stored quizCollection details
                    storeQuizCollectionSuccess(response.body().getId(), response.body().getCategory());
                    Toast.makeText(CreateCollectionActivity.this, "QuizCollection updated successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "QuizCollection updated with ID: " + currentQuizCollectionId);
                } else {
                    // Dismiss progress dialog on error
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(CreateCollectionActivity.this, "Failed to update quizCollection: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update quizCollection: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizCollectionResponse> call, Throwable t) {
                // Dismiss progress dialog on error
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateCollectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}