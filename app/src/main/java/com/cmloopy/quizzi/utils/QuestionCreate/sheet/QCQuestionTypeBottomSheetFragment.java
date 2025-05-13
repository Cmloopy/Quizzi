package com.cmloopy.quizzi.utils.QuestionCreate.sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuestionCreate.QCQuestionTypeAdapter;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionType;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCQuestionDataGenerator;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class QCQuestionTypeBottomSheetFragment extends BottomSheetDialogFragment {
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
}
