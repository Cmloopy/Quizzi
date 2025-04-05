package com.cmloopy.quizzi.views.QuizCreate.after;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.QCMenuAdapter;
import com.cmloopy.quizzi.adapter.QuizCreate.QCMenuItem;
import com.cmloopy.quizzi.fragment.QuizCreate.after.QCBaseResponseFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.after.QCQuestionBNVFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.after.QCQuestionFragmentManager;
import com.cmloopy.quizzi.models.QuizCreate.after.Question;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCHelper;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.after.QCQuestionDataGenerator;

import java.util.Arrays;
import java.util.List;

public class QuizCreateActivity extends AppCompatActivity implements QCQuestionBNVFragment.OnQuestionBNVListener{

    private ImageButton clearButton;
    private ImageButton menuButton;
    private QCQuestionBNVFragment bottomFragment;
    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_create);
        questions = QCQuestionDataGenerator.generateQuestions(2);

        clearButton = findViewById(R.id.clearButton);
        menuButton = findViewById(R.id.menuButton);

        loadBottomNavigationFrame();
        if (bottomFragment.getQuestions().isEmpty()) {
            QCHelper.showQuestionTypeBottomSheet2(
                    this,
                    getSupportFragmentManager(),
                    questionType -> {
                        QCHelper.navigateToQuestionCreation2(getSupportFragmentManager(), questionType);
                    }
            );
        }
        clearButton.setOnClickListener(v -> {
            Toast.makeText(this, "Back/Clear", Toast.LENGTH_SHORT).show();
        });

        menuButton.setOnClickListener(this::showPopupMenu);
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
        // TODO: Implement duplicate logic
    }

    private void handleSave() {
        Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
        // TODO: Implement save logic
    }

    private void handleDelete() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.question_type_frame_container);

        if (currentFragment instanceof QCBaseResponseFragment) {
            QCBaseResponseFragment questionFragment = (QCBaseResponseFragment) currentFragment;
            Question currentQuestion = questionFragment.getCurrentQuestion();

            if (bottomFragment != null) {
                bottomFragment.onDeleteQuestion(currentQuestion.getPosition(), currentQuestion);
            }
        } else {
            Toast.makeText(this, "No question selected to delete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickListener(Question question) {
        QCQuestionFragmentManager fragmentManager = new QCQuestionFragmentManager(getSupportFragmentManager(), R.id.question_type_frame_container);
        fragmentManager.showQuestionFragment(question, bottomFragment);
        Log.d("SHOWFRAGMENT", "Updating question at position: " + question.getPosition() + " " + question.getContent());

    }

    @Override
    public void onDeleteQuestion(int position, Question question, List<Question> questions) {
        Log.d("QuizCreateActivity", "Handling deletion of question at position: " + position);

        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.question_type_frame_container))
                .commit();

        Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show();

        if (bottomFragment.getQuestions().isEmpty()) {
            QCHelper.showQuestionTypeBottomSheet2(
                    this,
                    getSupportFragmentManager(),
                    questionType -> {
                        String name = questionType.getName();
                        List<Question> _questions = bottomFragment.getQuestions();
                        Question _question = QCHelper.QuestionTypeMapper.createQuestionInstance2(_questions, name);
                        _question.setQuestionType(questionType);
                        int newPosition = _questions.size();
                        _question.setPosition(newPosition);
                        bottomFragment.addQuestion(_question);
                        Log.d("QUIZCREATEACTIVITY", "NEW ADDED " + _questions.size());

                        QCQuestionFragmentManager fragmentManager = new QCQuestionFragmentManager(
                                getSupportFragmentManager(),
                                R.id.question_type_frame_container);
                        fragmentManager.showQuestionFragment(_question, bottomFragment);

//                        QCHelper.navigateToQuestionCreation(getSupportFragmentManager(), questionType);
                    }
            );
        }
    }
}