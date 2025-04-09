package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.Question;
import com.cmloopy.quizzi.models.TrueFalseQuestion;
import com.cmloopy.quizzi.views.PlayGameActivity;

public class TrueFalseFragment extends Fragment {

    private TrueFalseQuestion question;
    private TextView questionTextView;
    private Button trueButton;
    private Button falseButton;

    public static TrueFalseFragment newInstance(Question question) {
        TrueFalseFragment fragment = new TrueFalseFragment();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (TrueFalseQuestion) getArguments().getSerializable("question");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_true_false, container, false);

        questionTextView = view.findViewById(R.id.question_text);
        trueButton = view.findViewById(R.id.true_button);
        falseButton = view.findViewById(R.id.false_button);

        questionTextView.setText(question.getQuestion());

        // Assume the correct answer is "False"
        trueButton.setOnClickListener(v -> {
            ((PlayGameActivity) getActivity()).onQuestionAnswered(false);
        });

        falseButton.setOnClickListener(v -> {
            ((PlayGameActivity) getActivity()).onQuestionAnswered(true);
        });

        return view;
    }
}