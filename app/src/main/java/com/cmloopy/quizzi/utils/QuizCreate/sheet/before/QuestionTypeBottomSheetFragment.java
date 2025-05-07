package com.cmloopy.quizzi.utils.QuizCreate.sheet.before;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.after.QCQuestionTypeAdapter;
import com.cmloopy.quizzi.models.QuizCreate.before.QCQuestionType;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionType;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.after.QCQuestionDataGenerator;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

public class QuestionTypeBottomSheetFragment extends BottomSheetDialogFragment {
    private QCQuestionTypeAdapter adapter;
    private OnQuestionTypeSelectedListener listener;

    public interface OnQuestionTypeSelectedListener {
        void onQuestionTypeSelected(QuestionType questionType);
    }

    public void setOnQuestionTypeSelectedListener(OnQuestionTypeSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_qc_bottom_sheet_question_type, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_question_types);

        List<QuestionType> questionTypes = QCQuestionDataGenerator.initializeQuestionTypes();

        adapter = new QCQuestionTypeAdapter(questionTypes);
        adapter.setOnQuestionTypeClickListener(questionType -> {
            if (listener != null) {
                listener.onQuestionTypeSelected(questionType);
            }
            dismiss();
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setDimAmount(0.5f); // Adjust dim amount if needed
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View parent = (View) view.getParent();
        if (parent != null) {
            parent.setBackgroundResource(R.drawable.ui_qc_bg_bottom_sheet);
        }
    }


    private List<QCQuestionType> createQuestionTypes() {
        return Arrays.asList(
                new QCQuestionType("Quiz", R.drawable.ic_70_quiz, 1),
                new QCQuestionType("True or False", R.drawable.ic_70_true_or_false, 2),
                new QCQuestionType("Puzzle", R.drawable.ic_70_puzzle, 3),
                new QCQuestionType("Type Answer", R.drawable.ic_70_type_answers, 4),
                new QCQuestionType("Quiz Audio", R.drawable.ic_70_quiz_and_audio, 5),
                new QCQuestionType("Slider", R.drawable.ic_70_silder, 6),
                new QCQuestionType("Checkbox", R.drawable.ic_70_checkbox, 7),
                new QCQuestionType("Say Word", R.drawable.ic_70_say_the_word, 8)
        );
    }
}
