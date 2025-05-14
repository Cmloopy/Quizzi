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
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTypeText;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreatePuzzle;
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
    private List<QuestionCreate> questionCreates;
    private QCQuestionSaveManager saveManager;
    private boolean updatingQuestion = false; // Flag to prevent recursive calls

    private static final String SINGLE_CHOICE = "SINGLE_CHOICE";
    private static final String MULTI_CHOICE = "MULTI_CHOICE";
    private static final String PUZZLE = "PUZZLE";
    private static final String TEXT = "TEXT";

    public QCQuestionBNVFragment() {
        questionCreates = QCQuestionDataGenerator.generateQuestions(2);
    }

    public QCQuestionBNVFragment(List<QuestionCreate> questionCreates) {
        this.questionCreates = questionCreates;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Create the save manager
        saveManager = new QCQuestionSaveManager(context, 1L);
    }

    @Override
    public void onUpdateQuestion(int position, QuestionCreate questionCreate) {
        if (updatingQuestion) {
            return;
        }

        try {
            updatingQuestion = true;

            questionCreates.set(position, questionCreate);
            Log.d("QCQuestionBNVFragment", "Updating question at position: " + position);
            questionBottomAdapter.notifyItemChanged(position);

            if (listener != null) {
                listener.onUpdateQuestion(position, questionCreate);
            }
        } finally {
            updatingQuestion = false;
        }
    }

    public void updateQuestionInView(int position, QuestionCreate questionCreate) {
        if (position >= 0 && position < questionCreates.size()) {
            questionCreates.set(position, questionCreate);
            if (questionBottomAdapter != null) {
                questionBottomAdapter.notifyItemChanged(position);
            }
        }
    }

    public void addQuestion(QuestionCreate questionCreate) {
        if (isQuestionValid(questionCreate)) {
            questionCreates.add(questionCreate);
            questionBottomAdapter.notifyDataSetChanged();

            if (listener != null && getActivity() != null && saveManager != null) {
                saveManager.onQuestionAdded(questionCreate.getPosition());
            }
        } else {
            showValidationError(questionCreate);
        }
    }

    private boolean isQuestionValid(QuestionCreate questionCreate) {
        String questionType = questionCreate.getQuestionType().getName();
        if(questionCreate.getContent() == null || questionCreate.getContent().isEmpty()) return true; // Allow empty questions initially
        switch (questionType) {
            case SINGLE_CHOICE:
            case MULTI_CHOICE:
                return validateChoiceQuestion(questionCreate);
            case PUZZLE:
                return validatePuzzleQuestion(questionCreate);
            case TEXT:
                return validateTextQuestion(questionCreate);
            default:
                return true;
        }
    }

    private boolean validateChoiceQuestion(QuestionCreate questionCreate) {
        if(!(questionCreate instanceof QuestionCreateChoice)) return false;
        QuestionCreateChoice questionChoice = (QuestionCreateChoice) questionCreate;
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

    private boolean validatePuzzleQuestion(QuestionCreate questionCreate) {
        if(!(questionCreate instanceof QuestionCreatePuzzle)) return false;
        QuestionCreatePuzzle questionPuzzle = (QuestionCreatePuzzle) questionCreate;
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

    private boolean validateTextQuestion(QuestionCreate questionCreate) {
        if(!(questionCreate instanceof QuestionCreateTypeText)) return false;
        QuestionCreateTypeText questionTypeText = (QuestionCreateTypeText) questionCreate;
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

    private void showValidationError(QuestionCreate questionCreate) {
        String questionType = questionCreate.getQuestionType().getName();
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
        QuestionCreate invalidQuestionCreate = null;

        for (int i = 0; i < questionCreates.size(); i++) {
            QuestionCreate questionCreate = questionCreates.get(i);
            if (!isQuestionValid(questionCreate)) {
                allValid = false;
                invalidQuestionPosition = i;
                invalidQuestionCreate = questionCreate;
                break;
            }
        }

        if (!allValid && invalidQuestionCreate != null) {
            showValidationError(invalidQuestionCreate);
            if (listener != null) {
                listener.onClickListener(invalidQuestionCreate);
                Toast.makeText(getContext(),
                        "Please complete question " + (invalidQuestionPosition + 1) + " before adding a new one.",
                        Toast.LENGTH_LONG).show();
            }
        }

        return allValid;
    }

    @Override
    public void onDeleteQuestion(int position, QuestionCreate questionCreate) {
        questionCreates.remove(position);

        for (int i = position; i < questionCreates.size(); i++) {
            questionCreates.get(i).setPosition(i);
        }

        questionBottomAdapter.notifyDataSetChanged();

        if (listener != null) {
            listener.onDeleteQuestion(position, questionCreate, questionCreates);
        }

        if (!questionCreates.isEmpty()) {
            int newPosition = Math.min(position, questionCreates.size() - 1);
            listener.onClickListener(questionCreates.get(newPosition));
        }

        Log.d("QCQuestionBNVFragment", "Deleted question at position: " + position);
    }

    public interface OnQuestionBNVListener {
        void onClickListener(QuestionCreate questionCreate);
        void onDeleteQuestion(int position, QuestionCreate questionCreate, List<QuestionCreate> questionCreates);
        void onUpdateQuestion(int position, QuestionCreate questionCreate);
        void onFabAddQuestion(List<QuestionCreate> questionCreates, QCQuestionBNVAdapter questionBNVAdapter);
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
        questionBottomAdapter = new QCQuestionBNVAdapter(context, questionCreates, this);
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

                listener.onFabAddQuestion(questionCreates, questionBottomAdapter);

            }
        });

        if(!questionCreates.isEmpty() && listener != null) {
            listener.onClickListener(questionCreates.get(0));
        }
        return view;
    }

    @Override
    public void onQuestionClick(QuestionCreate questionCreate, int position) {
        listener.onClickListener(questionCreate);
    }

    public List<QuestionCreate> getQuestions() {
        if(questionCreates == null) questionCreates = new ArrayList<>();
        return questionCreates;
    }

    public void setQuestions(List<QuestionCreate> questionCreates) {
        this.questionCreates.clear();

        for (int i = 0; i < questionCreates.size(); i++) {
            QuestionCreate questionCreate = questionCreates.get(i);
            questionCreate.setPosition(i);
            this.questionCreates.add(questionCreate);
        }

        if (questionBottomAdapter != null) {
            questionBottomAdapter.notifyDataSetChanged();
        }

        if (!this.questionCreates.isEmpty() && listener != null) {
            listener.onClickListener(this.questionCreates.get(0));
        }

        // Initialize the save manager with the new questions
        if (saveManager != null && getActivity() != null) {
            saveManager.initialize(this.questionCreates);
        }
    }


    public void notifyBottomFragment() {
        questionBottomAdapter.notifyDataSetChanged();
    }

}