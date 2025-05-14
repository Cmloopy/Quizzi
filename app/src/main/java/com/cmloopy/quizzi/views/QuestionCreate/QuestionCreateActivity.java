package com.cmloopy.quizzi.views.QuestionCreate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCMenuAdapter;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCMenuItem;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCQuestionBNVAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuestionAPI;
import com.cmloopy.quizzi.data.api.QuestionCreate.service.QuestionService;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCBaseQuestionFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionBNVFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionEmptyPlaceHolder;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionFragmentManager;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreatePuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTypeText;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSlider;
import com.cmloopy.quizzi.utils.QuestionCreate.helper.QCHelper;
import com.cmloopy.quizzi.utils.QuestionCreate.manager.QCQuestionSaveManager;
import com.cmloopy.quizzi.utils.QuestionCreate.storage.QCLocalStorageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//base on the api i provide you (if missing some endpoint or service function implement it)
// write save question a thing that can track if one of the question in question list
// have change display a dialog to save change this will work when click on save or
// click on arrow back if change made each question will be created or updated
// with its method question type, delete you just need an id and update the question list

public class QuestionCreateActivity extends AppCompatActivity implements QCQuestionBNVFragment.OnQuestionBNVListener,
        QCBaseQuestionFragment.OnChangeListener {

    private static final String TAG = "QuestionCreateActivity";
    private ImageButton clearButton;
//    private ImageButton menuButton;
    private ImageButton saveButton;
    private ImageButton deleteButton;
    private QCQuestionBNVFragment bottomFragment;
    private List<QuestionCreate> questionCreates;
    private QuestionAPI questionAPI;
    private List<QuestionCreate> questionCreateListResponse;
    private ProgressBar loadingIndicator;
    private QCQuestionSaveManager saveManager;
    private boolean updatingQuestion = false;
    private Long quizId;
    private QuestionService saveService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_create);
        initializeQuizId();
        saveService = new QuestionService(this);

        questionAPI = RetrofitClient.getQuestionCreateApi();
        saveManager = new QCQuestionSaveManager(this, quizId);
        Log.d(TAG, "LOADING question");

        Map<String, Object> _user = QCLocalStorageUtils.getLoggedInUser(this);
//        Map<String, Object> _quiz = QCLocalStorageUtils.getLatestQuizCreatedByUser(this);


        Log.d("USER INFO", _user != null ? _user.toString() : "No user");
//        Log.d("QUIZ INFO", _quiz != null ? _quiz.toString() : "No quiz");



        questionCreates = new ArrayList<>();
        loadQuizQuestions();

        clearButton = findViewById(R.id.clearButton);
//        menuButton = findViewById(R.id.menuButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        loadBottomNavigationFrame();
        if (questionCreates.isEmpty()) {
            Fragment emptyFragment = new QCQuestionEmptyPlaceHolder();
            loadQuestionTypeFrame(getSupportFragmentManager(), emptyFragment);
        }

        clearButton.setOnClickListener(v -> {
            handleBackButton();
        });

        saveButton.setOnClickListener(v -> {
            handleSave();
        });

        deleteButton.setOnClickListener(v -> {
            handleDelete();
        });

//        menuButton.setOnClickListener(this::showPopupMenu);
    }

    void initializeQuizId() {
        Intent intent = getIntent();
        quizId = intent.getLongExtra("quizId", -1);
        Log.d(TAG, "Initialize Quiz Id");

        if (quizId == -1) {
            Log.d(TAG, "Quiz ID not provided!");
            finish();
        }
        else {
            Log.d(TAG, "Provide Quiz ID successfully with quizId = " + quizId);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void handleBackButton() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Save your dialog before leaving ?")
                .setNegativeButton("No", (dialog, which) -> onBackPressed())
                .setPositiveButton("Save", (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Saving");
                    progressDialog.setMessage("Saving your works...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    saveService.saveAllQuestionsWithFullReset(questionCreates, quizId, (isSuccessful, message) -> {
                        progressDialog.dismiss();
                        if (isSuccessful) {
                            Toast.makeText(this, "Questions saved successfully", Toast.LENGTH_SHORT).show();
                            runOnUiThread(this::onBackPressed);
                        } else {
                            runOnUiThread(() -> new AlertDialog.Builder(this)
                                    .setTitle("Save Error")
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show());
                        }
                    });
                })
                .show();
    }

    private void loadQuizQuestions() {
        ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading questions...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        final int MAX_RETRIES = 3;
        final AtomicInteger retryCount = new AtomicInteger(0);

        final Runnable onLoadingComplete = () -> {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }

            saveManager.initialize(questionCreates);

            if (bottomFragment != null) {
                bottomFragment.setQuestions(questionCreates);
                bottomFragment.notifyBottomFragment();
            }

            if (questionCreates.isEmpty()) {
                Fragment emptyFragment = new QCQuestionEmptyPlaceHolder();
                loadQuestionTypeFrame(getSupportFragmentManager(), emptyFragment);
            } else {
            }
        };

        final Callback<List<QuestionCreate>> callback = new Callback<List<QuestionCreate>>() {
            @Override
            public void onResponse(Call<List<QuestionCreate>> call, Response<List<QuestionCreate>> response) {
                Log.d(TAG, "Response received. Success: " + response.isSuccessful()
                        + ", Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<QuestionCreate> questionsList = response.body();
                    Log.d(TAG, "Successfully parsed questions. Count: " + questionsList.size());

                    for (QuestionCreate question : questionsList) {
                        Log.d(TAG, "Before Question class: " + question.getClass().getName());
                        Log.d(TAG, "Before Question type: " + question.getQuestionType().getName());
                        if (question instanceof QuestionCreateChoice) {
                        QuestionCreateChoice choiceQuestion = (QuestionCreateChoice) question;
                            Log.d(TAG, "Choice question: " + choiceQuestion.getContent() +
                                    ", Options: " + choiceQuestion.getChoiceOptions().size());
                        } else if (question instanceof QuestionCreatePuzzle) {
                            QuestionCreatePuzzle puzzleQuestion = (QuestionCreatePuzzle) question;
                            Log.d(TAG, "Puzzle question: " + puzzleQuestion.getContent() +
                                    ", Pieces: " + puzzleQuestion.getPuzzlePieces().size());
                        } else if (question instanceof QuestionCreateSlider) {
                            QuestionCreateSlider sliderQuestion = (QuestionCreateSlider) question;
                            Log.d(TAG, "Slider question: " + sliderQuestion.getContent() +
                                    ", Range: " + sliderQuestion.getMinValue() + "-" + sliderQuestion.getMaxValue());
                        } else if (question instanceof QuestionCreateTypeText) {
                            QuestionCreateTypeText textQuestion = (QuestionCreateTypeText) question;
                            Log.d(TAG, "Text question: " + textQuestion.getContent() +
                                    ", Accepted answers: " + textQuestion.getAcceptedAnswers().size());
                        }

                        Log.d(TAG, "After Question class: " + question.getClass().getName());
                        Log.d(TAG, "Question type: " + question.getQuestionType());
                    }

                    // Update the questions list
                    questionCreates = new ArrayList<>(questionsList);

                    runOnUiThread(() -> {
                        Toast.makeText(QuestionCreateActivity.this,
                                "Loaded " + questionCreates.size() + " questions",
                                Toast.LENGTH_SHORT).show();

                        // Call the completion callback
                        onLoadingComplete.run();
                    });
                } else {
                    Log.e(TAG, "Error response: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to read error body", e);
                    }
                    Log.e(TAG, "Message: " + response.message());

                    if (retryCount.getAndIncrement() < MAX_RETRIES) {
                        Log.d(TAG, "Retrying... Attempt " + retryCount.get() + " of " + MAX_RETRIES);
                        new Handler().postDelayed(() -> {
                            questionAPI.getQuizQuestions(quizId).enqueue(this);
                        }, 1000);
                        return;
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(QuestionCreateActivity.this,
                                "Failed to load questions: " + response.code() + " " + response.message(),
                                Toast.LENGTH_LONG).show();

                        // Even on failure, we need to proceed with empty questions list
                        onLoadingComplete.run();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<QuestionCreate>> call, Throwable t) {
                Log.e(TAG, "Network failure: " + t.getClass().getSimpleName() + ": " + t.getMessage(), t);

                if (retryCount.getAndIncrement() < MAX_RETRIES) {
                    Log.d(TAG, "Retrying... Attempt " + retryCount.get() + " of " + MAX_RETRIES);
                    new Handler().postDelayed(() -> {
                        questionAPI.getQuizQuestions(quizId).enqueue(this);
                    }, 1000);
                    return;
                }

                runOnUiThread(() -> {
                    showRetryDialog(t.getMessage(), () -> {
                        loadQuizQuestions();
                    }, () -> {
                        onLoadingComplete.run();
                    });
                });
            }
        };

        questionAPI.getQuizQuestions(quizId).enqueue(callback);
    }

    private void showRetryDialog(String errorMessage, Runnable onRetry, Runnable onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connection Error")
                .setMessage("Failed to load questions: " + errorMessage)
                .setPositiveButton("Retry", (dialog, which) -> {
                    if (onRetry != null) onRetry.run();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    if (onCancel != null) onCancel.run();
                })
                .setCancelable(false)
                .show();
    }


    private static void loadQuestionTypeFrame(
            @NonNull FragmentManager fragmentManager,
            @NonNull Fragment fragment
    ) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.question_type_frame_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void loadBottomNavigationFrame() {
        bottomFragment = new QCQuestionBNVFragment(questionCreates);
        bottomFragment.setListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.bottom_navigation_frame_container, bottomFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void showPopupMenu(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.ui_qc_custom_menu, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setAnimationStyle(1);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(10);
        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<QCMenuItem> menuItems = Arrays.asList(
                new QCMenuItem("Preview", R.drawable.ic_78_preview, false),
                new QCMenuItem("Duplicate Question", R.drawable.ic_78_duplicate_question, false),
                new QCMenuItem("Save", R.drawable.ic_78_save, false),
                new QCMenuItem("Delete", R.drawable.ic_78_delete, true) // Red delete
        );

        QCMenuAdapter adapter = new QCMenuAdapter(menuItems, item -> {
            popupWindow.dismiss();
            handleMenuClick(item);
        });

        recyclerView.setAdapter(adapter);

        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private void handleMenuClick(QCMenuItem item) {
        switch (item.getTitle()) {
            case "Preview":
                handlePreview();
                break;
            case "Duplicate Question":
                handleDuplicate();
                break;
            case "Save":
                handleSave();
                break;
            case "Delete":
                handleDelete();
                break;
        }
    }

    private void handlePreview() {
        Toast.makeText(this, "Preview", Toast.LENGTH_SHORT).show();
        // TODO: Implement preview logic
    }

    private void handleDuplicate() {
        Toast.makeText(this, "Duplicate Question", Toast.LENGTH_SHORT).show();
    }

    private void handleSave() {
//        saveManager.showSaveConfirmationDialog2(questions);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving");
        progressDialog.setMessage("Saving your works...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        saveService.saveAllQuestionsWithFullReset(questionCreates, quizId, (isSuccessful, message) -> {
            progressDialog.dismiss();
//
            if (isSuccessful) {
                Toast.makeText(this, "Questions saved successfully", Toast.LENGTH_SHORT).show();
                saveService.initializeChangeTracker(questionCreates);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Save Error")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void handleDelete() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.question_type_frame_container);

        if (currentFragment instanceof QCBaseQuestionFragment) {
            QCBaseQuestionFragment questionFragment = (QCBaseQuestionFragment) currentFragment;
            QuestionCreate currentQuestionCreate = questionFragment.getCurrentQuestion();

            saveManager.showDeleteConfirmationDialog(
                    () -> {
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("Deleting question...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();


                        saveService.deleteQuestionById(currentQuestionCreate.getId(), new QuestionService.SaveOperationListener() {
                            @Override
                            public void onSaveComplete(boolean isSuccessful, String message) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                if (isSuccessful) {

                                } else {
//                                    // Show error message
//                                    runOnUiThread(() -> {
//                                        Toast.makeText(QuestionCreateActivity.this,
//                                                "Failed to delete question: " + message,
//                                                Toast.LENGTH_LONG).show();
//                                    });
                                }


                                questionCreates.remove(currentQuestionCreate.getPosition());
                                if (bottomFragment != null) {
                                    bottomFragment.onDeleteQuestion(currentQuestionCreate.getPosition(), currentQuestionCreate);
                                }
                            }
                        });


                    }
            );
        } else {
            Toast.makeText(this, "No question selected to delete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickListener(QuestionCreate questionCreate) {
        QCQuestionFragmentManager fragmentManager = new QCQuestionFragmentManager(getSupportFragmentManager(), R.id.question_type_frame_container);
        fragmentManager.showQuestionFragment(questionCreate, this);
        Log.d("SHOWFRAGMENT", "Updating question at position: " + questionCreate.getPosition() + " " + questionCreate.getContent());
    }

    @Override
    public void onDeleteQuestion(int position, QuestionCreate questionCreate, List<QuestionCreate> questionCreates) {
        Log.d("QuizCreateActivity", "Handling deletion of question at position: " + position);

        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.question_type_frame_container))
                .commit();

        // Register the question deletion with the save manager
        saveManager.onQuestionDeleted(position);

        Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show();

        if (bottomFragment.getQuestions().isEmpty()) {
//            QCHelper.showQuestionTypeBottomSheet(
//                    this,
//                    getSupportFragmentManager(),
//                    questionType -> {
//                        String name = questionType.getName();
//                        List<Question> _questions = bottomFragment.getQuestions();
//                        Question _question = QCHelper.QuestionTypeMapper.createQuestionInstance(_questions, name);
//                        _question.setQuestionType(questionType);
//                        int newPosition = _questions.size();
//                        _question.setPosition(newPosition);
//                        bottomFragment.addQuestion(_question);
//
//                        // Register the new question with the save manager
//                        saveManager.onQuestionAdded(newPosition);
//
//                        Log.d("QUIZCREATEACTIVITY", "NEW ADDED " + _questions.size());
//
//                        QCQuestionFragmentManager fragmentManager = new QCQuestionFragmentManager(
//                                getSupportFragmentManager(),
//                                R.id.question_type_frame_container);
//                        fragmentManager.showQuestionFragment(_question, this);
//                    }
//            );
            Fragment emptyFragment = new QCQuestionEmptyPlaceHolder();
            loadQuestionTypeFrame(getSupportFragmentManager(), emptyFragment);
        }
    }

    @Override
    public void onUpdateQuestion(int position, QuestionCreate questionCreate) {
        if (updatingQuestion) {
            return;
        }

        try {
            updatingQuestion = true;

            saveManager.onQuestionUpdated(position, questionCreate);

            if (bottomFragment != null) {
                bottomFragment.updateQuestionInView(position, questionCreate);
            }
        } finally {
            updatingQuestion = false;
        }
    }

    @Override
    public void onFabAddQuestion(List<QuestionCreate> _questionCreates, QCQuestionBNVAdapter questionBottomAdapter) {

        QCHelper.showQuestionTypeBottomSheet(
                this,
                getSupportFragmentManager(),
                (questionType) -> {
                    String name = questionType.getName();
                    QuestionCreate questionCreate = QCHelper.QuestionTypeMapper.createQuestionInstance(questionCreates, name);
                    questionCreate.setQuestionType(questionType);

                    int newPosition = questionCreates.size();
                    questionCreate.setPosition(newPosition);
                    questionCreates.add(questionCreate);
                    _questionCreates.add(questionCreate);
                    questionBottomAdapter.notifyItemInserted(newPosition);

                    if (saveManager != null) {
                        saveManager.onQuestionAdded(newPosition);
                    }
                    onClickListener(questionCreates.get(questionCreates.size() - 1));
                }
        );
    }

    @Override
    public void onDeleteQuestion(int position, QuestionCreate questionCreate) {
        if (bottomFragment != null) {
            bottomFragment.onDeleteQuestion(position, questionCreate);
        }
    }
}