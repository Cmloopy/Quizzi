package com.cmloopy.quizzi.fragment.QuizCreate.after;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.after.Option.TypeTextOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QCQuestionTypeTextFieldFragment extends Fragment {
    private EditText answerEditText;
    private Button addAnswerButton;
    private OnAnswerAddedListener listener;

    private List<TypeTextOption> answers;

    List<Integer> backgroundColors = Arrays.asList(
            R.drawable.ui_qc_bg_elevation_blue,
            R.drawable.ui_qc_bg_elevation_red,
            R.drawable.ui_qc_bg_elevation_orange,
            R.drawable.ui_qc_bg_elevation_green
    );

    public interface OnAnswerAddedListener {
        void onAnswerAdded(TypeTextOption answer);
    }

    public void setOnAnswerAddedListener(OnAnswerAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_type_text_field, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        answerEditText = view.findViewById(R.id.et_answer);
        addAnswerButton = view.findViewById(R.id.btn_add_answer);

        addAnswerButton.setOnClickListener(v -> {
            String answerText = answerEditText.getText().toString().trim();
            if (!answerText.isEmpty()) {
                // Cycle through background colors
                int backgroundColor = backgroundColors.get(answers.size() % 4);

                TypeTextOption newAnswer = new TypeTextOption(
                        answers.size(),
                        "",
                        answerText,
                        "",
                        "");

                if (listener != null) {
                    listener.onAnswerAdded(newAnswer);

                    // Clear the edit text
                    answerEditText.setText("");

                    Toast.makeText(getContext(), "Answer added: ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter an answer", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAnswers(List<TypeTextOption> answers) {
        if(answers == null) answers = new ArrayList<>();
        this.answers = answers;
    }
}