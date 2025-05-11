package com.cmloopy.quizzi.fragment.QuestionCreate;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCQuestionBNVAdapter;
import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.TypeTextOption;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTypeText;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCHelper;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCQuestionDataGenerator;
import com.cmloopy.quizzi.utils.QuestionCreate.manager.QCQuestionSaveManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QCQuestionBNVFragment extends Fragment
        implements QCQuestionBNVAdapter.OnQuestionClickListener,
        QCBaseQuestionFragment.OnChangeListener {
    private RecyclerView questionRecyclerView;
    private QCQuestionBNVAdapter questionBottomAdapter;
    private HorizontalScrollView questionScrollView;
    private FloatingActionButton fabAdd;

    private OnQuestionBNVListener listener;
    private List<Question> questions;
    private QCQuestionSaveManager saveManager;
    private boolean updatingQuestion = false; // Flag to prevent recursive calls

    private static final String SINGLE_CHOICE = "SINGLE_CHOICE";
    private static final String MULTI_CHOICE = "MULTI_CHOICE";
    private static final String PUZZLE = "PUZZLE";
    private static final String TEXT = "TEXT";

    public QCQuestionBNVFragment() {
        questions = QCQuestionDataGenerator.generateQuestions(2);
    }

    public QCQuestionBNVFragment(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Create the save manager
        saveManager = new QCQuestionSaveManager(context, 1L);
    }

    @Override
    public void onUpdateQuestion(int position, Question question) {
        if (updatingQuestion) {
            return;
        }

        try {
            updatingQuestion = true;

            questions.set(position, question);
            Log.d("QCQuestionBNVFragment", "Updating question at position: " + position);
            questionBottomAdapter.notifyItemChanged(position);

            if (listener != null) {
                listener.onUpdateQuestion(position, question);
            }
        } finally {
            updatingQuestion = false;
        }
    }

    public void updateQuestionInView(int position, Question question) {
        if (position >= 0 && position < questions.size()) {
            questions.set(position, question);
            if (questionBottomAdapter != null) {
                questionBottomAdapter.notifyItemChanged(position);
            }
        }
    }

    public void addQuestion(Question question) {
        if (isQuestionValid(question)) {
            questions.add(question);
            questionBottomAdapter.notifyDataSetChanged();

            if (listener != null && getActivity() != null && saveManager != null) {
                saveManager.onQuestionAdded(question.getPosition());
            }
        } else {
            showValidationError(question);
        }
    }

    private boolean isQuestionValid(Question question) {
        String questionType = question.getQuestionType().getName();
        if(question.getContent() == null || question.getContent().isEmpty()) return true; // Allow empty questions initially
        switch (questionType) {
            case SINGLE_CHOICE:
            case MULTI_CHOICE:
                return validateChoiceQuestion(question);
            case PUZZLE:
                return validatePuzzleQuestion(question);
            case TEXT:
                return validateTextQuestion(question);
            default:
                return true;
        }
    }

    private boolean validateChoiceQuestion(Question question) {
        if(!(question instanceof QuestionChoice)) return false;
        QuestionChoice questionChoice = (QuestionChoice) question;
        if (questionChoice.getChoiceOptions() == null || questionChoice.getChoiceOptions().size() < 4) {
            return false;
        }

        for (ChoiceOption option : questionChoice.getChoiceOptions()) {
            if (option == null || option.getText() == null || option.getText().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private boolean validatePuzzleQuestion(Question question) {
        if(!(question instanceof QuestionPuzzle)) return false;
        QuestionPuzzle questionPuzzle = (QuestionPuzzle) question;
        if (questionPuzzle.getPuzzlePieces() == null || questionPuzzle.getPuzzlePieces().size() < 4) {
            return false;
        }

        for (PuzzleOption piece : questionPuzzle.getPuzzlePieces()) {
            if (piece == null || piece.getText() == null || piece.getText().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private boolean validateTextQuestion(Question question) {
        if(!(question instanceof QuestionTypeText)) return false;
        QuestionTypeText questionTypeText = (QuestionTypeText) question;
        if (questionTypeText.getAcceptedAnswers() == null || questionTypeText.getAcceptedAnswers().isEmpty()) {
            return false;
        }

        boolean hasValidAnswer = false;
        for (TypeTextOption answer : questionTypeText.getAcceptedAnswers()) {
            if (answer != null && answer.getText() != null && !answer.getText().trim().isEmpty()) {
                hasValidAnswer = true;
                break;
            }
        }

        return hasValidAnswer;
    }

    private void showValidationError(Question question) {
        String questionType = question.getQuestionType().getName();
        String errorMessage;

        switch (questionType) {
            case SINGLE_CHOICE:
            case MULTI_CHOICE:
                errorMessage = "Choice questions require 4 non-empty options";
                break;
            case PUZZLE:
                errorMessage = "Puzzle questions require 4 non-empty puzzle pieces";
                break;
            case TEXT:
                errorMessage = "Text questions require at least one accepted answer";
                break;
            default:
                errorMessage = "Invalid question configuration";
        }

        if (getContext() != null) {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateAllQuestions() {
        boolean allValid = true;
        int invalidQuestionPosition = -1;
        Question invalidQuestion = null;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (!isQuestionValid(question)) {
                allValid = false;
                invalidQuestionPosition = i;
                invalidQuestion = question;
                break;
            }
        }

        if (!allValid && invalidQuestion != null) {
            showValidationError(invalidQuestion);
            if (listener != null) {
                listener.onClickListener(invalidQuestion);
                Toast.makeText(getContext(),
                        "Please complete question " + (invalidQuestionPosition + 1) + " before adding a new one.",
                        Toast.LENGTH_LONG).show();
            }
        }

        return allValid;
    }

    @Override
    public void onDeleteQuestion(int position, Question question) {
        questions.remove(position);

        for (int i = position; i < questions.size(); i++) {
            questions.get(i).setPosition(i);
        }

        questionBottomAdapter.notifyDataSetChanged();

        if (listener != null) {
            listener.onDeleteQuestion(position, question, questions);
        }

        if (!questions.isEmpty()) {
            int newPosition = Math.min(position, questions.size() - 1);
            listener.onClickListener(questions.get(newPosition));
        }

        Log.d("QCQuestionBNVFragment", "Deleted question at position: " + position);
    }

    public interface OnQuestionBNVListener {
        void onClickListener(Question question);
        void onDeleteQuestion(int position, Question question, List<Question> questions);
        void onUpdateQuestion(int position, Question question);
        void onFabAddQuestion(List<Question> questions, QCQuestionBNVAdapter questionBNVAdapter);
    }

    public void setListener(OnQuestionBNVListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_qc_bnv, container, false);
        questionRecyclerView = view.findViewById(R.id.question_recycler_view);
        questionScrollView = view.findViewById(R.id.question_scroll_view);
        fabAdd = view.findViewById(R.id.fab_add);
        Context context = getContext();
        questionBottomAdapter = new QCQuestionBNVAdapter(context, questions, this);
        questionBottomAdapter.setListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        questionRecyclerView.setLayoutManager(linearLayoutManager);
        questionRecyclerView.setAdapter(questionBottomAdapter);
        questionRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = 20;

                outRect.right = spacing;
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!validateAllQuestions()) {
//                    return;
//                }

                listener.onFabAddQuestion(questions, questionBottomAdapter);

            }
        });

        if(!questions.isEmpty() && listener != null) {
            listener.onClickListener(questions.get(0));
        }
        return view;
    }

    @Override
    public void onQuestionClick(Question question, int position) {
        listener.onClickListener(question);
    }

    public List<Question> getQuestions() {
        if(questions == null) questions = new ArrayList<>();
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions.clear();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            question.setPosition(i);
            this.questions.add(question);
        }

        if (questionBottomAdapter != null) {
            questionBottomAdapter.notifyDataSetChanged();
        }

        if (!this.questions.isEmpty() && listener != null) {
            listener.onClickListener(this.questions.get(0));
        }

        // Initialize the save manager with the new questions
        if (saveManager != null && getActivity() != null) {
            saveManager.initialize(this.questions);
        }
    }
}