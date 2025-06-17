package com.cmloopy.quizzi.views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuizCollectionAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.utils.QuestionCreate.storage.QCLocalStorageUtils;
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

public class UpdateCollectionActivity extends AppCompatActivity {

    private MaterialCardView cardCoverImage;
    private LinearLayout layoutCoverPlaceholder;
    private ImageView ivCoverIcon, ivSelectedCover;
    private EditText etTitle;
    private Spinner spinnerVisibility;
    private Button btnUpdateQuizCollection;
    private TextView headerTitle;
    private ImageButton btnClose, btnMore;
    private FlexboxLayout chipContainer;

    private static final String TAG = "UpdateCollectionActivity";
    private ProgressDialog progressDialog;
    private String visibility = "true";
    private boolean hasImageChanged = false;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CODE = 1000;
    private Uri selectedImageUri = null;
    private List<String> keywordsList = new ArrayList<>();
    private Map<String, Object> user;

    // Intent extras
    private int collectionId = -1;
    private int userId = -1;
    private static final int MAX_TITLE_LENGTH = 50;
    private TextView tvTitleCounter;
    private QuizCollection originalCollection;
    QuizCollectionAPI quizCollectionAPI = RetrofitClient.getCollectionService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_collection);

        user = QCLocalStorageUtils.getLoggedInUser(this);

        collectionId = getIntent().getIntExtra("collectionId", -1);
        userId = getIntent().getIntExtra("userId", -1);

        Log.d(TAG, "Update Collection - ID: " + collectionId + ", User ID: " + userId);

        if (collectionId == -1 || userId == -1) {
            Toast.makeText(this, "Invalid collection data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_close), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
        setupSpinners();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading collection...");
        progressDialog.setCancelable(false);

        // Load existing collection data
        loadCollectionData();
    }

    private void initializeViews() {
        cardCoverImage = findViewById(R.id.card_cover_image);
        layoutCoverPlaceholder = findViewById(R.id.layout_cover_placeholder);
        ivCoverIcon = findViewById(R.id.iv_cover_icon);
        ivSelectedCover = findViewById(R.id.iv_selected_cover);

        etTitle = findViewById(R.id.et_title);
        tvTitleCounter = findViewById(R.id.tv_title_counter);
        chipContainer = findViewById(R.id.chipContainer);

        spinnerVisibility = findViewById(R.id.spinner_visibility);
        headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Update Quiz Collection");
        btnUpdateQuizCollection = findViewById(R.id.btn_save_quiz);
        btnUpdateQuizCollection.setText("Update Collection"); // Change button text
        btnClose = findViewById(R.id.btn_close);
        btnMore = findViewById(R.id.btn_more);
        btnMore.setVisibility(View.GONE);

        setupTitleTextWatcher(etTitle, tvTitleCounter, "Title", MAX_TITLE_LENGTH);
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
                    Toast.makeText(UpdateCollectionActivity.this,
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

        btnClose.setOnClickListener(v -> {
            showExitConfirmationDialog();
        });

        btnMore.setOnClickListener(v -> {
            showMoreOptionsMenu();
        });

        btnUpdateQuizCollection.setOnClickListener(v -> {
            updateQuizCollection();
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

    private void loadCollectionData() {
        progressDialog.show();

        Call<QuizCollection> call = quizCollectionAPI.getCollectionById(collectionId);

        call.enqueue(new Callback<QuizCollection>() {
            @Override
            public void onResponse(Call<QuizCollection> call, Response<QuizCollection> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    originalCollection = response.body();
                    populateFields(originalCollection);
                } else {
                    Toast.makeText(UpdateCollectionActivity.this, "Failed to load collection data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load collection: " + response.code());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuizCollection> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(UpdateCollectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage());
                finish();
            }
        });
    }

    private void populateFields(QuizCollection collection) {
        // Set title
        etTitle.setText(collection.getCategory());

        // Set visibility spinner
        boolean isPublic = collection.isVisibleTo(); // Assuming isVisible() method exists
        spinnerVisibility.setSelection(isPublic ? 0 : 1);
        visibility = isPublic ? "true" : "false";

        // Load cover image if exists
        String coverImageUrl = collection.getCoverPhoto(); // Assuming getCoverPhotoUrl() method exists
        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            layoutCoverPlaceholder.setVisibility(View.GONE);
            ivSelectedCover.setVisibility(View.VISIBLE);

            // Use Glide or similar library to load image from URL
            Glide.with(this)
                    .load(coverImageUrl)
                    .placeholder(R.drawable.ic_image_placeholder) // Add placeholder drawable
                    .error(R.drawable.ic_launcher_background) // Add error drawable
                    .into(ivSelectedCover);
        }

        Log.d(TAG, "Collection data populated: " + collection.getCategory());
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
            hasImageChanged = true;

            layoutCoverPlaceholder.setVisibility(View.GONE);
            ivSelectedCover.setVisibility(View.VISIBLE);

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
        if (originalCollection == null) return false;

        String currentTitle = etTitle.getText().toString().trim();
        boolean currentVisibility = visibility.equals("true");

        return !currentTitle.equals(originalCollection.getCategory()) ||
                currentVisibility != originalCollection.isVisibleTo() ||
                hasImageChanged;
    }

    private void showMoreOptionsMenu() {
        String[] options = {"Delete Collection", "Duplicate", "Help"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showDeleteConfirmationDialog();
                            break;
                        case 1:
                            Toast.makeText(this, "Duplicate feature coming soon", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(this, "Help selected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Collection?")
                .setMessage("This action cannot be undone. All questions in this collection will also be deleted.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteCollection();
                })
                .show();
    }

    private void deleteCollection() {
        progressDialog.setMessage("Deleting collection...");
        progressDialog.show();

        QuizAPI quizCollectionApi = RetrofitClient.getQuizApi();
        Long tmpCollectionId = (long) collectionId;
        Call<Void> call = quizCollectionAPI.deleteQuizCollection(tmpCollectionId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful()) {
                    Toast.makeText(UpdateCollectionActivity.this, "Collection deleted successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Signal that collection was deleted
                    finish();
                } else {
                    Toast.makeText(UpdateCollectionActivity.this, "Failed to delete collection: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to delete collection: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(UpdateCollectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Please enter a title");
            isValid = false;
        }

        return isValid;
    }

    private void updateQuizCollection() {
        if (!validateForm()) {
            return;
        }

        progressDialog.setMessage("Updating collection...");
        progressDialog.show();

        QuizAPI quizCollectionApi = RetrofitClient.getQuizApi();

        String titles = etTitle.getText().toString().trim();
        String visiblee = visibility;

        File file = null;
        MultipartBody.Part filePart = null;

        // Only process image if it was changed
        if (hasImageChanged && selectedImageUri != null) {
            file = getFileFromUri(selectedImageUri);
            if (file == null) {
                Log.e(TAG, "Failed to convert URI to File");
            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                filePart = MultipartBody.Part.createFormData("coverPhotoFile", file.getName(), requestFile);
            }
        }

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), this.userId + "");
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), titles);
        RequestBody visible = RequestBody.create(MediaType.parse("text/plain"), visiblee);
        Long tmpCollectionId = (long) collectionId;
        Call<QuizCollection> call = quizCollectionAPI.updateQuizCollection(
                tmpCollectionId,
                userId,
                title,
                visible,
                filePart
        );

        call.enqueue(new Callback<QuizCollection>() {
            @Override
            public void onResponse(Call<QuizCollection> call, Response<QuizCollection> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdateCollectionActivity.this, "Collection updated successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Collection updated with ID: " + collectionId);

                    // Set result to indicate successful update
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedCollection", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(UpdateCollectionActivity.this, "Failed to update collection: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update collection: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizCollection> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(UpdateCollectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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