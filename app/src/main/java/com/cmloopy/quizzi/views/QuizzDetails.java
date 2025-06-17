package com.cmloopy.quizzi.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizzDetailsQuestionAdapter;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCMenuItem;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCMenuAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.models.user.User;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizzDetails extends AppCompatActivity {
    private static final int UPDATE_QUIZ_REQUEST_CODE = 1001;

    private RecyclerView questionRecyclerView;
    private QuizzDetailsQuestionAdapter questionAdapter;
    private Button btn;
    private int idUser = -1;
    private long idQuizz = -1;
    private QuizResponse quizResponse;

    // UI elements that need to be updated after quiz modification
    private TextView txtTitleQuiz;
    private TextView quizDescriptionDetail;
    private TextView txtNameAuthor;
    private TextView txtHandler;
    private ImageView profileImage;
    private ImageView quizCoverImage;
    private ImageView btnClose;
    private ImageView btnEdit;
    private ImageView btnFavorite;
    private ProgressDialog progressDialog;
    private boolean quizUpdated = false;

    private LinearLayout hiddenQuestionsContainer;
    private TextView hiddenQuestionsMessage;
    private ImageView hiddenQuestionsIcon;

    private LinearLayout noQuestionsContainer;
    private TextView noQuestionsMessage;
    private ImageView noQuestionsIcon;

    UserApi userApi = RetrofitClient.getUserApi();
    QuizzApi quizzApi = RetrofitClient.getQuizzApi();
    QuestionApi questionApi = RetrofitClient.getQuestionApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizz_details_full_page);

        idQuizz = getIntent().getLongExtra("quizId", -1);
        idUser = getIntent().getIntExtra("userId", -1);
        Log.e("QuizApi", idQuizz + "");
        Log.e("IdUser", idUser + "");

        initializeViews();
        setupClickListeners();
        loadQuizData();
        setupPlayButton();
    }

    private void initializeViews() {
        txtTitleQuiz = findViewById(R.id.quizTitle);
        quizDescriptionDetail = findViewById(R.id.quizDescriptionDetail);
        txtNameAuthor = findViewById(R.id.userName);
        txtHandler = findViewById(R.id.userHandle);
        btn = findViewById(R.id.btn_play);
        questionRecyclerView = findViewById(R.id.quizDetailsQuestionRecyclerView);
        profileImage = findViewById(R.id.profileImage);
        quizCoverImage = findViewById(R.id.quizCoverImage);
        btnFavorite = findViewById(R.id.btnFavorite);

        // Initialize button references
        btnClose = findViewById(R.id.btnClose);
        btnEdit = findViewById(R.id.btnEdit);

        hiddenQuestionsContainer = findViewById(R.id.hiddenQuestionsContainer);
        hiddenQuestionsMessage = findViewById(R.id.hiddenQuestionsMessage);
        hiddenQuestionsIcon = findViewById(R.id.hiddenQuestionsIcon);

        noQuestionsContainer = findViewById(R.id.noQuestionsContainer);
        noQuestionsMessage = findViewById(R.id.noQuestionsMessage);
        noQuestionsIcon = findViewById(R.id.noQuestionsIcon);
    }

    private void setupClickListeners() {
        btnFavorite.setVisibility(View.GONE);
        // Setup close button
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("QuizzDetails", "Close button clicked");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("quiz_updated", quizUpdated);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            });
        } else {
            Log.e("QuizzDetails", "btnClose is null - check if ID exists in layout");
        }
        // Setup options menu button - now shows custom popup
        if (btnEdit != null) {
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("QuizzDetails", "Options button clicked");
                    showPopupMenuAlternative(v);
                }
            });
        } else {
            Log.e("QuizzDetails", "btnEdit is null - check if ID exists in layout");
        }
    }

    private void showPopupMenu(View anchorView) {
        if (quizResponse == null) {
            Log.e("QuizzDetails", "Quiz data not loaded yet");
            return;
        }

        if (idUser != quizResponse.getUserId()) {
            Log.d("QuizzDetails", "User is not quiz owner, menu not displayed");
            return;
        }

        View popupView = LayoutInflater.from(this).inflate(R.layout.ui_qc_custom_menu, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(10);

        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<QCMenuItem> menuItems = Arrays.asList(
                new QCMenuItem("Edit Quiz", R.drawable.ic_edit_dark, false),
                new QCMenuItem("Share Quiz", R.drawable.ic_share_dark, false),
                new QCMenuItem("Generate QR Code", R.drawable.ic_37_qr, false),
                new QCMenuItem("Delete Quiz", R.drawable.ic_78_delete, false)
        );

        QCMenuAdapter adapter = new QCMenuAdapter(menuItems, item -> {
            popupWindow.dismiss();
            handleMenuClick(item);
        });
        recyclerView.setAdapter(adapter);

        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private void showPopupMenuAlternative(View anchorView) {
        if (quizResponse == null) {
            Log.e("QuizzDetails", "Quiz data not loaded yet");
            return;
        }

        View popupView = LayoutInflater.from(this).inflate(R.layout.ui_qc_custom_menu, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(10);

        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<QCMenuItem> menuItems;

        if (idUser == quizResponse.getUserId()) {
            menuItems = Arrays.asList(
                    new QCMenuItem("Edit Quiz", R.drawable.ic_edit_dark, false),
                    new QCMenuItem("Share Quiz", R.drawable.ic_share_dark, false),
                    new QCMenuItem("Generate QR Code", R.drawable.ic_37_qr, false),
                    new QCMenuItem("Delete Quiz", R.drawable.ic_78_delete, false)
            );
        } else {
            menuItems = Arrays.asList(
                    new QCMenuItem("Share Quiz", R.drawable.ic_share_dark, false)
            );
        }

        QCMenuAdapter adapter = new QCMenuAdapter(menuItems, item -> {
            popupWindow.dismiss();
            handleMenuClick(item);
        });
        recyclerView.setAdapter(adapter);

        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private void handleMenuClick(QCMenuItem item) {
        switch (item.getTitle()) {
            case "Edit Quiz":
                openEditQuiz();
                break;
            case "Share Quiz":
                shareQuiz();
                break;
            case "Generate QR Code":
                generateQRCode();
                break;
            case "Delete Quiz":
                deleteQuiz();
                break;
            default:
                Log.d("QuizzDetails", "Unknown menu item: " + item.getTitle());
                break;
        }
    }

    private void deleteQuiz() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz")
                .setMessage("Are you sure you want to delete this quiz?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performQuizDeletion();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performQuizDeletion() {
        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Deleting quiz...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<Void> call = quizzApi.deleteQuiz(idQuizz);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(QuizzDetails.this, "Quiz deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("quiz_deleted", true);
                    resultIntent.putExtra("quizId", idQuizz);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(QuizzDetails.this, "Failed to delete quiz", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("DeleteQuiz", "Error: " + t.getMessage());
                Toast.makeText(QuizzDetails.this, "Network error during deletion", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadQuizData() {
        Call<QuizResponse> quizResponseCall = quizzApi.getQuizById(idQuizz);
        quizResponseCall.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    quizResponse = response.body();
                    updateQuizUI(quizResponse);
                    setUpInfoUser(quizResponse.getUserId());
                } else {
                    Log.e("quizz", "failed: " + response.code());
                    Toast.makeText(QuizzDetails.this, "Failed to load quiz details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                Log.e("Quizz", "Error: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(QuizzDetails.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

        setUpRecycleView();
    }

    private void updateQuizUI(QuizResponse quizResponse) {
        if (txtTitleQuiz != null) {
            txtTitleQuiz.setText(quizResponse.getTitle());
        }
        if (quizDescriptionDetail != null) {
            quizDescriptionDetail.setText(quizResponse.getDescription());
        }
        if(quizCoverImage != null) {
            quizCoverImage.setVisibility(View.VISIBLE);
//            Picasso.get()
//                    .load(quizResponse.getCoverPhoto())
//                    .resize(200, 200)
//                    .centerInside()
//                    .into(quizCoverImage);
            Glide.with(this)
                    .load(quizResponse.getCoverPhoto())
                    .placeholder(R.drawable.ic_image_placeholder_2)
                    .error(R.drawable.ic_image_placeholder_2)
                    .into(quizCoverImage);
        }
        handleQuestionVisibility(quizResponse.isVisibleQuizQuestion());
    }

    private void handleQuestionVisibility(boolean isVisible) {
        if (isVisible) {
            if (questionRecyclerView != null) {
                questionRecyclerView.setVisibility(View.VISIBLE);
            }
            hideAllMessageContainers();
            setUpRecycleView();
        } else {
            if (questionRecyclerView != null) {
                questionRecyclerView.setVisibility(View.GONE);
            }
            showHiddenQuestionsView();
        }
    }

    private void hideAllMessageContainers() {
        if (hiddenQuestionsContainer != null) {
            hiddenQuestionsContainer.setVisibility(View.GONE);
        }
        if (noQuestionsContainer != null) {
            noQuestionsContainer.setVisibility(View.GONE);
        }
    }
    private void showHiddenQuestionsView() {
        hideAllMessageContainers();
        if (hiddenQuestionsContainer != null) {
            hiddenQuestionsContainer.setVisibility(View.VISIBLE);
        }
        setupHiddenQuestionsView();
    }

    private void showNoQuestionsView() {
        hideAllMessageContainers();
        if (questionRecyclerView != null) {
            questionRecyclerView.setVisibility(View.GONE);
        }
        if (noQuestionsContainer != null) {
            noQuestionsContainer.setVisibility(View.VISIBLE);
        }
        setupNoQuestionsView();
    }

    private void setupHiddenQuestionsView() {
        if (hiddenQuestionsIcon != null) {
            // Set the eye-off icon
            hiddenQuestionsIcon.setImageResource(R.drawable.ic_visibility_off);
        }

        if (hiddenQuestionsMessage != null) {
            hiddenQuestionsMessage.setText("Question content is hidden by the creator");
        }
    }

    private void setupNoQuestionsView() {
        if (noQuestionsIcon != null) {
            // Set the empty/no content icon
            noQuestionsIcon.setImageResource(R.drawable.ic_empty_questions); // You'll need this drawable
        }

        if (noQuestionsMessage != null) {
            noQuestionsMessage.setText("No questions available in this quiz yet");
        }
    }

    private void setupPlayButton() {
        if (btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quizResponse.getNumberQuestion() != 0) {
                        Intent intent = new Intent(QuizzDetails.this, UI43.class);
                        intent.putExtra("userId", idUser);
                        intent.putExtra("quizId", idQuizz);
                        startActivity(intent);
                    } else {
                        Toast.makeText(QuizzDetails.this, "No question available!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    // Keep the original options menu methods for fallback
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_edit) {
            openEditQuiz();
            return true;
        } else if (itemId == R.id.menu_share) {
            shareQuiz();
            return true;
        } else if (itemId == R.id.menu_generate_qr) {
            generateQRCode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openEditQuiz() {
        Intent intent = new Intent(this, UpdateQuizActivity.class);
        intent.putExtra("quizId", idQuizz);
        intent.putExtra("userId", idUser);
        startActivityForResult(intent, UPDATE_QUIZ_REQUEST_CODE);
    }

    private void shareQuiz() {
        Toast.makeText(this, "Sharing quiz...", Toast.LENGTH_SHORT).show();
        // Implement share functionality
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this quiz: " + txtTitleQuiz.getText().toString());
        startActivity(Intent.createChooser(shareIntent, "Share Quiz"));
    }

    private void generateQRCode() {
        Toast.makeText(this, "Generating QR Code...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, UI_40_generate_qr.class);
        intent.putExtra("quizId", idQuizz);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_QUIZ_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            quizUpdated = data.getBooleanExtra("quiz_updated", false);
             if (quizUpdated) {
                Toast.makeText(this, "Quiz updated successfully", Toast.LENGTH_SHORT).show();
                reloadQuizData();
            }
        }
    }

    private void reloadQuizData() {
        Log.d("QuizzDetails", "Reloading quiz data after update");
        loadQuizData();
    }

    private void setUpInfoUser(int idAuthor) {
        Call<User> user = userApi.getInfoUserById(idAuthor);
        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User userr = response.body();
                    if (txtNameAuthor != null) {
                        txtNameAuthor.setText(userr.getFullName());
                    }
                    if (txtHandler != null) {
                        txtHandler.setText("@" + String.join("_", userr.getFullName().toLowerCase().split(" ")) + "_" + userr.getId());
                    }
                    if(profileImage != null) {
                        Picasso.get()
                                .load(userr.getAvatar())
                                .resize(48, 48)
                                .centerCrop()
                                .into(profileImage);
                    }
                } else {
                    Log.e("get info author", "failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("info author", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void setUpRecycleView() {
        if (quizResponse != null && !quizResponse.isVisibleQuizQuestion()) {
            Log.d("QuizzDetails", "Questions are hidden by creator");
            return;
        }

        Call<List<Question>> calll = questionApi.getQuestionByQuiz(idQuizz);

        calll.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Question> questionList = response.body();

                    if (questionList.isEmpty()) {
                        Log.d("QuizzDetails", "No questions found for this quiz");
                        showNoQuestionsView();
                        return;
                    }

                    hideAllMessageContainers();

                    if (questionRecyclerView == null) {
                        questionRecyclerView = findViewById(R.id.quizDetailsQuestionRecyclerView);
                    }

                    questionRecyclerView.setVisibility(View.VISIBLE);
                    questionRecyclerView.setLayoutManager(new LinearLayoutManager(QuizzDetails.this));
                    questionRecyclerView.setHasFixedSize(true);

                    questionAdapter = new QuizzDetailsQuestionAdapter(QuizzDetails.this, questionList);
                    questionRecyclerView.setAdapter(questionAdapter);
                } else {
                    Log.e("get list quest", "Failed: " + response.code());
                    // Show no questions view on API error
                    showNoQuestionsView();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.e("get list quest", "Error: " + t.getMessage());
                t.printStackTrace();
                // Show no questions view on network error
                showNoQuestionsView();
            }
        });
    }
}