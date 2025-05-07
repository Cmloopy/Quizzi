package com.cmloopy.quizzi.fragment;

    import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

    import com.cmloopy.quizzi.R;
    import com.cmloopy.quizzi.models.MultiChoiceQuestion;
    import com.cmloopy.quizzi.models.Question;
    import com.cmloopy.quizzi.views.PlayGameActivity;

public class MultipleChoiceFragment extends Fragment {

        private MultiChoiceQuestion question;
        private TextView questionTextView;
        private RadioGroup optionsRadioGroup;

        public static MultipleChoiceFragment newInstance(Question question) {
            MultipleChoiceFragment fragment = new MultipleChoiceFragment();
            Bundle args = new Bundle();
            args.putSerializable("question", question);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                question = (MultiChoiceQuestion) getArguments().getSerializable("question");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_multiple_choice, container, false);

            questionTextView = view.findViewById(R.id.question_text);
            optionsRadioGroup = view.findViewById(R.id.options_radio_group);

            questionTextView.setText(question.getQuestion());

            // Populate options (in a real app, you would get these from the question object)
            String[] options = {"Paris", "London", "Berlin", "Rome"};
            int correctAnswerIndex = 0; // Assuming "Paris" is correct

            for (int i = 0; i < options.length; i++) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(options[i]);
                radioButton.setId(View.generateViewId());
                optionsRadioGroup.addView(radioButton);

                final int optionIndex = i;
                radioButton.setOnClickListener(v -> {
                    boolean isCorrect = (optionIndex == correctAnswerIndex);
                    ((PlayGameActivity) getActivity()).onQuestionAnswered(isCorrect);
                });
            }

            return view;
        }
    }
