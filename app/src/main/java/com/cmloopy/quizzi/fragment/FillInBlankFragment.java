package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.Question;
import com.cmloopy.quizzi.views.PlayGameActivity;

public class FillInBlankFragment extends Fragment {

    private Question question;
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitButton;

    public static FillInBlankFragment newInstance(Question question) {
        FillInBlankFragment fragment = new FillInBlankFragment();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("question");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_in_blank, container, false);

        questionTextView = view.findViewById(R.id.question_text);
        answerEditText = view.findViewById(R.id.answer_edit_text);
        submitButton = view.findViewById(R.id.submit_button);

        questionTextView.setText(question.getQuestion());

        // Giả sử đáp án đúng là "Jupiter"
        submitButton.setOnClickListener(v -> {
            String answer = answerEditText.getText().toString().trim();
            boolean isCorrect = answer.equalsIgnoreCase("Jupiter");
            ((PlayGameActivity) getActivity()).onQuestionAnswered(isCorrect);
        });

        // Bật nút Submit khi người dùng nhập văn bản
        answerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitButton.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }
}