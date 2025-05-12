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
import com.cmloopy.quizzi.fragment.QuestionCreate.QCBaseQuestionFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionBNVFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionEmptyPlaceHolder;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionFragmentManager;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTypeText;
import com.cmloopy.quizzi.utils.QuestionCreate.helper.QCHelper;
import com.cmloopy.quizzi.utils.QuestionCreate.manager.QCQuestionSaveManager;
import com.cmloopy.quizzi.utils.QuestionCreate.storage.QCLocalStorageUtils;

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
    private ImageButton menuButton;
    private QCQuestionBNVFragment bottomFragment;
    private List<Question> questions;
    private QuestionAPI questionAPI;
    private List<Question> questionListResponse;
    private ProgressBar loadingIndicator;
    private QCQuestionSaveManager saveManager;
    private boolean updatingQuestion = false;
    private Long quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_create);
        initializeQuizId();


        questionAPI = RetrofitClient.getQuestionApi();
        saveManager = new QCQuestionSaveManager(this, quizId);
        Log.d(TAG, "LOADING question");

        Map<String, Object> _user = QCLocalStorageUtils.getLoggedInUser(this);
//        Map<String, Object> _quiz = QCLocalStorageUtils.getLatestQuizCreatedByUser(this);


        Log.d("USER INFO", _user != null ? _user.toString() : "No user");
//        Log.d("QUIZ INFO", _quiz != null ? _quiz.toString() : "No quiz");



        questions = new ArrayList<>();
        loadQuizQuestions();

        clearButton = findViewById(R.id.clearButton);
        menuButton = findViewById(R.id.menuButton);

        loadBottomNavigationFrame();
        if (questions.isEmpty()) {
            Fragment emptyFragment = new QCQuestionEmptyPlaceHolder();
            loadQuestionTypeFrame(getSupportFragmentManager(), emptyFragment);
        }

        clearButton.setOnClickListener(v -> {
            handleBackButton();
        });

        menuButton.setOnClickListener(this::showPopupMenu);
    }

    void initializeQuizId() {
        Intent intent = getIntent();
        quizId = intent.getLongExtra("quizId", -1);
        if (quizId == -1) {
            Log.e(TAG, "Quiz ID not provided!");
            finish();
        }
        else {
            Log.e(TAG, "Provide Quiz ID successfully with quizId = " + quizId);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackButton();
    }

    private void handleBackButton() {
        saveManager.showBackConfirmationDialog(
                questions,
                () -> {
                    super.onBackPressed();
                },
                (isSuccessful, message) -> {
                    if (isSuccessful) {
                        Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                        super.onBackPressed();
                    } else {
                        // Show error and stay on screen
                        new AlertDialog.Builder(this)
                                .setTitle("Save Error")
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
        );
    }

    private void loadQuizQuestions() {
        ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading questions...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        final int MAX_RETRIES = 3;
        final AtomicInteger retryCount = new AtomicInteger(0);

        final Call<List<Question>>[] call = new Call[1];
        final Callback<List<Question>> callback = new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                Log.d(TAG, "Response received. Success: " + response.isSuccessful()
                        + ", Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Question> questionsList = response.body();
                    Log.d(TAG, "Successfully parsed questions. Count: " + questionsList.size());

                    List<Question> allQuestions = new ArrayList<>(questionsList);

                    for (Question question : allQuestions) {
                        if (question instanceof QuestionChoice) {
                            QuestionChoice choiceQuestion = (QuestionChoice) question;
                            Log.d(TAG, "Choice question: " + choiceQuestion.getContent() +
                                    ", Options: " + choiceQuestion.getChoiceOptions().size());
                        } else if (question instanceof QuestionPuzzle) {
                            QuestionPuzzle puzzleQuestion = (QuestionPuzzle) question;
                            Log.d(TAG, "Puzzle question: " + puzzleQuestion.getContent() +
                                    ", Pieces: " + puzzleQuestion.getPuzzlePieces().size());
                        } else if (question instanceof QuestionSlider) {
                            QuestionSlider sliderQuestion = (QuestionSlider) question;
                            Log.d(TAG, "Slider question: " + sliderQuestion.getContent() +
                                    ", Range: " + sliderQuestion.getMinValue() + "-" + sliderQuestion.getMaxValue());
                        } else if (question instanceof QuestionTypeText) {
                            QuestionTypeText textQuestion = (QuestionTypeText) question;
                            Log.d(TAG, "Text question: " + textQuestion.getContent() +
                                    ", Accepted answers: " + textQuestion.getAcceptedAnswers().size());
                        }
                    }

                    questions = new ArrayList<>(allQuestions);

                    saveManager.initialize(questions);

                    runOnUiThread(() -> {
                        if (bottomFragment != null) {
                            bottomFragment.setQuestions(questions);
                        }

                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        Toast.makeText(QuestionCreateActivity.this,
                                "Loaded " + questions.size() + " questions",
                                Toast.LENGTH_SHORT).show();
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

                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(QuestionCreateActivity.this,
                                "Failed to load questions: " + response.code() + " " + response.message(),
                                Toast.LENGTH_LONG).show();

                        saveManager.initialize(questions);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.e(TAG, "Network failure: " + t.getClass().getSimpleName() + ": " + t.getMessage(), t);

                if (retryCount.getAndIncrement() < MAX_RETRIES) {
                    Log.d(TAG, "Retrying... Attempt " + retryCount.get() + " of " + MAX_RETRIES);
                    new Handler().postDelayed(() -> {
                        questionAPI.getQuizQuestions(quizId).enqueue(this);
                    }, 1000);
                    return;
                }

                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                runOnUiThread(() -> {
                    Toast.makeText(QuestionCreateActivity.this,
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();

                    showRetryDialog(t.getMessage());

                    saveManager.initialize(questions);
                });
            }
        };

        call[0] = questionAPI.getQuizQuestions(quizId);
        call[0].enqueue(callback);
    }

    private void showRetryDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connection Error")
                .setMessage("Failed to load questions: " + errorMessage)
                .setPositiveButton("Retry", (dialog, which) -> {
                    loadQuizQuestions();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
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
        bottomFragment = new QCQuestionBNVFragment(questions);
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
        Log.d(TAG,  "Save hehehe: "  + String.valueOf(questions.size()));
        saveManager.showSaveConfirmationDialog2(questions);
    }

    private void handleDelete() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.question_type_frame_container);

        if (currentFragment instanceof QCBaseQuestionFragment) {
            QCBaseQuestionFragment questionFragment = (QCBaseQuestionFragment) currentFragment;
            Question currentQuestion = questionFragment.getCurrentQuestion();

            saveManager.showDeleteConfirmationDialog(
                    () -> {
                        questions.remove(currentQuestion.getPosition());
                        if (bottomFragment != null) {
                            bottomFragment.onDeleteQuestion(currentQuestion.getPosition(), currentQuestion);
                        }
                    }
            );
        } else {
            Toast.makeText(this, "No question selected to delete", Toast.LENGTH_SHORT).show();
        }    }

    @Override
    public void onClickListener(Question question) {
        QCQuestionFragmentManager fragmentManager = new QCQuestionFragmentManager(getSupportFragmentManager(), R.id.question_type_frame_container);
        fragmentManager.showQuestionFragment(question, this);
        Log.d("SHOWFRAGMENT", "Updating question at position: " + question.getPosition() + " " + question.getContent());
    }

    @Override
    public void onDeleteQuestion(int position, Question question, List<Question> questions) {
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
    public void onUpdateQuestion(int position, Question question) {
        if (updatingQuestion) {
            return;
        }

        try {
            updatingQuestion = true;

            saveManager.onQuestionUpdated(position, question);

            if (bottomFragment != null) {
                bottomFragment.updateQuestionInView(position, question);
            }
        } finally {
            updatingQuestion = false;
        }
    }

    @Override
    public void onFabAddQuestion(List<Question> _questions, QCQuestionBNVAdapter questionBottomAdapter) {

        QCHelper.showQuestionTypeBottomSheet(
                this,
                getSupportFragmentManager(),
                (questionType) -> {
                    String name = questionType.getName();
                    Question question = QCHelper.QuestionTypeMapper.createQuestionInstance(questions, name);
                    question.setQuestionType(questionType);

                    int newPosition = questions.size();
                    question.setPosition(newPosition);
                    questions.add(question);
                    _questions.add(question);
                    questionBottomAdapter.notifyItemInserted(newPosition);

                    if (saveManager != null) {
                        saveManager.onQuestionAdded(newPosition);
                    }
                    onClickListener(questions.get(questions.size() - 1));
                }
        );
    }

    @Override
    public void onDeleteQuestion(int position, Question question) {
        if (bottomFragment != null) {
            bottomFragment.onDeleteQuestion(position, question);
        }
    }
}